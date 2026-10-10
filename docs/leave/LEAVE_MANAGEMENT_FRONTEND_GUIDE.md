# Leave Management — Frontend Guide

A short, practical guide to the Leave module: the endpoints you call, the shapes
you get back, and the rules the backend enforces. Every statement below was
verified against the current backend source (controllers, DTOs, services,
repositories, security configuration, Flyway migrations and tests).

> **Note:** the PAID/LOP split used to live in a separate
> `LEAVE_PAID_LOP_FRONTEND_GUIDE.md`. That file no longer exists; its content is
> folded into [§6](#6-paidlop-and-balance-rules) and [§9](#9-leave-policy-settings)
> of this guide.

---

## 1. Basics

* **Base path:** `/api/v1` — all paths below are relative to it.
* **Auth:** send the JWT on every call — `Authorization: Bearer <token>`.
* **Roles** (`RoleName` enum, granted as Spring authorities `ROLE_<name>`):
  `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER`, `EMPLOYEE`, `PAYROLL_ADMIN`, `CLIENT`.
  Only the first four are used by any leave endpoint — never gate leave UI on
  `PAYROLL_ADMIN` or `CLIENT`.
* **Success envelope** (`ApiResponse<T>`; `timestamp` is always present):

  ```json
  { "success": true, "message": "Leave applied successfully.", "data": { },
    "timestamp": "2026-10-10T10:15:00" }
  ```

  List endpoints return `data` as a **plain JSON array** — there is no page
  wrapper. Pagination and sorting parameters are **not supported** anywhere in
  the leave module; the database decides row order, so sort client-side if the
  UI needs a specific order.

* **Error envelope** (`ApiError`, HTTP 4xx/5xx): read `message` to show the user
  and branch in code on `errorCode`:

  ```json
  { "success": false, "message": "You are not authorized to approve this leave.",
    "errorCode": "NOT_ALLOWED", "path": "/api/v1/leave-approvals/12/leave-approval-by-manager",
    "timestamp": "2026-10-10T10:15:00" }
  ```

* **Unauthenticated** calls are rejected before the controller with HTTP **401**
  and `errorCode: "UNAUTHORIZED"`; a valid token without the required role gets
  HTTP **403** with `errorCode: "ACCESS_DENIED"`. The 403 `message` differs
  depending on where the denial happened ("Access denied." from method security
  vs "You do not have permission to perform this action." from the filter
  chain), so **always branch on `errorCode`, never on the message**.

---

## 2. Roles, permissions and UI visibility

Backend authorization is enforced by `@PreAuthorize` on each controller method
(method security is enabled in `SecurityConfig`). Frontend visibility must be
built from the table below — **do not assume visibility and authorization are
the same**.

| Capability | EMPLOYEE | MANAGER | HR_ADMIN | SUPER_ADMIN |
|---|---|---|---|---|
| Apply for leave | ✅ | ✅ | ✅ | ✅ |
| View own requests (`/my`) | ✅ | ✅ | ✅ | ✅ |
| View one request by id | ✅ | ✅ | ✅ | ✅ |
| Cancel **own pending** request | ✅ | ✅ | ✅ | ✅ |
| Approve / reject (`/leave-approvals/...`) | ❌ | ✅ *(own reports only)* | ❌ | ❌ |
| View approval history | ✅ | ✅ | ✅ | ✅ |
| Team requests (`/team`) | ❌ | ✅ | ❌ | ✅ |
| All requests (`/leave-requests`) | ❌ | ✅ | ✅ | ✅ |
| Own balances (`/leave-balances/my`) | ✅ | ✅ | ✅ | ✅ |
| Another employee's balances | ❌ | ✅ | ✅ | ✅ |
| All balances / balance by id | ❌ | ✅ | ✅ | ✅ |
| Own leave ledger (`/leave-transactions/my`) | ✅ | ✅ | ✅ | ❌ |
| Another employee's ledger / per-request ledger | ❌ | ✅ | ✅ | ✅ |
| Read & update leave policy settings | ❌ | ✅ | ✅ | ✅ |
| Manage leave types / run allocation | ❌ | ✅ | ✅ | ❌ |

Key consequences for the UI:

* **HR_ADMIN and SUPER_ADMIN cannot approve or reject.** The manager-action
  endpoint is `hasRole('MANAGER')` only, and there is no HR action endpoint
  (it is commented out in the backend). HR/HR-admin screens must be
  **view-only** for approvals — show status and history, no Approve/Reject
  buttons.
* **A MANAGER can only act on their own direct reports** (see
  [§7](#7-approval-rejection-and-cancellation)); the `/team` endpoint and the
  Approve/Reject buttons follow the employee's `reportingManager`.
* **A MANAGER cannot approve their own leave** — hide/disable the action on
  their own rows.
* `/leave-transactions/my` excludes `SUPER_ADMIN`; all ledger endpoints exclude
  `EMPLOYEE` except `/my`.
* `SUPER_ADMIN` cannot manage leave types or allocations (those are
  `HR_ADMIN`/`MANAGER` only), despite what some Swagger summaries claim.

---

## 3. Endpoints

### 3.1 Leave types (`/leave-types`)

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `GET` | `/leave-types/active` | any authenticated | **Leave-type dropdown.** Active types only. |
| `GET` | `/leave-types` | any authenticated | All types (active + inactive). |
| `GET` | `/leave-types/{leaveTypeId}` | any authenticated | One type. |
| `POST` | `/leave-types` | HR_ADMIN, MANAGER | Create → **201**. |
| `PUT` | `/leave-types/{leaveTypeId}` | HR_ADMIN, MANAGER | Update. |
| `PATCH` | `/leave-types/{leaveTypeId}/activate` | HR_ADMIN, MANAGER | Activate. |
| `PATCH` | `/leave-types/{leaveTypeId}/deactivate` | HR_ADMIN, MANAGER | Deactivate. |

There is **no delete endpoint** (commented out in the controller).

### 3.2 Leave requests (`/leave-requests`)

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `POST` | `/leave-requests` | EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN | Apply → **201**. |
| `PUT` | `/leave-requests/{leaveRequestId}/cancel` | same four roles | Cancel own **pending** request. |
| `GET` | `/leave-requests/{leaveRequestId}` | same four roles | One request. |
| `GET` | `/leave-requests/my?month=&year=` | same four roles | My requests (optional month filter). |
| `GET` | `/leave-requests/team` | MANAGER, SUPER_ADMIN | Direct reports' requests (no filter). |
| `GET` | `/leave-requests?month=&year=` | HR_ADMIN, SUPER_ADMIN, MANAGER | All requests (optional month filter). |

### 3.3 Approvals (`/leave-approvals`)

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `PUT` | `/leave-approvals/{leaveRequestId}/leave-approval-by-manager` | **MANAGER only** | Approve / reject → updated request. |
| `GET` | `/leave-approvals/leave-request/{leaveRequestId}` | any authenticated | Approval / rejection history. |

### 3.4 Balances, ledger and allocation

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `GET` | `/leave-balances/my` | all four roles | My balances (all years, one row per leave type). |
| `GET` | `/leave-balances/employee/{employeeId}` | HR_ADMIN, SUPER_ADMIN, MANAGER | A team member's balances. |
| `GET` | `/leave-balances/{leaveBalanceId}` | HR_ADMIN, SUPER_ADMIN, MANAGER | One balance row. |
| `GET` | `/leave-balances` | HR_ADMIN, SUPER_ADMIN, MANAGER | Every balance row. |
| `GET` | `/leave-transactions/my` | EMPLOYEE, MANAGER, HR_ADMIN | My ledger. |
| `GET` | `/leave-transactions/employee/{employeeId}` | SUPER_ADMIN, HR_ADMIN, MANAGER | A team member's ledger. |
| `GET` | `/leave-transactions/leave-request/{leaveRequestId}` | SUPER_ADMIN, HR_ADMIN, MANAGER | Ledger rows for one request. |
| `POST` | `/leave-allocation/all-employee` | HR_ADMIN, MANAGER | Allocate/adjust the current year for all active employees. |
| `POST` | `/leave-allocation/leave-type/{leaveTypeId}` | HR_ADMIN, MANAGER | Re-sync balances after a leave type's allocated days changed. |

### 3.5 Leave settings (`/settings/leave`)

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `GET` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Read the leave policy. |
| `PUT` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Update the leave policy. |

### 3.6 Month filter (`month`, `year`)

Both are optional and **independent** on `/leave-requests/my` and
`/leave-requests`. Omit both for everything (backward compatible).

| Query | Result |
|---|---|
| `?month=10&year=2026` | Requests that **intersect** October 2026. |
| `?month=10` | October of the **current** year. |
| `?year=2026` | The whole of 2026. |
| *(none)* | Everything. |

The filter is an **overlap** test (`startDate <= periodEnd AND endDate >= periodStart`),
so a request spanning a month boundary appears under **both** months
(e.g. Oct 30 – Nov 2 shows in October and November). `/leave-requests/team` has
**no** month filter.

---

## 4. Payloads and response shapes

### 4.1 Apply — `POST /leave-requests` (`LeaveRequestRequest`)

```json
{
  "leaveTypeId": 1,
  "startDate": "2026-11-27",
  "endDate": "2026-11-30",
  "reason": "Family event"
}
```

| Field | Type | Required | Validation |
|---|---|---|---|
| `leaveTypeId` | number | yes | `@NotNull` — "Leave type is required." |
| `startDate` | `yyyy-MM-dd` | yes | `@NotNull`; must be **today or later**; not after `endDate`. |
| `endDate` | `yyyy-MM-dd` | yes | `@NotNull`; must be **today or later**; not before `startDate`. |
| `reason` | string | yes | `@NotBlank` + max **500** chars. Trimmed server-side. |

**Do not send** `employeeId`, `status`, `totalDays`, `paidDays`, `lopDays` or an
approver: the employee comes from the token, everything else is computed.

### 4.2 Manager action — `PUT /leave-approvals/{id}/leave-approval-by-manager` (`LeaveActionRequest`)

```json
{ "action": "APPROVE" }
```

```json
{ "action": "REJECT", "reason": "Insufficient team coverage that week" }
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `action` | `"APPROVE"` \| `"REJECT"` | yes | `@NotNull` — "Action is required." |
| `reason` | string | required for `REJECT`, optional for `APPROVE` | max 500. |

**The rejection reason field is `reason`** (there is no `rejectionReason`,
`remarks` or `comment`). The backend stores it as `remarks` on the approval
history row, so it comes back as `remarks` on `GET /leave-approvals/leave-request/{id}`.
A blank `reason` with `action: "REJECT"` fails bean validation with
`400 VALIDATION_FAILED` and message
`"Reason is required when rejecting a leave request."`

The approver is always taken from the JWT — **never send an approver**.

### 4.3 `LeaveRequestResponse`

| Field | Type | Notes |
|---|---|---|
| `id` | number | |
| `employeeId`, `employeeCode`, `employeeName` | number / string / string | Applicant. `employeeName` is `firstName + " " + lastName`. |
| `leaveTypeId`, `leaveType` | number / string | |
| `startDate`, `endDate` | `yyyy-MM-dd` | Inclusive. |
| `totalDays` | number | **Chargeable** days (weekends/holidays excluded, sandwich expansion included). |
| `reason` | string | |
| `status` | string | `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`. |
| `paidDays`, `lopDays` | number \| null | `null` until **approved** (also `null` for rejected/cancelled). `paidDays + lopDays == totalDays`. |
| `approvedById`, `approvedByName`, `approvedByCode` | null until approved | Set only on **approve**, not on reject. `approvedByName` omits a blank last name. |

Example (approved, Friday→Monday under the Friday+Monday sandwich rule):

```json
{
  "success": true,
  "message": "Leave requests fetched successfully.",
  "data": [
    {
      "id": 41,
      "employeeId": 101,
      "employeeCode": "EMP-0101",
      "employeeName": "Jitendra Prajapati",
      "leaveTypeId": 1,
      "leaveType": "Annual Leave",
      "startDate": "2026-11-27",
      "endDate": "2026-11-30",
      "totalDays": 4,
      "reason": "Family event",
      "status": "APPROVED",
      "paidDays": 2,
      "lopDays": 2,
      "approvedById": 7,
      "approvedByName": "Rohit Sharma",
      "approvedByCode": "EMP-0007"
    }
  ],
  "timestamp": "2026-10-10T10:15:00"
}
```

### 4.4 `LeaveApprovalResponse` (approval history, oldest first)

```json
{
  "id": 55,
  "approvedById": 7,
  "approvedByName": "Rohit Sharma",
  "approvalLevel": "MANAGER",
  "action": "REJECT",
  "remarks": "Insufficient team coverage that week",
  "createdAt": "2026-10-09T14:22:05"
}
```

`approvalLevel` is always `MANAGER` today (`HR` exists in the enum but no HR
action path writes it). `action` is `APPROVE` or `REJECT`. `remarks` holds the
approve reason / reject reason.

### 4.5 `LeaveBalanceResponse`

| Field | Notes |
|---|---|
| `id`, `employeeId`, `employeeCode`, `employeeName` | |
| `leaveTypeId`, `leaveType` | |
| `year` | |
| `allocatedLeaves` | Annual allocation (prorated for a mid-year joiner). |
| `usedLeaves` | Approved days deducted so far. |
| `expiredLeaves` | Always `0` — nothing in the backend increments it (see §6.5). |
| `remainingLeaves` | `allocatedLeaves - usedLeaves - expiredLeaves`; never negative. |

### 4.6 `LeaveTransactionResponse` (ledger row)

`id`, `leaveType`, `transactionType`, `days`, `balanceBefore`, `balanceAfter`,
`remarks`, `createdAt`.

`transactionType` (`LeaveTransactionType`): `ALLOCATION`,
`LEAVE_APPROVED`, `LEAVE_CANCELLED`, `HR_ADJUSTMENT`, `CARRY_FORWARD`,
`EXPIRED`, `EXPIRY`, `ALLOCATION_ADJUSTED`. Only `ALLOCATION`,
`LEAVE_APPROVED` and `ALLOCATION_ADJUSTED` are written by the current leave
flows; `LEAVE_CANCELLED` / `EXPIRY` exist in code paths that are not reachable
from the API today.

### 4.7 `LeaveTypeResponse`

`id`, `name`, `description`, `paid`, `allocatedDays`, `monthlyGuideline`,
`carryForwardAllowed`, `active`.

`LeaveTypeRequest` (create/update): `name` required, max 50, unique
(case-insensitive); `description` max 255; `paid` required; `allocatedDays`
required, `>= 0`; `monthlyGuideline` optional (`>= 0`, defaults to 2);
`carryForwardAllowed` optional boolean (default `false`). New types are always
created `active = true`.

---

## 5. Pages and what to show

### 5.1 My Requests
* Endpoint: `GET /leave-requests/my?month=&year=` (own requests only).
* Columns: leave type, start–end, `totalDays`, status, and — once approved —
  `paidDays` / `lopDays`.
* Filters: month/year (server-side). Status filter can be client-side; the
  backend has no status query parameter.
* Status chips: `PENDING` (amber), `APPROVED` (green), `REJECTED` (red),
  `CANCELLED` (grey).
* Actions: **Cancel** only on `PENDING` rows owned by the current user.

### 5.2 Apply for Leave
* Populate the type dropdown from `GET /leave-types/active`.
* Fields: leave type, start date, end date, reason (max 500, required).
* Client-side guards mirroring the server (see §6.4): `startDate <= endDate`
  and **no past dates**; block submit while a request is in flight (a
  double-submit is rejected by the backend, not de-duplicated).
* Show the returned `totalDays` as the chargeable day count. The backend
  provides **no preview/quote endpoint** — days are only known after `POST`.
* Never compute or submit `totalDays`, `paidDays` or `lopDays`.

### 5.3 Team Requests (MANAGER, SUPER_ADMIN)
* Endpoint: `GET /leave-requests/team` — every request of the manager's direct
  reports, all statuses, **no month filter**.
* Columns: employee, leave type, dates, `totalDays`, status, paid/LOP, action.
* Actions: **Approve** / **Reject** for `PENDING` rows only, and never on the
  manager's own row (the API refuses that).

### 5.4 All Leave Requests (HR_ADMIN, SUPER_ADMIN, MANAGER)
* Endpoint: `GET /leave-requests?month=&year=` (overlap filter).
* Read-only for HR_ADMIN / SUPER_ADMIN. MANAGER rows may expose Approve/Reject
  only where the manager is the applicant's reporting manager; every other row
  must fail gracefully if attempted.

### 5.5 Leave Balance
* `GET /leave-balances/my` → current user, all years, one row per leave type.
* `GET /leave-balances/employee/{employeeId}` → HR/manager view.
* Columns: leave type, year, `allocatedLeaves`, `usedLeaves`,
  `remainingLeaves` (and `expiredLeaves`, which is always 0).
* Do not compute the balance client-side; show the returned values.

### 5.6 Leave Request Details + Approval History
* `GET /leave-requests/{id}` for the request.
* `GET /leave-approvals/leave-request/{id}` for the timeline (oldest first):
  actor, action, remarks, timestamp.
* On a `REJECTED` request the reason lives in the history `remarks`; the
  request object itself has no rejection-reason field.
* Optional extra tab: `GET /leave-transactions/leave-request/{id}` (HR/manager)
  for the resulting balance movements.

### 5.7 Leave Policy Settings (SUPER_ADMIN, HR_ADMIN, MANAGER)
* `GET /settings/leave` to load, `PUT /settings/leave` to save — see
  [§9](#9-leave-policy-settings).

---

## 6. PAID/LOP and balance rules

### 6.1 Working days
`totalDays` counts **weekdays only** — weekends and **public holidays** inside
the range are skipped (holidays come from the `holidays` table). If the range
contains **no** chargeable day the request is rejected with
`400` / `RESOURCE_NOT_FOUND` and message
`"No working days found between selected dates."`

### 6.2 Sandwich leave (all three settings are configurable)
Sandwich settings come from `GET /settings/leave` and are evaluated at
**submission** time; the frontend always submits the plain `startDate`/`endDate`
range and the backend computes the chargeable days.

| Setting | When leave is taken on… | Expanded range | Charged days |
|---|---|---|---|
| `sandwichLeaveFridayMondayEnabled` | Friday **and** Monday both inside the request | unchanged (Fri→Mon) | **4** (Fri, Sat, Sun, Mon) |
| `sandwichLeaveFridayEnabled` | Friday (no Monday in range) | end extended to the following Sunday | **3** (Fri, Sat, Sun) |
| `sandwichLeaveMondayEnabled` | Monday (no Friday in range) | start moved back to the preceding Saturday | **3** (Sat, Sun, Mon) |

Rules verified in the implementation and pinned by
`LeaveSandwichPolicyTest`:

* Precedence is **Friday+Monday → Friday-only → Monday-only**. The Friday+Monday
  rule suppresses the other two, and the forced weekend days are stored in a
  `Set`, so the weekend is counted **once** (Friday→Monday is 4, never 6).
* The rules are independent of each other: with only the Monday rule on, a
  Friday-only request is unaffected, and vice versa.
* The forced Sat/Sun are charged **even if they are public holidays** — they are
  forced working days and are always counted.
* With all rules disabled (the defaults), a Friday→Monday request charges only
  the two working days.
* If the settings row cannot be read at submission, sandwich expansion is
  skipped (logged) rather than failing the request.

### 6.3 PAID vs LOP
* `monthlyGuideline` (leave settings, fallback to the leave type's value, hard
  fallback 2) is **one shared allowance per employee per calendar month across
  all paid leave types**.
* On approval of a **paid** leave type, each working day is walked in date
  order. A day is **PAID** while *both* the month's remaining allowance *> 0*
  **and** the annual balance for that day's year *> 0*; otherwise it is **LOP**.
* **PAID days reduce the annual balance. LOP days never touch it, and the
  balance can never go negative.**
* Already-approved PAID days in the same month (counted by the date each day
  actually falls on, all paid leave types) are subtracted from the allowance
  first, so the allowance is shared across requests.
* A request that **crosses a month boundary** gets each month's own allowance,
  and the per-month split is stored so later approvals in the second month see
  the reduced allowance.
* A **cross-year** request deducts each year's PAID days from that year's own
  balance.
* `monthlyGuideline = 0` makes every day LOP. A guideline larger than the
  request lets every working day be PAID (subject to the balance).
* An **unpaid** leave type keeps the legacy behaviour: every approved day is
  deducted from its annual balance, **clamped** to what is available; days the
  balance cannot cover are recorded as LOP. So `paidDays` here means
  "deducted from the annual balance", **not** "salaried" — unpaid leave is
  still unpaid in payroll.

`paidDays` / `lopDays` are produced **by the backend at approval only**. There
is no endpoint that previews them, and the frontend must never invent them.

### 6.4 Submission rules (`POST /leave-requests`)
* `startDate <= endDate`, and **neither date may be in the past** (today is OK).
  Errors: `400` / `VALIDATION_FAILED`, messages
  `"Start date cannot be after end date."`, `"Start date cannot be before today."`,
  `"End date cannot be before today."`
* The leave type must exist and be **active**:
  `400` / `NOT_ALLOWED` — `"Selected leave type is inactive."`
* No overlap with another **PENDING or APPROVED** request:
  `400` / `LEAVE_ALREADY_EXIST` — `"Another pending or approved leave request overlaps the selected dates."`
* The range must contain at least one chargeable day (§6.1).
* A leave balance row for the **current year** must exist
  (`404` / `RESOURCE_NOT_FOUND`) — a missing allocation is a configuration
  problem, not a user error. Note this check uses the current year, not the
  request's year; the balance that is actually deducted is resolved per
  calendar year at approval (a cross-year request therefore also needs the
  following year's allocation to exist).
* **Over-balance is allowed.** Excess days become LOP at approval; there is no
  "insufficient balance" submission error and the frontend must not block on it.
* On success the backend emails the reporting manager; a mail failure is logged
  and never fails the request.

### 6.5 Expiry, carry-forward and annual allocation
* The month-end job (`LeaveScheduler`, last day 23:30) is **report-only**: it
  logs how much of each employee's monthly guideline went unused. It performs
  **no balance writes** — no deduction, no `expiredLeaves` increment, no ledger
  row — so runs are idempotent and `expiredLeaves` stays `0`.
  Unused allowance simply lapses because the next month recomputes it from
  `LeaveSettings`.
* `carryForwardAllowed = true` **skips** the month-end report ("Global
  carry-forward is enabled"); it does not credit anything into the annual
  balance. Either way, the annual balance is only ever changed by allocation,
  approval (PAID days) and an allocation adjustment.
* Annual allocation runs on **1 January at 00:00** for all active employees,
  and can be triggered manually via `POST /leave-allocation/all-employee`.
  A new employee's first-year allocation is prorated by remaining months
  (`allocatedDays * (13 - joiningMonth) / 12`). A mid-year joiner gets no
  automatic re-allocation when their balance already exists.
* Changing a leave type's `allocatedDays` does **not** silently re-allocate;
  call `POST /leave-allocation/leave-type/{leaveTypeId}` to sync all employees
  (an existing balance is adjusted by the delta, and `remainingLeaves` is
  clamped to `0` if the reduction would push it negative).

### 6.6 When values become available / how to refresh
* `paidDays`, `lopDays` and `approvedBy*` are `null` on `PENDING` (and on
  `REJECTED` / `CANCELLED`) and are populated by the **approve** call.
* After Approve/Reject/Cancel the response body **is** the updated request, so
  the caller can update that row directly. Balances, the ledger and team/all
  lists are **not pushed** — refetch `GET /leave-balances/my`,
  `GET /leave-transactions/my` and the relevant list after a successful action,
  or invalidate the relevant queries.

---

## 7. Approval, rejection and cancellation

### 7.1 The status lifecycle

```
PENDING ──approve──▶ APPROVED
PENDING ──reject───▶ REJECTED
PENDING ──cancel───▶ CANCELLED
```

`APPROVED`, `REJECTED` and `CANCELLED` are terminal. `MANAGER_APPROVED` /
`HR_APPROVED` exist only as commented-out code and are **never** returned.

### 7.2 Approve / reject availability (`PUT /leave-approvals/{id}/leave-approval-by-manager`)
Checks run in this order; the first failure is the response:

1. Caller must hold role `MANAGER` → otherwise `403 ACCESS_DENIED`.
2. The request must exist → `404` / `RESOURCE_NOT_FOUND` — `"Leave Request id: <id>"`.
3. Status must be `PENDING` → `400` / `LEAVE_ALREADY_PROCESSED` —
   `"Leave request has already been processed."` (a double-click or retry hits
   this; approval is also serialized with a row lock, so a race cannot deduct
   twice).
4. The manager cannot act on their **own** request → `400` / `NOT_ALLOWED` —
   `"You cannot approve your own leave."`
5. The caller must be the applicant's **reporting manager**:
   `400` / `REPORTING_MANAGER_NOT_ASSIGNED` — `"Reporting manager is not assigned."`,
   or `400` / `NOT_ALLOWED` — `"You are not authorized to approve this leave."`
6. **Attendance conflict**: if attendance already exists on **any** date in
   `[startDate, endDate]` (including weekends inside the range), or on a
   sandwich-forced weekend outside it → `400` / `VALIDATION_FAILED` —
   `"Attendance already exists on <date>. Leave cannot be approved."`
7. The start date must not be in the past → `400` / `INVALID_DATE` —
   `"Leave cannot be approved after its start date."`
8. `APPROVE` classifies and deducts PAID days, writes the per-month breakdown,
   records the approver, marks attendance as leave and sets `APPROVED`.
   `REJECT` requires a non-blank `reason` and sets `REJECTED` (no balance
   change, no approver on the request).

Both outcomes append a `leave_approvals` row (level `MANAGER`, action, remarks)
and email the employee; mail failures are logged and never fail the action.

**Frontend: confirm before approving** when the request will not be fully PAID
(days beyond the monthly guideline or the remaining balance become LOP) — but
remember the exact split is only known after approval.

### 7.3 Cancellation (`PUT /leave-requests/{id}/cancel`)
* Only the **owner** may cancel: otherwise `400` / `NOT_ALLOWED` —
  `"You cannot cancel another employee's leave."`
* Effectively only `PENDING` can be cancelled:
  * already cancelled → `400` / `LEAVE_ALREADY_CANCELLED` —
    `"Leave request is already cancelled."`
  * rejected → `400` / `NOT_ALLOWED` — `"Rejected leave cannot be cancelled."`
  * approved → `400` / `NOT_ALLOWED` — `"Approved leave cannot be cancelled."`
* **Cancelling never restores a balance and never removes attendance** (that
  code is commented out; it is unnecessary because approved leave cannot be
  cancelled). Do not expect `remainingLeaves` or the ledger to change.
* A cancellation email is sent to the reporting manager; failures are logged.

### 7.4 Leave-type management rules
* Creating/updating rejects a duplicate name (case-insensitive):
  `400` / `LEAVE_ALREADY_EXIST` — `"Leave type with name '<name>' already exists..."`
* Activating an active type, or deactivating an inactive one, is rejected with
  `400` / `LEAVE_ALREADY_EXIST` — `"Leave type is already active."` /
  `"Leave type is already inactive."`

---

## 8. Errors, status codes and frontend states

| HTTP | `errorCode` | Raised when |
|---|---|---|
| 200 | — | Successful read / update / cancel / approve / reject. |
| 201 | — | `POST /leave-requests`, `POST /leave-types`. |
| 400 | `VALIDATION_FAILED` | Bean-validation failures, invalid date range, past dates, no working days, attendance conflict. |
| 400 | `NOT_ALLOWED` | Inactive leave type, foreign cancellation, approved/rejected cancellation, manager not authorized, approving own leave. |
| 400 | `LEAVE_ALREADY_EXIST` | Overlapping pending/approved request; duplicate leave-type name; activate/deactivate no-op. |
| 400 | `LEAVE_ALREADY_PROCESSED` | Acting on a request that is no longer `PENDING`. |
| 400 | `LEAVE_ALREADY_CANCELLED` | Cancelling an already cancelled request. |
| 400 | `INVALID_DATE` | Approving a request whose start date has passed. |
| 400 | `REASON_REQUIRED` | Service-level backstop for a blank reject reason (bean validation normally fires first with `VALIDATION_FAILED`). |
| 400 | `REPORTING_MANAGER_NOT_ASSIGNED` | Applicant has no reporting manager. |
| 400 | `INVALID_REQUEST` | Malformed JSON / unparseable enum or date. |
| 401 | `UNAUTHORIZED` | Missing / invalid token. |
| 403 | `ACCESS_DENIED` | Authenticated but wrong role. |
| 404 | `RESOURCE_NOT_FOUND` | Unknown request / leave type / balance, or a missing balance allocation. |
| 500 | `INTERNAL_SERVER_ERROR` | Anything unmapped; message is prefixed `"An unexpected error occurred."` |

`ErrorCode` also declares `LEAVE_NOT_FOUND`, `LEAVE_BALANCE_EXCEEDED`,
`INSUFFICIENT`, `APPROVAL_PENDING` and `ON_LEAVE` — **no leave endpoint returns
them today**, so do not build UI branches for them.

Presentation guidance (recommended, not implemented by the backend):

* **Loading:** per-list/field skeletons; disable submit and Approve/Reject while
  a mutation is in flight.
* **Empty lists:** the API returns `200` with `data: []` — show an empty state,
  not an error.
* **Validation (400):** show `message` inline near the form and, for
  `<date>`-specific attendance conflicts, next to the date field.
* **401:** sign the user out and route to login.
* **403:** hide/disable the offending action; treat as a UI-permission bug, not
  a user error.
* **404:** show a "request not found" state and refresh the list.
* **500 / network failure:** toast + retry with backoff; keep the form state.
* **Overlap (`LEAVE_ALREADY_EXIST`) on submit:** re-fetch `/leave-requests/my`
  and highlight the clashing request.
* **`LEAVE_ALREADY_PROCESSED`:** another approver beat this one; refresh the row
  and show the new status instead of an error.

> **Needs backend confirmation:** `month` / `year` are range-checked twice — by
> `@Min/@Max` on the controller parameters and again in
> `LeaveSpecification.resolveRange` (which raises `400 VALIDATION_FAILED`).
> The global exception handler has no mapping for framework-level
> parameter-validation failures, so an out-of-range value may surface as the
> generic `500` envelope instead of a clean `400`. Keep the UI range-limited and
> do not rely on a specific status for out-of-range input.

---

## 9. Leave policy settings

`GET /settings/leave` returns the single active row (`findFirstByActiveTrue`).
If no row exists the API answers `404` / `RESOURCE_NOT_FOUND` —
`"Leave settings not found."`

| Field | Type | Meaning | Default | Validation on save | Suggested control |
|---|---|---|---|---|---|
| `carryForwardAllowed` | boolean | `true` = the month-end allowance review is skipped (nothing is reported as lapsing); `false` = the unused part of the monthly guideline is reported. Never credits the annual balance. | no DB default — must be sent | `@NotNull` | Toggle |
| `monthlyGuideline` | number | Paid days per employee **per calendar month**, shared across paid leave types (drives the PAID/LOP split). `0` = all LOP. | `2` | `@NotNull`, `@Min(0)` | Number input, min 0 |
| `annualPaidLeave` | number | Fallback annual allocation used only when a **paid** leave type has `allocatedDays <= 0`; a leave type's own value always wins. | `24` | `@NotNull`, `@Min(0)` | Number input, min 0 |
| `sandwichLeaveMondayEnabled` | boolean | Monday leave also charges the preceding Sat+Sun (3 days). | `false` | optional — omitted/null is stored as `false` | Toggle |
| `sandwichLeaveFridayEnabled` | boolean | Friday leave also charges the following Sat+Sun (3 days). | `false` | optional — omitted/null is stored as `false` | Toggle |
| `sandwichLeaveFridayMondayEnabled` | boolean | Friday→Monday leave charges the intervening weekend once (4 days); takes precedence over the two rules above. | `false` | optional — omitted/null is stored as `false` | Toggle |
| `active` | boolean | Read-only flag from the settings base entity. | `true` | not accepted in the request | Display only |
| `id` | number | Row id. | — | not accepted in the request | Display only |

Save payload (`PUT /settings/leave` — all three toggles should be sent; omitting
one resets it to `false`):

```json
{
  "carryForwardAllowed": false,
  "monthlyGuideline": 2,
  "annualPaidLeave": 24,
  "sandwichLeaveMondayEnabled": true,
  "sandwichLeaveFridayEnabled": true,
  "sandwichLeaveFridayMondayEnabled": true
}
```

The response is the saved `LeaveSettingsResponse` (`id`, `carryForwardAllowed`,
`monthlyGuideline`, `annualPaidLeave`, the three sandwich flags, `active`).
Settings apply from the next submission/approval — pending requests are **not**
re-evaluated when settings change.

**Do not invent settings that are not in this table.** Fields such as
`halfDayLeaveAllowed`, `minimumAdvanceNoticeDays`, `maximumConsecutiveLeaveDays`,
`managerApprovalRequired`, `hrApprovalRequired`, `autoApproveLeave`,
`allowLeaveOnHoliday`, `allowLeaveOnWeekend`, `allowNegativeLeaveBalance` and
`allowBackdatedLeaveApplication` are commented out in the entity/DTO and are
**not** exposed by the API.

---

## 10. Frontend testing checklist

**Application**
- [ ] Valid single-day, multi-day and cross-month request is created (201) and shows the returned `totalDays`.
- [ ] Past `startDate`, `endDate < startDate`, missing reason, reason > 500 chars → inline `400` validation messages.
- [ ] Inactive leave type is not offered in the dropdown, and a stale id is rejected with `NOT_ALLOWED`.
- [ ] A range of only weekends/holidays is rejected with "No working days found…".
- [ ] Overlapping pending/approved dates are rejected (`LEAVE_ALREADY_EXIST`) and the clashing request is highlighted.
- [ ] A request exceeding the monthly guideline / balance is still submittable and warns about likely LOP.

**Sandwich leave**
- [ ] Monday-only, Friday-only and Friday→Monday each charge 3 / 3 / 4 days with the matching setting on.
- [ ] Friday→Monday with all three settings on still charges 4 (weekend counted once).
- [ ] With all settings off, Friday→Monday charges only the working days.
- [ ] A forced Saturday that is a public holiday is still charged.

**PAID / LOP and balances**
- [ ] 1st approval in a month: days within the guideline are PAID, the rest LOP.
- [ ] 2nd request in the same month gets only the leftover allowance.
- [ ] A cross-month request is PAID per month (October allowance does not leak into November).
- [ ] `paidDays + lopDays == totalDays` on every approved request.
- [ ] `remainingLeaves` drops by exactly `paidDays`, never below 0; LOP-only requests leave the balance unchanged.
- [ ] Balance and ledger refresh after approval; `paidDays`/`lopDays` are `null` while pending.
- [ ] `expiredLeaves` stays 0 after a month-end run.

**Approval / rejection / cancellation**
- [ ] Approve and Reject succeed with the correct payload; Reject without a reason is blocked client-side and by the API.
- [ ] Approve on a non-PENDING request → `LEAVE_ALREADY_PROCESSED` and the row refreshes.
- [ ] A manager cannot act on their own request or on a non-report's request.
- [ ] Approval is blocked when attendance exists on any date (incl. a sandwich weekend).
- [ ] Approval after the start date → `INVALID_DATE`.
- [ ] Cancel works on own PENDING request; approved/rejected/cancelled/foreign rows show the right error and no balance change.
- [ ] Approval history shows one entry per action with actor, action, remarks and timestamp.

**Roles**
- [ ] EMPLOYEE cannot see Team/All/other balances/other ledgers/settings/allocation.
- [ ] MANAGER sees Team with Approve/Reject but only for direct reports.
- [ ] HR_ADMIN / SUPER_ADMIN can view everything but have **no** Approve/Reject controls.
- [ ] `SUPER_ADMIN` does not see leave-type or allocation actions; `/leave-transactions/my` is unavailable to them.

**Settings and states**
- [ ] GET/PUT round-trips all six policy fields and the toggles take effect on the next submission/approval.
- [ ] `404` when settings are missing shows a config error, not an empty form.
- [ ] Empty lists render an empty state; loading skeletons and disabled submits prevent double effects.
- [ ] 401 → re-login; 403 → hide the action; 500/offline → retry with the form preserved.
