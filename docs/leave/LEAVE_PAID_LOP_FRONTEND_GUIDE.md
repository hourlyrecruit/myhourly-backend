# Leave PAID / LOP Split & Approver — Frontend Implementation Guide

**Module:** Leave
**Backend change:** every approved leave request is now classified day-by-day into **PAID** (deducted from the annual leave balance) and **LOP** (Loss of Pay — *not* deducted), and the authenticated approver is stored on the request itself.
**Endpoints touched:** `POST /api/v1/leave-requests`, `GET /api/v1/leave-requests` (`/my`, `/team`, `/{id}`), `PUT /api/v1/leave-approvals/{id}/leave-approval-by-manager`
**Backend status:** implemented, **uncommitted** on branch `deployment` (working tree of `myhourly-backend`)
**Frontend repo:** `HRMS` (separate git repository, currently on `main`)
**Audience:** frontend team

> This guide is written against the backend working tree, not a released build. Nothing here is committed or pushed yet.

---

## 0. TL;DR — what the frontend actually has to do

1. **No new endpoint and no request-body change.** Apply and approve keep the same contracts. `paidDays`, `lopDays` and the approver are **server-owned** — never send them.
2. **Five new fields** arrive on every leave request in the response: `paidDays`, `lopDays`, `approvedById`, `approvedByName`, `approvedByCode`. They are `null` until the request is approved.
3. **`"Insufficient leave balance."` is no longer a submission error.** Submission now accepts a request that exceeds the monthly allowance and/or the remaining balance; the excess is classified as LOP *at approval*. Any UI copy or client-side gate that says "you don't have enough balance, reduce your dates" must go.
4. **Show the split and the approver** in the employee leave history and the manager approval screen, and warn the approver — before they confirm — that days over the monthly guideline or over the balance become LOP.
5. `paidDays` means **"deducted from the annual balance"**, *not* "salaried". An unpaid leave type stores `paidDays = totalDays` / `lopDays = 0` while still being unpaid in payroll. Do not label the split column "Salary".

---

## 1. What changed in the backend

### 1.1 Approved days are split into PAID and LOP

At approval time the backend walks the request's **working days** in date order and classifies each day:

* **PAID** — the day is deducted from the annual leave balance.
* **LOP** — the day is *not* deducted from the annual leave balance and is not paid.

The split is stored **per request**, so one approval can be 2 PAID + 3 LOP.

