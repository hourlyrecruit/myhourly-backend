# Leave Management — Frontend Guide

A short, practical guide to the Leave module: the endpoints you call, the shapes
you get back, and the rules the backend enforces. For the deep dive on the
PAID/LOP split, see [LEAVE_PAID_LOP_FRONTEND_GUIDE.md](LEAVE_PAID_LOP_FRONTEND_GUIDE.md).

---

## 1. Basics

* **Base path:** `/api/v1` — all paths below are relative to it.
* **Auth:** send the JWT on every call — `Authorization: Bearer <token>`.
* **Roles:** `EMPLOYEE`, `MANAGER`, `HR_ADMIN`, `SUPER_ADMIN`.
* **Success envelope:**

  ```json
  { "success": true, "message": "…", "data": { }, "timestamp": "2026-10-10T10:15:00" }
  ```

* **Error envelope** (4xx/5xx), read `message` to show the user and `errorCode`
  to branch in code:

  ```json
  { "success": false, "message": "…", "errorCode": "VALIDATION_FAILED",
    "path": "/api/v1/leave-requests", "timestamp": "…" }
  ```

---

## 2. Endpoints you need

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `GET` | `/leave-types/active` | any authenticated | Populate the leave-type dropdown. |
| `POST` | `/leave-requests` | EMPLOYEE, MANAGER, HR_ADMIN, SUPER_ADMIN | Apply for leave → **201**. |
| `PUT` | `/leave-requests/{id}/cancel` | same as apply | Cancel **own pending** request. |
| `GET` | `/leave-requests/my?month=&year=` | any authenticated | My requests (optional month filter). |
| `GET` | `/leave-requests/team` | MANAGER, SUPER_ADMIN | Requests of my direct reports. |
| `GET` | `/leave-requests?month=&year=` | HR_ADMIN, SUPER_ADMIN, MANAGER | All requests (optional month filter). |
| `GET` | `/leave-requests/{id}` | any authenticated | One request. |
| `PUT` | `/leave-approvals/{id}/leave-approval-by-manager` | **MANAGER only** | Approve / reject → returns the updated request. |
| `GET` | `/leave-approvals/leave-request/{id}` | any authenticated | Approval / rejection history. |
| `GET` | `/leave-balances/my` | any authenticated | My balances (one row per leave type). |
| `GET` | `/leave-balances/employee/{employeeId}` | HR_ADMIN, SUPER_ADMIN, MANAGER | Balances of a team member. |
| `GET` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Read leave policy settings. |
| `PUT` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Update leave policy settings. |

Optional extras: `GET /leave-transactions/my` (leave ledger) and
`GET /leave-balances` (all balances, HR/manager views).

### Month filter (`month`, `year`)

Both are optional; **omit both for everything** (backward compatible).

| Query | Result |
|---|---|
| `?month=10&year=2026` | Requests that **intersect** October 2026. |
| `?month=10` | October of the **current** year. |
| `?year=2026` | The whole of 2026. |
| *(none)* | Everything. |

A request that spans a month boundary appears under **both** months (e.g. Oct 30
– Nov 2 shows in October and November). `month` must be 1–12 and `year`
2000–2100, otherwise `400 VALIDATION_FAILED`.

---

## 3. Payloads

### Apply — `POST /leave-requests`

```json
{ "leaveTypeId": 1, "startDate": "2026-11-27", "endDate": "2026-11-30",
  "reason": "Family event" }
```

`reason` is required, max 500 chars. `startDate`/`endDate` are `yyyy-MM-dd`.

### Manager action — `PUT /leave-approvals/{id}/leave-approval-by-manager`

```json
{ "action": "APPROVE" }                       // or "REJECT" (reason required)
```

The approver is taken from the token — **never send an approver**.

### `LeaveRequestResponse` (the main object)

| Field | Notes |
|---|---|
| `id`, `employeeId`, `employeeCode`, `employeeName` | Who applied. |
| `leaveTypeId`, `leaveType` | Leave type. |
| `startDate`, `endDate`, `totalDays` | `totalDays` is **chargeable** working days (see rules). |
| `reason` | |
| `status` | `PENDING`, `APPROVED`, `REJECTED`, `CANCELLED`. |
| `paidDays`, `lopDays` | `null` until approved; `paidDays + lopDays == totalDays`. |
| `approvedById`, `approvedByName`, `approvedByCode` | `null` until approved. |

