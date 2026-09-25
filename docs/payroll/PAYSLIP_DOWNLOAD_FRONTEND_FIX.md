# Payslip Download — Frontend Gating Fix

**Symptom:** `GET /api/v1/payroll/72/payslip` returns **400** and the browser console only shows
`Failed to load resource: the server responded with a status of 400`.

**Status:** backend is correct and unchanged. The change is entirely in `HRMS/`.

---

## 1. Root cause

`PayslipPdfServiceImpl.generatePayslip()` only produces a PDF when the payroll is
**`APPROVED` or `PAID`** *and* `active = true`. Anything else is a deliberate `400`:

```java
// PayslipPdfServiceImpl
if (payroll.getStatus() != PayrollStatus.APPROVED
        && payroll.getStatus() != PayrollStatus.PAID) {
    throw new BadRequestException(
        "Payslip can only be generated for APPROVED or PAID payrolls. "
            + "Current status: " + payroll.getStatus(),
        ErrorCode.BAD_REQUEST);
}
```

Verified against the dev database — payroll `72` is `GENERATED` and was never approved:

```
id=72  num=PR-202610-0004  version=1  active=true  status=GENERATED  month=2026-10-01  employee_id=7
```

| `status` | `active` | Result |
|----------|----------|--------|
| `APPROVED` / `PAID` | `true` | `200` PDF |
| `DRAFT`, `GENERATED`, `CANCELLED` | `true` | **`400` not approved yet** |
| any | `false` | **`400` superseded/cancelled version** |

## 2. Where the frontend is already correct — and where it is not

`src/pages/payroll/PayrollRunsPanel.jsx` (HR admin) **already gates** the action:

```js
// line 347
const canDownload = (item) => ["APPROVED", "PAID"].includes(item.status);
```

`src/pages/payroll/EmployeePayslipView.jsx` (employee self-service) has **no gate at all**.
It loads `GET /payroll/employee/{employeeId}`, which returns *every version of every month*
including `GENERATED` and `SUPERSEDED`, and renders a download button for all of them:

* line ~201 — hero "Payslip" button, `disabled={downloading}` only
* line ~285 — "Payslip History" row button, same
* line ~109 — `handleDownload(item)` fires the request with no status check

So clicking any unapproved or superseded row produces exactly the 400 in the report.

---

## 3. Patch A — `HRMS/src/services/payrollService.js`

Replace `downloadPayslip` (currently lines **82–91**) with the version below, and add the two
helpers above it. The shared `canDownloadPayslip` is what the pages should use, so the rule
cannot drift between screens.

```js
/**
 * A payslip PDF only exists for a payroll that is APPROVED or PAID and still
 * active. Calling the endpoint in any other state returns HTTP 400, so callers
 * must gate the action instead of letting the request fail.
 */
export function canDownloadPayslip(payroll) {
  return Boolean(payroll)
    && payroll.active === true
    && ["APPROVED", "PAID"].includes(payroll.status);
}

/**
 * Failed blob responses arrive as a Blob, so the backend's message has to be
 * read out of it before it can be shown to the user.
 */
async function blobErrorMessage(error, fallback) {
  const data = error?.response?.data;
  if (data instanceof Blob) {
    try {
      const parsed = JSON.parse(await data.text());
      if (parsed?.message) return parsed.message;
    } catch {
      /* response body was not JSON */
    }
  } else if (data?.message) {
    return data.message;
  }
  return error?.message || fallback;
}

export async function downloadPayslip(id) {
  try {
    const response = await api.get(`/payroll/${id}/payslip`, { responseType: "blob" });
    const disposition = response.headers?.["content-disposition"] || "";
    const match = disposition.match(/filename="?([^"]+)"?/);
    return {
      blob: response.data,
      filename: match?.[1] || `payslip-${id}.pdf`,
    };
  } catch (error) {
    throw new Error(await blobErrorMessage(error, "Failed to download payslip."));
  }
}
```