Source: `LeavePaidLopServiceImpl.classify` ([LeavePaidLopServiceImpl.java:72](../../src/main/java/com/my_hourly/leave/service/impl/LeavePaidLopServiceImpl.java#L72)), applied in `LeaveRequestServiceImpl.managerAction` ([LeaveRequestServiceImpl.java:330](../../src/main/java/com/my_hourly/leave/service/impl/LeaveRequestServiceImpl.java#L330)).

### 1.2 The monthly allowance decides how many days can be PAID

The **monthly guideline** (`LeaveSettings.monthlyGuideline`, falling back to the leave type's own `monthlyGuideline`, then a hard default of `2`) is the maximum number of PAID days per calendar month. What is already approved as PAID in that month is subtracted first, so the allowance is shared across requests.

### 1.3 The approver is recorded on the request

The approver is taken from the **security context** — never from the request body — and written onto the leave request (`approved_by`). `leave_approvals` still holds the per-action audit trail; `approvedByName` on the request is the denormalised pointer the list views read.

### 1.4 Submission no longer rejects an over-balance request

`LeaveValidationServiceImpl.resolveLeaveBalance` only checks that a balance **exists** for the employee/leave type/year. The old `balance < totalDays → 400 INSUFFICIENT "Insufficient leave balance."` check is gone ([LeaveValidationServiceImpl.java:187](../../src/main/java/com/my_hourly/leave/service/impl/LeaveValidationServiceImpl.java#L187)).

A missing balance row is still a hard error at submission (`RESOURCE_NOT_FOUND`), because that is a configuration problem.

### 1.5 The balance only moves on PAID days

`deductPaidLeaveDays` adds only the PAID days to `usedLeaves` and subtracts them from `remainingLeaves`; when the PAID count is `0` no balance write and **no ledger transaction** happen ([LeaveBalanceServiceImpl.java:166](../../src/main/java/com/my_hourly/leave/service/impl/LeaveBalanceServiceImpl.java#L166)).

Two concurrency guards were added (no frontend surface, but they explain certain errors): the request row is read `FOR UPDATE` before the status check, and the balance row `FOR UPDATE` before the split is computed. A double-clicked Approve now loses cleanly with `LEAVE_ALREADY_PROCESSED` instead of deducting twice.

---

## 2. API surface

| # | Method | Path | Roles | Body | `data` |
|---|---|---|---|---|---|
| 1 | `POST` | `/api/v1/leave-requests` | EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN | `{ leaveTypeId, startDate, endDate, reason }` | `LeaveRequestResponse` (**201**) |
| 2 | `PUT` | `/api/v1/leave-requests/{id}/cancel` | same as #1 | — | `LeaveRequestResponse` |
| 3 | `GET` | `/api/v1/leave-requests/my` | any authenticated leave role | — | `LeaveRequestResponse[]` |
| 4 | `GET` | `/api/v1/leave-requests/team` | MANAGER, SUPER_ADMIN | — | `LeaveRequestResponse[]` |
| 5 | `GET` | `/api/v1/leave-requests` | HR_ADMIN, SUPER_ADMIN, MANAGER | — | `LeaveRequestResponse[]` |
| 6 | `GET` | `/api/v1/leave-requests/{id}` | any authenticated leave role | — | `LeaveRequestResponse` |
| 7 | `PUT` | `/api/v1/leave-approvals/{id}/leave-approval-by-manager` | **MANAGER only** | `{ action: "APPROVE" \| "REJECT", reason? }` | `LeaveRequestResponse` (**the updated request**) |
| 8 | `GET` | `/api/v1/leave-approvals/leave-request/{id}` | authenticated | — | `LeaveApprovalResponse[]` |

All of them are wrapped in the usual envelope:

```json
{ "success": true, "message": "…", "data": { }, "timestamp": "2026-10-09T10:15:00" }
```

The HRMS axios instance already unwraps `.data.data` ([leaveService.js](../../HRMS/src/services/leaveService.js)), so no service-layer change is required for the response shape.

**Unchanged request contracts.** The apply body and the approve/reject body are byte-identical to before. `reason` is optional for `APPROVE` and **required** for `REJECT`.

---

## 3. Response schema — `LeaveRequestResponse`

Source: [LeaveRequestResponse.java](../../src/main/java/com/my_hourly/leave/api/response/LeaveRequestResponse.java)

| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `employeeId`, `employeeCode`, `employeeName` | number / string / string | |
| `leaveTypeId`, `leaveType` | number / string | `leaveType` is the **name** |
| `startDate`, `endDate` | string (`yyyy-MM-dd`) | |
| `totalDays` | number | **working days** (weekends and holidays excluded) |
| `reason` | string | |
| `status` | `PENDING` \| `APPROVED` \| `REJECTED` \| `CANCELLED` | |
| **`paidDays`** | number \| **null** | Days deducted from the annual balance. `null` while pending/rejected/cancelled **and for every row approved before this change** |
| **`lopDays`** | number \| **null** | Days *not* deducted from the balance |
| **`approvedById`** | number \| null | **Employee id** of the approver (not the user id) |
| **`approvedByName`** | string \| null | `"First Last"`, or just `"First"` when there is no last name |
| **`approvedByCode`** | string \| null | Approver's employee code |

### Null semantics (important)

| Request state | `paidDays` / `lopDays` | `approvedBy*` |
|---|---|---|
| `PENDING` | `null` | `null` |
| `REJECTED` | `null` | `null` (the rejection is still in `leave_approvals`) |
| `CANCELLED` | `null` | `null` |
| `APPROVED` after this change | `paidDays + lopDays === totalDays` | populated |
| `APPROVED` **before** this change (legacy rows) | **`null`** | **`null`** |

Legacy rows are deliberately left `NULL`: before the split existed every approved day was deducted, so a `null` must be read as "fully deducted" — not as "0 PAID / 0 LOP". The frontend must therefore treat `null` as *unknown*, never render `0P · 0L`, and never compute `totalDays - paidDays` without a null check.

### Example — mixed approval

```json
{
  "id": 318,
  "employeeId": 101,
  "employeeCode": "EMP000101",
  "employeeName": "Jitendra Prajapati",
  "leaveTypeId": 1,
  "leaveType": "ANNUAL",
  "startDate": "2026-10-12",
  "endDate": "2026-10-16",
  "totalDays": 5,
  "reason": "Family function",
  "status": "APPROVED",
  "paidDays": 2,
  "lopDays": 3,
  "approvedById": 7,
  "approvedByName": "Rohit Sharma",
  "approvedByCode": "EMP000007"
}
```

### Example — pending (apply response)

```json
{
  "id": 319,
  "employeeId": 101,
  "employeeCode": "EMP000101",
  "employeeName": "Jitendra Prajapati",
  "leaveTypeId": 1,
  "leaveType": "ANNUAL",
  "startDate": "2026-11-02",
  "endDate": "2026-11-04",
  "totalDays": 3,
  "reason": "Personal",
  "status": "PENDING",
  "paidDays": null,
  "lopDays": null,
  "approvedById": null,
  "approvedByName": null,
  "approvedByCode": null
}
```

---

## 4. Balance semantics — `LeaveBalanceResponse`

Source: [LeaveBalanceResponse.java](../../src/main/java/com/my_hourly/leave/api/response/LeaveBalanceResponse.java)

| Field | Meaning after the change |
|---|---|
| `allocatedLeaves` | annual allocation |
| `usedLeaves` | **only PAID approved days** (LOP days are never added) |
| `expiredLeaves` | expired guideline days (month-end job) |
| `remainingLeaves` | the live remaining days (kept in step by the allocation, expiry and deduction flows) |

Frontend implications:

* `usedLeaves` is no longer "all approved leave days" — it is "balance days spent". Label cards accordingly (e.g. *"3 of 24 balance days used"*, not *"3 days taken"*).
* `remainingLeaves` only goes down through **PAID** days, so a user with an exhausted allowance can still submit leave that will be entirely LOP.
* The new deduction path clamps so a PAID deduction can never push the balance below zero. The **legacy unpaid path** (`deductLeaveBalance`, used for leave types with `paid = false`) still subtracts the full `totalDays` with no clamp — unchanged behaviour.

---

## 5. Classification rules (server-side — do **not** re-implement in the UI)

```
guideline      = LeaveSettings.monthlyGuideline
                 ?? leaveType.monthlyGuideline
                 ?? 2
workingDays    = every day in [startDate, endDate] that is not Sat/Sun and not a holiday
for each calendar month touched by the request, in date order:
    allowanceLeft = max(0, guideline − PAID days already approved in that month)
    remaining     = employee's annual balance for that month's year
    for each working day of the request in that month:
        if allowanceLeft > 0 and remaining > 0:  PAID  (allowanceLeft--, remaining--)
        else:                                    LOP
```

Consequences worth knowing before you write UI copy:

* A month boundary **resets** the allowance, so one request can be PAID in October and LOP in November.
* Within the classifier the annual balance can never go negative — an exhausted balance degrades to LOP instead. (This applies to **paid** leave types; unpaid types still take the legacy full-deduction path.)
* Only **working days** are classified, matching `totalDays` (weekends/holidays are excluded). The employee-side "Estimated days" preview in the HRMS form counts **calendar days**, so it can disagree with the stored `totalDays`; this is pre-existing, but do not use it to predict the split.
* The guideline is a **per-month allowance for PAID days**, not a credit into the annual balance. Unused allowance days still expire per the existing month-end job.

### Worked examples (guideline = 2)

| Scenario | Request days | Already PAID this month | Balance | Result |
|---|---|---|---|---|
| Single month, allowance free | 5 | 0 | 10 | **2 PAID / 3 LOP**, balance 10 → 8 |
| Allowance already spent | 5 | 2 | 10 | **0 PAID / 5 LOP**, balance untouched, no ledger row |
| Balance nearly empty | 3 | 0 | 1 | **1 PAID / 2 LOP**, balance 1 → 0 |
| Crosses a month boundary, 3 days in Oct + 2 in Nov | 5 | Oct 1, Nov 0 | 10 | up to **2 PAID in Oct + 2 PAID in Nov**, rest LOP |
| Unpaid leave type (`paid = false`) | 4 | — | 10 | **`paidDays = 4`, `lopDays = 0`**, balance fully deducted (legacy behaviour) |

---

## 6. Screen-by-screen implementation plan

### 6.1 Employee page — `src/pages/Leave.jsx` (+ `Leave.css`)

**Current state (already in the HRMS working tree, uncommitted):** the history table's *Days* cell renders the split chip (`2P · 3L` with a tooltip) when `lopDays > 0`, and the *Status* cell shows `by <approvedByName>`. The `.leave-split` / `.leave-approver` rules exist in `Leave.css`.

**Remaining work:**

1. **Gate the chip on the leave type being paid.** The split is only meaningful for a paid leave type; for an unpaid type every day is reported as `paidDays = totalDays`. Look up the type in the `leaveTypes` state (`paid` is exposed by `GET /leave-types/active`) and render the chip only when `type.paid === true`.
2. **Never render the chip for legacy rows.** Condition must be `Number(row.lopDays) > 0` — which already holds, because `null` fails the comparison. Keep it that way; if you switch to `row.lopDays !== undefined`, you break old rows.
3. **Reword the tooltip** to balance language: `"2 day(s) deducted from your balance, 3 LOP day(s) (unpaid, not deducted)"`.
4. **Add an LOP hint to the apply-form preview.** In the `lf-preview` grid, next to *Remaining balance*, add a non-blocking line when `estimatedDays > selectedBalance.remainingLeaves` (or when days in the month exceed the monthly guideline):
   > *"This request exceeds your remaining balance / monthly allowance. The extra days will be marked LOP (unpaid)."*
   Use the leave type's `monthlyGuideline` (from `GET /leave-types/active`) for the guideline hint. **Do not block submission.**
5. **Delete any "insufficient balance" gate.** Search `Leave.jsx` for balance comparisons on submit — the backend no longer rejects on balance, so a client gate would be wrong. (As of this writing there is no such gate, only the preview values.)
6. **Balance card copy.** Line "`{balance.usedLeaves} of {balance.allocatedLeaves} used`" is now PAID-only; consider *"… balance days used"*. Optional.

**Acceptance criteria**

- [ ] Approving a 5-day request with a free allowance shows `5` with a `2P · 3L` chip, and the tooltip explains both numbers.
- [ ] A legacy approved row (nulls) shows the plain total with no chip and no `by …` line.
- [ ] A pending row shows the plain total, no chip, no approver.
- [ ] Submitting a request longer than the remaining balance succeeds (201) and lands as `PENDING`.
- [ ] No copy anywhere says the request will be rejected for insufficient balance.

### 6.2 Manager page — `src/pages/LeaveApprovals.jsx` (+ `LeaveApprovals.css`)

**Current state:** untouched. The *Days* column prints `row.totalDays` and the Approve confirmation modal only repeats employee, type and dates.

**Work to do:**

1. **Days column** — same split chip as 6.1, shown only when `row.status === 'APPROVED'` and `Number(row.lopDays) > 0`. Reuse the `.leave-split` style; the file has its own stylesheet, so add the class to `LeaveApprovals.css` (or import the shared one).
2. **Status column** — add the approver under the status badge for approved rows: `by {row.approvedByName}` (`title={approvedByCode}` is a nice touch).
3. **Drawer (`drawer-grid`)** — for an approved request add two items: *Balance days* (`paidDays`) and *LOP days* (`lopDays`), plus an *Approved by* item (`approvedByName` / `approvedByCode`). Hide them when the values are `null` rather than printing `—`, so legacy rows don't look like zero.
4. **Approve confirmation modal — the important one.** Before the manager confirms, show what is at stake. The response fields are only available *after* approval, so the client cannot know the exact split; present the outcome as a consequence, not a number:
   > **"Days beyond the monthly paid guideline, or beyond the employee's remaining balance, will be marked LOP (Loss of Pay) and are not deducted from their balance."**
   Do not compute or display a predicted split.
5. **Disable Approve on requests that the backend will refuse**, instead of letting the manager hit an error:
   - `startDate < today` → the backend answers `400 INVALID_DATE` *"Leave cannot be approved after its start date."* Disable the button and show *"Cannot be approved — the start date has passed."*
   - Optional: disable while another action is in flight (already handled by `acting`).
   The existing error-text rewriting in `decide()` can stay as a safety net.
6. **Refresh after the decision.** `load()` already refetches; since the response now carries the split and the approver, you can also patch the row in place from the `managerAction` response. The list is the source of truth — refetch and you get the split for free.
7. **Export** currently calls `/reports/leave`, which does **not** include the split (see §6.5): an exported file will never show `paidDays`/`lopDays`. Either leave the export as-is or add a note in the UI; do not promise LOP columns in the export.

**Acceptance criteria**

- [ ] Approving a request that ends up mixed shows *"Leave Request Approved."* and the refreshed row shows the split chip + approver.
- [ ] The confirmation modal explains the LOP consequence before the manager confirms.
- [ ] Approve is disabled (with a reason) for a request whose `startDate` is in the past.
- [ ] Double-clicking Approve produces a single deduction; the second call surfaces `LEAVE_ALREADY_PROCESSED` ("Leave request has already been processed.") or is prevented by the disabled state.
- [ ] A rejected request shows neither split nor approver.

### 6.3 Settings → Leave Types — `src/pages/settings/LeaveTypesSettings.jsx`

The screen already edits `monthlyGuideline` ("Monthly Guideline (days)"), and the current helper text says *"Unused monthly guideline days don't expire at month-end."* — which is unrelated to this change and arguably describes expiry, not the PAID cap.

**Work to do:** extend the helper text (no schema change):

> *"Maximum number of PAID days an employee can take per calendar month for this leave type. Days beyond this allowance (or beyond their remaining balance) are recorded as LOP and are not deducted from the balance."*

**Acceptance criteria:** the guideline field explains its role in the PAID/LOP split; saving still round-trips through `PUT /leave-types/{id}` with `monthlyGuideline` as a whole number ≥ 0.

### 6.4 Dashboard — `src/pages/Dashboard.jsx`

`loadLeave()` sums `usedLeaves` / `remainingLeaves` into the *Leaves Left* card (*"N Days Taken"*). Both numbers are now PAID-only, so *"Days Taken"* under-reports when LOP days exist.

**Work to do (low risk, copy-only):** rename the sub-label to *"N balance days used"*. Leave the numbers alone.

### 6.5 Reports — **backend gap, no frontend change yet**

`LeaveReportResponse` exposes `totalDays` but **not** `paidDays`/`lopDays`, and `LeaveSummaryResponse` has no LOP aggregate at all ([LeaveReportResponse.java](../../src/main/java/com/my_hourly/report/dto/response/LeaveReportResponse.java), [LeaveSummaryResponse.java](../../src/main/java/com/my_hourly/report/dto/response/LeaveSummaryResponse.java)). The report query is built by [LeaveReportSpecification.java](../../src/main/java/com/my_hourly/report/specification/LeaveReportSpecification.java).

**Therefore:** do not add LOP columns or LOP summary tiles to `Reports.jsx` / `ReportsExplorer` / the export until the report DTOs and the query are extended. This is a backend task; raise it as a separate ticket.

### 6.6 Payroll — **cross-module inconsistency to be aware of**

Payroll already has its own `lopDays` / `lopAmount` (used by the regeneration edit form and the payslip). Those numbers are **not** read from leave: `PayrollServiceImpl` derives them from *attendance* rows as

```
lopDays = absentDays + max(0, leaveDays − LeaveSettings.monthlyGuideline)
```

([PayrollServiceImpl.java:766](../../src/main/java/com/my_hourly/payroll/service/impl/PayrollServiceImpl.java#L766)).

So for the same month the leave screen can legitimately say *"3 LOP days"* while the payslip says *"1 LOP day"*, because payroll counts attendance `LEAVE` days against a single global guideline for the whole month, while the leave split is per request/leave type/per month. **Do not display a combined "annual LOP total"** in the UI until this is reconciled; keep LOP figures scoped to the screen they come from.

---

## 7. Service layer — `HRMS/src/services/leaveService.js`

No new functions and no signature changes are needed. Optional convenience additions:

```js
// Human-readable split for a leave request row.
export function leaveSplitLabel(row) {
  const paid = row?.paidDays;
  const lop = row?.lopDays;
  if (paid === null || paid === undefined || lop === null || lop === undefined) return null; // pending / legacy
  if (!lop) return null; // nothing to explain
  return `${paid}P · ${lop}L`;
}

export function leaveSplitTooltip(row) {
  if (row?.paidDays == null || row?.lopDays == null) return undefined;
  return `${row.paidDays} day(s) deducted from the balance, `
       + `${row.lopDays} LOP day(s) (unpaid, not deducted)`;
}
```

**Refresh rules:** after `applyLeave`, `cancelLeave` or `managerLeaveAction`, re-fetch the affecting lists (`getMyLeaveRequests` / `getAllLeaveRequests` / `getTeamLeaveRequests`) **and** `getMyLeaveBalances` — the balance only changes on approval, but the employee page shows both.

---

## 8. Types

```ts
export type LeaveStatus = "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";

export interface LeaveRequest {
  id: number;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  leaveTypeId: number;
  leaveType: string;
  startDate: string;   // yyyy-MM-dd
  endDate: string;     // yyyy-MM-dd
  totalDays: number;
  reason: string;
  status: LeaveStatus;
  /** Days deducted from the annual balance. null while pending/rejected, and on rows approved before the split existed. */
  paidDays: number | null;
  /** Loss-of-pay days, not deducted from the balance. Same null rules as paidDays. */
  lopDays: number | null;
  approvedById: number | null;
  approvedByName: string | null;
  approvedByCode: string | null;
}

export interface LeaveBalance {
  id: number;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  leaveTypeId: number;
  leaveType: string;
  year: number;
  allocatedLeaves: number;
  /** PAID days only. */
  usedLeaves: number;
  expiredLeaves: number;
  remainingLeaves: number;
}

export interface LeaveApprovalActionRequest {
  action: "APPROVE" | "REJECT";
  reason?: string; // required for REJECT
}
```

---

## 9. Error handling

`ApiError` carries `errorCode` (enum name) and `message`; the HRMS axios interceptor surfaces `message` as `error.message` ([api.js](../../HRMS/src/services/api.js)).

| Screen / action | Status | `errorCode` | Message | Frontend handling |
|---|---|---|---|---|
| Apply | 400 | `INSUFFICIENT` | ~~"Insufficient leave balance."~~ | **No longer returned.** Remove any handling/copy that expects it |
| Apply | 400 | `LEAVE_ALREADY_EXIST` | "Leave request already exists for the selected dates." | keep existing |
| Apply | 404 | `RESOURCE_NOT_FOUND` | "No working days found between selected dates." | keep existing |
| Apply | 404 | `RESOURCE_NOT_FOUND` | "Leave balance not allocated for employee …" | keep existing (allocation missing for that year) |
| Approve | 400 | `LEAVE_ALREADY_PROCESSED` | "Leave request has already been processed." | refetch the row; it was approved/rejected elsewhere |
| Approve | 400 | `NOT_ALLOWED` | "You cannot approve your own leave." | disable Approve when `row.employeeId === currentUser.employeeId` if you have it, else show the message |
| Approve | 400 | `INVALID_DATE` | "Leave cannot be approved after its start date." | **disable Approve for past start dates** (see §6.2) |
| Approve | 422 (validation) | `VALIDATION_FAILED` | "Attendance already exists on {date}. Leave cannot be approved." | surface as-is; no retry |
| Approve | 404 | `RESOURCE_NOT_FOUND` | "Leave balance not allocated for employee …, leaveType …, year …." | configuration problem; surface as-is |
| Approve (REJECT) | 400 | `REASON_REQUIRED` | "Rejection reason is required." | keep the existing required-field rule |
| Cancel | 400 | `NOT_ALLOWED` | "Approved leave cannot be cancelled." | the Cancel button must only render for `PENDING` (already the case) |
| Cancel | 400 | `LEAVE_ALREADY_CANCELLED` | "Leave request is already cancelled." | refetch |

HTTP status for the validation-exception case is whatever the global handler maps `ValidationException` to — verify against the running API rather than hard-coding a branch on it.

---

## 10. Worked examples

**Apply** — succeeds even though the balance is smaller than the request:

```js
const created = await applyLeave({
  leaveTypeId: 1,
  startDate: "2026-11-02",
  endDate: "2026-11-06",   // 5 working days
  reason: "Personal",
});
// created.status === "PENDING", created.paidDays === null, created.approvedByName === null
```

**Approve** (manager token) — the response is the updated request, split already computed:

```js
const updated = await managerLeaveAction(318, "APPROVE", "");
// updated.status       === "APPROVED"
// updated.paidDays     === 2
// updated.lopDays      === 3
// updated.totalDays    === 5   (paidDays + lopDays === totalDays)
// updated.approvedByName === "Rohit Sharma"
```

**Refresh** the list and the balances after that call; do not derive new balance numbers locally.

---

## 11. Test checklist

Run against a backend with the migration applied (`V9__leave_request_paid_lop_and_approver.sql`).

- [ ] `POST /leave-requests` with `totalDays > remainingLeaves` → **201**, status `PENDING` (no `INSUFFICIENT`).
- [ ] Approve a 5-working-day request with a free allowance → `paidDays = 2`, `lopDays = 3`, `approvedByName` set; balance drops by **2 only**.
- [ ] Approve a second request in the same month after the allowance is spent → `paidDays = 0`, `lopDays = totalDays`; balance unchanged; `GET /leave-transactions/my` shows **no** new ledger row.
- [ ] Approve a request whose balance is smaller than the PAID allowance → `paidDays` equals the remaining balance (never more), balance is `0`, never negative.
- [ ] Employee list (`/my`) and manager list (`/team`, `/`) both return the new fields.
- [ ] Legacy approved row (approved before the migration) → `paidDays`, `lopDays`, `approvedBy*` all `null`; UI shows no chip.
- [ ] Unpaid leave type → `paidDays = totalDays`, `lopDays = 0`; UI shows no chip for it (gate on `leaveType.paid`).
- [ ] Second Approve on an already-approved request → `LEAVE_ALREADY_PROCESSED`, and the balance does **not** move again.
- [ ] Approve with a past `startDate` → `INVALID_DATE`; the UI has the button disabled.
- [ ] Cancel is offered only for `PENDING`; cancelling an approved request is impossible from the UI.

---

## 12. Known backend gaps & open questions

1. **The leave report does not expose the split** (§6.5) — no LOP columns/totals in reports or exports until the report DTOs are extended.
2. **Payroll computes LOP independently from attendance** (§6.6) — the two modules can disagree for the same month; do not aggregate them in the UI.
3. **Monthly usage is attributed to the request's start month.** `sumPaidLeaveDaysInMonth` filters `startDate` within the month, so a request spanning a month boundary contributes its *entire* `paidDays` to the start month when subsequent approvals compute the remaining allowance. The classifier itself walks months correctly, but later approvals in the *second* month can under-count consumption. Store/approve a cross-month request and check the allowance before relying on it in the UI.
4. **Approved leave cannot be cancelled**, and the balance-restore branch in `cancelLeave` is commented out — so a wrong split cannot be undone from the UI today. There is no "re-adjust split" action; treat the split as final.
5. **Legacy rows carry `null`** — covered above, but it also means aggregate reporting cannot distinguish "0 LOP" from "unknown".
6. **Verification status:** the backend change compiles and its unit tests pass (50 tests across the leave/attendance suites), but the migration + `ddl-auto=validate` combination and the `FOR UPDATE` behaviour have **not** been exercised against a real PostgreSQL database. When the frontend team starts integration testing, run the tests in §11 against a migrated database first.

---

## 13. Gotchas / checklist

- [ ] Never send `paidDays`, `lopDays` or `approvedBy*` — they are server-owned and ignored.
- [ ] A manager cannot approve their own leave; the approver always comes from the authenticated token.
- [ ] `null` split ≠ `0` split. Gate chips on `Number(row.lopDays) > 0`.
- [ ] `paidDays` means "deducted from the balance", not "paid in salary".
- [ ] Display the split only for `APPROVED` rows of a **paid** leave type.
- [ ] Do not predict the split client-side — the response is the only source of truth.
- [ ] Keep the Cancel button PENDING-only.
- [ ] Do not remove the existing `by …` approver rendering when refactoring the status cell.
- [ ] After approve/reject, refetch both the request list **and** the balances.
- [ ] If you add a leave-transaction/ledger view, note the ledger description is now `"Leave approved (N PAID day(s))"` and an all-LOP approval writes no row at all.

---

## Appendix — files touched by the backend change

| File | Change |
|---|---|
| [LeaveRequest.java](../../src/main/java/com/my_hourly/leave/entity/LeaveRequest.java#L53) | `paid_days`, `lop_days`, `approved_by` columns |
| [LeaveRequestResponse.java](../../src/main/java/com/my_hourly/leave/api/response/LeaveRequestResponse.java#L37) | 5 new response fields |
| [LeaveRequestMapper.java](../../src/main/java/com/my_hourly/leave/mapper/LeaveRequestMapper.java) | maps the new fields (null-safe) |
| [LeavePaidLopService.java](../../src/main/java/com/my_hourly/leave/service/LeavePaidLopService.java), [LeavePaidLopServiceImpl.java](../../src/main/java/com/my_hourly/leave/service/impl/LeavePaidLopServiceImpl.java) | new: the PAID/LOP classifier |
| [PaidLopAllocation.java](../../src/main/java/com/my_hourly/leave/dto/PaidLopAllocation.java) | new: the split value object (per-month breakdown) |
| [LeaveRequestServiceImpl.java](../../src/main/java/com/my_hourly/leave/service/impl/LeaveRequestServiceImpl.java#L267) | approval writes the split + approver; request row locked |
| [LeaveBalanceServiceImpl.java](../../src/main/java/com/my_hourly/leave/service/impl/LeaveBalanceServiceImpl.java#L166) | `deductPaidLeaveDays`, `getLeaveBalanceEntityForUpdate` |
| [LeaveValidationServiceImpl.java](../../src/main/java/com/my_hourly/leave/service/impl/LeaveValidationServiceImpl.java#L187) | submission no longer blocks on insufficient balance |
| [LeaveRequestRepository.java](../../src/main/java/com/my_hourly/leave/repository/LeaveRequestRepository.java#L124) | `sumPaidLeaveDaysInMonth`, `findByIdForUpdate`, `approvedBy` entity graph |
| [LeaveBalanceRepository.java](../../src/main/java/com/my_hourly/leave/repository/LeaveBalanceRepository.java#L36) | `...ForUpdate` balance lookup |
| [V9__leave_request_paid_lop_and_approver.sql](../../src/main/resources/db/migration/V9__leave_request_paid_lop_and_approver.sql) | migration: `paid_days`, `lop_days`, `approved_by`, FK + index |