### `LeaveBalanceResponse`

`leaveTypeId`, `leaveType`, `year`, `allocatedLeaves`, `usedLeaves`,
`expiredLeaves`, `remainingLeaves`.

### Leave settings (`GET/PUT /settings/leave`)

`carryForwardAllowed`, `monthlyGuideline`, `annualPaidLeave`, and the three
sandwich toggles `sandwichLeaveMondayEnabled`, `sandwichLeaveFridayEnabled`,
`sandwichLeaveFridayMondayEnabled`.

---

## 4. Business rules

### 4.1 Working days and sandwich leave

`totalDays` counts **weekdays only** — weekends and public holidays inside the
range are skipped. Sandwich settings expand this:

| Setting | When leave is taken on… | Charged days |
|---|---|---|
| **Monday** | Monday | Sat + Sun + Mon = **3** |
| **Friday** | Friday | Fri + Sat + Sun = **3** |
| **Friday + Monday** | Friday **and** Monday | Fri + Sat + Sun + Mon = **4** |

These are independent. If the Friday+Monday rule fires it takes precedence; the
weekend is counted **once**. A leave of Friday→Monday charges **4 days**, not 6.
The forced weekend days are treated as normal working days for balance and LOP
(they are charged even if they fall on a public holiday).

### 4.2 PAID vs LOP (important)

* The monthly guideline (`monthlyGuideline`, e.g. 2) is a **single shared
  allowance per employee per calendar month across all paid leave types**.
* At approval each working day in date order is **PAID** while the month still
  has allowance **and** the annual balance still has days; otherwise it is
  **LOP** (Loss of Pay).
* **PAID days reduce the balance. LOP days do not.** The balance never goes
  negative.
* Prior PAID days already approved that month are subtracted first, so the
  allowance is shared across requests.
* `paidDays` means "deducted from the annual balance", **not** "salaried". Unpaid
  leave types store `paidDays = totalDays`, `lopDays = 0` yet are unpaid in payroll.

### 4.3 Submission rules (`POST`)

* `startDate <= endDate`; dates cannot be in the past.
* The leave type must be **active**.
* No overlap with another **pending or approved** request.
* The range must contain at least one chargeable day.
* **Over-balance is allowed** — excess becomes LOP at approval. There is no
  "insufficient balance" submission error any more; do not block the user on it.
* A missing balance row is still an error (`RESOURCE_NOT_FOUND`) — a config problem.

### 4.4 Approval rules (`MANAGER`)

* Only the employee's **own reporting manager** may act.
* A manager **cannot approve their own** leave.
* Only a **PENDING** request can be processed — a second click fails with
  `LEAVE_ALREADY_PROCESSED`.
* Cannot approve **after the start date**.
* Approval fails if attendance already exists on any leave date (including
  sandwich weekends).
* `APPROVE` sets the PAID/LOP split, deducts PAID days, marks attendance.
  `REJECT` requires a non-blank `reason`.

### 4.5 Cancellation

Only your **own** request, and only while **PENDING**. Approved, rejected, and
already-cancelled requests cannot be cancelled.

### 4.6 Status lifecycle

```
PENDING ──approve──▶ APPROVED
PENDING ──reject───▶ REJECTED
PENDING ──cancel───▶ CANCELLED
```

---

## 5. Frontend checklist

* Handle the envelope: read `data` on success, `message`/`errorCode` on error.
* Show `totalDays` as the chargeable day count and, once approved, the
  `paidDays` / `lopDays` split. Never send these fields.
* Warn the approver **before confirming** that days beyond the monthly guideline
  or the remaining balance become LOP.
* Send sandwich leave as the plain `startDate`/`endDate` range — the backend
  calculates the chargeable days.
* Use `month`/`year` on `/leave-requests/my` (and the HR list) instead of
  filtering the full list client-side.
* Don't gate submission on balance; the backend classifies the excess as LOP.
