# Payroll Regeneration — Frontend Implementation Guide

**Module:** Payroll
**Endpoint:** `POST /api/v1/payroll/{payrollId}/regenerate`
**Backend status:** implemented on branch `payroll_regeneration`
**Audience:** frontend team

---

## 1. What changed

Previously, regenerate was a body-less call that cloned the payroll as-is; the only snapshot field it
failed to carry was `dateOfJoining`. Now the endpoint accepts an **optional partial body**. The frontend can send only the fields the user
changed; every omitted (`null`) field keeps the value from the existing payroll. All calculated totals
are recalculated server-side.

**Backward compatible:** sending no body at all still works exactly like before (pure copy of values),
so existing callers do not break.

---

## 2. Endpoint contract

| Item | Value |
|---|---|
| Method | `POST` |
| Path | `/api/v1/payroll/{payrollId}/regenerate` |
| Path param | `payrollId` (number) — the **currently active** payroll version |
| Request body | **Optional** `RegeneratePayrollRequest` (JSON) |
| Request `Content-Type` | `application/json` (omit entirely when sending no body) |
| Success status | `200 OK` |
| Success body | `PayrollResponse` (the **new** version) |
| Roles | `SUPER_ADMIN`, `HR_ADMIN`, `PAYROLL_ADMIN` |
| Auth | Standard bearer token, same as every other payroll endpoint |

---

## 3. Request schema — `RegeneratePayrollRequest`

All fields are optional. **`null` / omitted means "keep the value from the old payroll"** — it does
**not** mean zero, and it does **not** clear the field.

### Attendance

| Field | Type | Constraints (if sent) | Meaning |
|---|---|---|---|
| `totalWorkingDays` | integer | `>= 1` | Total working days in the month |
| `workedDays` | integer | `>= 0` | Days the employee worked |
| `lopDays` | integer | `>= 0` | Loss-of-pay days |

Cross-field rule enforced by the backend: `workedDays + lopDays <= totalWorkingDays`.

### Earnings

| Field | Type | Constraints (if sent) |
|---|---|---|
| `basicSalary` | decimal | `>= 0` |
| `hra` | decimal | `>= 0` |
| `specialAllowance` | decimal | `>= 0` |
| `medicalAllowance` | decimal | `>= 0` |
| `travelAllowance` | decimal | `>= 0` |
| `bonus` | decimal | `>= 0` |
| `otherAllowance` | decimal | `>= 0` |

### Deductions

| Field | Type | Constraints (if sent) |
|---|---|---|
| `pf` | decimal | `>= 0` |
| `esi` | decimal | `>= 0` |
| `professionalTax` | decimal | `>= 0` |
| `incomeTax` | decimal | `>= 0` |
| `otherDeduction` | decimal | `>= 0` |

### Other

| Field | Type | Constraints (if sent) |
|---|---|---|
| `remarks` | string (max 500) | — |

### Do NOT send

These are server-owned. Sending them is ignored:

- Calculated: `grossSalary`, `lopAmount`, `totalDeduction`, `netPayable`, `payableDays`
- Identity / versioning: `id`, `payrollNumber`, `version`, `active`, `status`
- Snapshots: `employee`, `salaryStructure`, `payrollMonth`, `employeeName`, `employeeCode`,
  `dateOfJoining`, `departmentName`, `designationName`, `panNumber`, `uanNumber`, `bankName`,
  `accountNumber`, `ifscCode`
- Approval / payment: `approvedBy`, `approvedDate`, `paymentDate`, `paymentReference`

---

## 4. Response schema — `PayrollResponse`

The response is the **new** payroll version (not wrapped in an envelope).