> Without `blobErrorMessage`, `err.message` is only `"Request failed with status code 400"` —
> which is why the UI currently shows no useful reason. With it, the user sees
> *"Payslip can only be generated for APPROVED or PAID payrolls. Current status: GENERATED"*.

## 4. Patch B — `HRMS/src/pages/payroll/EmployeePayslipView.jsx`

**B1 — import the helper** (line 6–13 import block):

```js
import {
  canDownloadPayslip,     // <— add
  downloadPayslip,
  formatINR,
  getPaymentDetails,
  getPayrollByEmployee,
  payrollMonthLabel,
  triggerBlobDownload,
} from "../../services/payrollService";
```

**B2 — refuse the request in `handleDownload`** (currently lines 109–112):

```js
  async function handleDownload(item) {
    if (!item || downloading) return;
    if (!canDownloadPayslip(item)) {
      setError(
        `Payslip for ${payrollMonthLabel(item.payrollMonth)} is not available yet — `
        + `status is ${item.status}. Payslips can be downloaded once the payroll is approved.`,
      );
      return;
    }
    setDownloading(true);
    // ...unchanged
```

**B3 — hero button** (currently line 200–204):

```jsx
                <button
                  className="btn btn-primary"
                  onClick={() => handleDownload(selected)}
                  disabled={downloading || !canDownloadPayslip(selected)}
                  title={canDownloadPayslip(selected)
                    ? "Download payslip"
                    : "Available once the payroll is approved"}
                >
                  <Download size={15} /> {downloading ? "Downloading…" : "Payslip"}
                </button>
```

**B4 — Payslip History row button** (currently lines 284–287):

```jsx
                        <button
                          className="btn btn-ghost btn-sm"
                          onClick={() => handleDownload(item)}
                          disabled={downloading || !canDownloadPayslip(item)}
                          title={canDownloadPayslip(item)
                            ? `Download ${item.payrollNumber}`
                            : `${item.status} — available once approved`}
                        >
                          <Download size={14} /> PDF
                        </button>
```

Disabling rather than hiding is deliberate: the history table is a record of what exists, so a
greyed PDF button with a reason is more informative than a silently missing column.

## 5. Patch C — `HRMS/src/pages/payroll/PayrollRunsPanel.jsx` (optional, recommended)

Line 347 is correct but ignores `active`. Point it at the shared helper so both screens use one rule:

```js
  // APPROVED/PAID and still the active version — payslips 400 otherwise.
  const canDownload = canDownloadPayslip;
```

add `canDownloadPayslip` to the `payrollService` import block (line 21–30).

---

## 6. Verification

1. **Unapproved payroll (id 72, `GENERATED`)** — button disabled, tooltip explains why, and the
   network tab shows **no** `/payslip` request at all.
2. **Superseded version (e.g. id 89, `SUPERSEDED`, `active=false`)** — disabled.
3. **Approved payroll (e.g. id 90, `APPROVED`)** — downloads `payslip-90.pdf`.
4. Approve 72 (`PATCH /api/v1/payroll/72/status` → `{"status":"APPROVED"}`), reload, and the
   button becomes enabled — confirming the gate follows the real backend rule.
5. Force a failure (e.g. call with a deleted id) and confirm the UI now shows the backend's
   message instead of a generic one.

## 7. Related behaviour worth knowing

`POST /{id}/regenerate` creates a new version with `status = GENERATED`. A payroll that had a
downloadable payslip therefore **loses** it until the new version is approved again. After a
successful regeneration the page must adopt the returned payroll (new `id`, new `version`) —
a button still wired to the superseded id fails with the superseded-version 400 above.

Also note that `GET /payroll/employee/{employeeId}` returns all versions of all months, so the
history table legitimately contains many `SUPERSEDED` rows (employee 7's September payroll
alone has 15 versions). Consider badging them as historical rather than listing them
indistinguishably from current records.