```json
{
  "id": 501,
  "payrollNumber": "PR-202608-0006",
  "version": 2,
  "active": true,
  "employeeId": 101,
  "employeeCode": "EMP000101",
  "employeeName": "Jitendra Prajapati",
  "dateOfJoining": "2024-04-01",
  "departmentName": "Engineering",
  "designationName": "Software Engineer",
  "panNumber": "ABCDE1234F",
  "bankName": "State Bank of India",
  "accountNumber": "123456789012",
  "ifscCode": "SBIN0001234",
  "payrollMonth": "2026-08-01",
  "status": "GENERATED",
  "totalWorkingDays": 30,
  "workedDays": 28,
  "lopDays": 2,
  "payableDays": 28,
  "basicSalary": 35000.00,
  "hra": 10000.00,
  "specialAllowance": 0.00,
  "medicalAllowance": 0.00,
  "travelAllowance": 0.00,
  "bonus": 5000.00,
  "otherAllowance": 0.00,
  "grossSalary": 50000.00,
  "lopAmount": 3333.33,
  "pf": 1800.00,
  "esi": 0.00,
  "professionalTax": 0.00,
  "incomeTax": 0.00,
  "otherDeduction": 0.00,
  "totalDeduction": 5133.33,
  "netPayable": 44866.67,
  "approvedBy": null,
  "approvedDate": null,
  "paymentDate": null,
  "paymentReference": null,
  "remarks": "Salary corrected before approval",
  "createdAt": "2026-09-18T10:15:30",
  "updatedAt": "2026-09-18T10:15:30"
}
```

> **Note:** `uanNumber` is stored on the payroll snapshot but is **not returned** by `PayrollResponse`
> today, so the UI cannot display it from this response. See §9.

---

## 5. Field behaviour after regeneration

| Field group | Source on the new version |
|---|---|
| Attendance (`totalWorkingDays`, `workedDays`, `lopDays`) | client value if sent, else old value |
| Earnings (`basicSalary` … `otherAllowance`) | client value if sent, else old value |
| Deductions (`pf` … `otherDeduction`) | client value if sent, else old value |
| `remarks` | client value if sent, else old value |
| `grossSalary` | **recalculated** |
| `lopAmount` | **recalculated** |
| `totalDeduction` | **recalculated** |
| `netPayable` | **recalculated** |
| `payableDays` | **recalculated** = merged `workedDays` |
| Employee / payment snapshots, `salaryStructure`, `payrollMonth` | copied from old payroll |
| `payrollNumber` | newly generated (e.g. `PR-202608-0006`) |
| `version` | `old version + 1` |
| `active` | `true` |
| `status` | `GENERATED` |
| `approvedBy`, `approvedDate`, `paymentDate`, `paymentReference` | reset to `null` |

---

## 6. Calculation rules (server-side, do not replicate in the UI)

```
Gross Salary    = basicSalary + hra + specialAllowance + medicalAllowance
                  + travelAllowance + bonus + otherAllowance

LOP Amount      = (Gross Salary / totalWorkingDays) * lopDays
                  (0 when totalWorkingDays <= 0 or lopDays <= 0)

Total Deduction = pf + esi + professionalTax + incomeTax + otherDeduction + LOP Amount

Net Payable     = Gross Salary - Total Deduction
```

Validation (server-side; the UI can mirror it for instant feedback):

- `grossSalary > 0`
- `totalDeduction >= 0`
- `totalDeduction <= grossSalary`
- `netPayable >= 0`

---

## 7. Versioning / lifecycle

Regeneration never overwrites the old record.

| | Before | After |
|---|---|---|
| Old payroll | `id=500`, `version=1`, `active=true`, `status=GENERATED` | `id=500`, `version=1`, `active=false`, `status=SUPERSEDED` |
| New payroll | — | `id=501`, `version=2`, `active=true`, `status=GENERATED` |

- The operation is transactional. If any merge/calculation/validation step fails, **nothing** is
  changed — the old payroll stays active.
- History records are written automatically: `SUPERSEDED` on the old payroll,
  `REGENERATED` on the new payroll.
- The old payroll is only marked `SUPERSEDED` after the new version is successfully saved.
- Regeneration resets approval/payment state, so the new version must be approved again before payment.

---

## 8. Worked examples

### 8.1 Partial update (the common case)

Old payroll:

```
basicSalary = 30000, hra = 10000, specialAllowance = 0, medicalAllowance = 0,
travelAllowance = 0, bonus = 2000, otherAllowance = 0, pf = 1800, totalWorkingDays = 30,
lopDays = 2
```

Request:

```json
{
  "basicSalary": 35000,
  "bonus": 5000
}
```

Result on new version:

```
basicSalary = 35000   <- client
hra         = 10000   <- old
bonus       = 5000    <- client
pf          = 1800    <- old
lopDays     = 2       <- old
grossSalary = 50000   <- recalculated
lopAmount   = 3333.33 <- recalculated
```

### 8.2 Attendance-only change

```json
{ "lopDays": 1, "remarks": "Salary corrected before approval" }
```

`lopDays` becomes `1`; `totalWorkingDays` / `workedDays` keep old values; `lopAmount`,
`totalDeduction`, `netPayable` and `payableDays` are recalculated.

### 8.3 No changes (pure copy)

```http
POST /api/v1/payroll/500/regenerate
```
(no body, no `Content-Type`)

Creates version 2 with the same values as version 1, recalculated totals (which will match the old
totals), and supersedes version 1.

---

## 9. Error handling

Error body (`ApiError`):

```json
{
  "success": false,
  "message": "Total Deduction cannot exceed Gross Salary.",
  "errorCode": "BAD_REQUEST",
  "path": "/api/v1/payroll/500/regenerate",
  "timestamp": "2026-09-18T10:15:30"
}
```

| HTTP | `errorCode` | Message (may vary) | Cause |
|---|---|---|---|
| 400 | `BAD_REQUEST` | `Paid payrolls cannot be regenerated.` | Target payroll is `PAID` |
| 400 | `BAD_REQUEST` | `Only the active payroll version can be regenerated.` | Target is `SUPERSEDED` / `CANCELLED` (`active = false`) |
| 400 | `BAD_REQUEST` | `Worked days + LOP days cannot exceed total working days.` | Conflicting attendance values |
| 400 | `BAD_REQUEST` | `Gross Salary must be greater than 0.` | All earnings resolve to zero |
| 400 | `BAD_REQUEST` | `Total Deduction cannot be negative.` | Invalid deduction values |
| 400 | `BAD_REQUEST` | `Total Deduction cannot exceed Gross Salary.` | Deductions too large |
| 400 | `BAD_REQUEST` | `Net Payable cannot be negative.` | Net below zero |
| 400 | `VALIDATION_FAILED` | e.g. `Basic Salary cannot be negative.`, `Total working days must be at least 1.`, `Worked days cannot be negative.` | Bean-validation on sent fields |
| 400 | `INVALID_REQUEST` | e.g. `Invalid value for field: basicSalary` | Malformed JSON / wrong type |
| 403 | `ACCESS_DENIED` | `Access denied.` | Role not permitted |
| 404 | `RESOURCE_NOT_FOUND` | `Payroll not found with id: 500` | Unknown `payrollId` |
| 500 | `INTERNAL_SERVER_ERROR` | `An unexpected error occurred. (...)` | Server fault |

---

## 10. Frontend integration guide

### 10.1 UI flow

1. Load the active payroll via `GET /api/v1/payroll/{payrollId}` and pre-fill the edit form with the
   returned values (editable fields only).
2. Let the user modify any subset of fields.
3. Send **only the fields that were actually changed** (dirty fields). Do not send unchanged fields as
   the same value, and never send `null` to mean "clear".
4. On success, replace the in-memory payroll with the returned object — including the **new `id`** —
   so subsequent approve/pay/payslip calls target the new version.
5. Refresh any employee payroll version list, since the old version is now `SUPERSEDED`.

### 10.2 Visibility rules

- Show the **Regenerate** action only when `status` is `DRAFT`, `GENERATED` or `APPROVED`.
- Hide it when `status` is `PAID`, `SUPERSEDED` or `CANCELLED`.
- Warn the user that regenerating an `APPROVED` payroll discards its approval and resets payment
  state.

### 10.3 Suggested client-side pre-validation (nice-to-have)

Mirror the server rules so users get instant feedback before the request:

```ts
function validateRegeneration(form: RegeneratePayrollRequest): string | null {
  const { totalWorkingDays, workedDays, lopDays } = form;
  if (totalWorkingDays != null && totalWorkingDays < 1) return "Total working days must be at least 1.";
  if ((workedDays ?? 0) < 0 || (lopDays ?? 0) < 0) return "Days cannot be negative.";
  if (totalWorkingDays != null && workedDays != null && lopDays != null &&
      workedDays + lopDays > totalWorkingDays) {
    return "Worked days + LOP days cannot exceed total working days.";
  }
  return null; // totals are calculated and validated server-side
}
```

### 10.4 TypeScript types

```ts
export interface RegeneratePayrollRequest {
  totalWorkingDays?: number;
  workedDays?: number;
  lopDays?: number;
  basicSalary?: number;
  hra?: number;
  specialAllowance?: number;
  medicalAllowance?: number;
  travelAllowance?: number;
  bonus?: number;
  otherAllowance?: number;
  pf?: number;
  esi?: number;
  professionalTax?: number;
  incomeTax?: number;
  otherDeduction?: number;
  remarks?: string;
}

export type PayrollStatus =
  | "DRAFT" | "GENERATED" | "APPROVED" | "PAID" | "SUPERSEDED" | "CANCELLED";

export interface PayrollResponse {
  id: number;
  payrollNumber: string;
  version: number;
  active: boolean;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  dateOfJoining: string | null;
  departmentName: string | null;
  designationName: string | null;
  panNumber: string | null;
  bankName: string | null;
  accountNumber: string | null;
  ifscCode: string | null;
  payrollMonth: string;
  status: PayrollStatus;
  totalWorkingDays: number;
  workedDays: number;
  lopDays: number;
  payableDays: number;
  basicSalary: number;
  hra: number;
  specialAllowance: number;
  medicalAllowance: number;
  travelAllowance: number;
  bonus: number;
  otherAllowance: number;
  grossSalary: number;
  lopAmount: number;
  pf: number;
  esi: number;
  professionalTax: number;
  incomeTax: number;
  otherDeduction: number;
  totalDeduction: number;
  netPayable: number;
  approvedBy: string | null;
  approvedDate: string | null;
  paymentDate: string | null;
  paymentReference: string | null;
  remarks: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ApiError {
  success: false;
  message: string;
  errorCode: string;
  path: string;
  timestamp: string;
}
```

### 10.5 Call examples

**Fetch (no body):**

```ts
await fetch(`/api/v1/payroll/${payrollId}/regenerate`, {
  method: "POST",
  headers: { Authorization: `Bearer ${token}` },
});
```

**Fetch (partial body):**

```ts
const body: RegeneratePayrollRequest = {
  basicSalary: 35000,
  bonus: 5000,
  lopDays: 1,
  remarks: "Salary corrected before approval",
};

await fetch(`/api/v1/payroll/${payrollId}/regenerate`, {
  method: "POST",
  headers: {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  },
  body: JSON.stringify(body),
});
```

**Axios:**

```ts
// No body
await api.post(`/api/v1/payroll/${payrollId}/regenerate`);

// Partial body
await api.post<PayrollResponse>(
  `/api/v1/payroll/${payrollId}/regenerate`,
  { basicSalary: 35000, bonus: 5000 }
);
```

### 10.6 Swagger

Available at `/swagger-ui.html` under tag `20-Payroll`.

---

## 11. Gotchas / checklist

- [ ] Send only **dirty** fields; omitted means "keep old", not zero.
- [ ] Never send `null` expecting the value to be cleared — it keeps the old value.
- [ ] Do not send calculated fields (`grossSalary`, `lopAmount`, `totalDeduction`, `netPayable`).
- [ ] Replace local state with the **returned** payroll — `id`, `version` and `payrollNumber` change.
- [ ] The old version becomes `SUPERSEDED`; refresh version lists and disable actions on it.
- [ ] After regenerating an `APPROVED` payroll, approval/payment fields are `null` — the new version
      needs approval again before payment.
- [ ] `uanNumber` is copied into the new payroll but is **not exposed** by `PayrollResponse`
      (pre-existing response gap). If the UI must show UAN after regeneration, that response field
      needs to be added backend-side.
- [ ] Sending a body with `Content-Type: application/json` but an empty payload (`{}`) is equivalent
      to no body.
- [ ] Decimals are `BigDecimal(precision 12, scale 2)` server-side; render with 2 decimals.
