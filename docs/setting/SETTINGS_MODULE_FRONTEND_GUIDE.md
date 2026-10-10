# Settings Module — Frontend Implementation Guide

A short, practical guide to the Settings module: what the backend actually
exposes, the exact payloads, the validation it enforces, and what each setting
changes elsewhere in the system. Every statement was verified against the
current backend source (`com.my_hourly.settings.*`, `SettingController`,
`DataInitializer`, plus every module that reads a settings row).

---

## 1. Basics

* **Base path:** `/api/v1/settings` — all paths below are relative to it.
* **Auth:** send the JWT on every call — `Authorization: Bearer <token>`.
* **Roles:** `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER`, `EMPLOYEE`, `PAYROLL_ADMIN`,
  `CLIENT`. **All six settings endpoints require
  `SUPER_ADMIN` / `HR_ADMIN` / `MANAGER`** — an `EMPLOYEE` token gets `403`.
* **Success envelope** (`ApiResponse<T>`; `timestamp` always present):

  ```json
  { "success": true, "message": "Attendance settings fetched successfully.",
    "data": { }, "timestamp": "2026-10-10T10:15:00" }
  ```

* **Error envelope** (`ApiError`): branch on `errorCode`, show `message`:

  ```json
  { "success": false, "message": "Phone number must be a valid 10-digit Indian mobile number.",
    "errorCode": "VALIDATION_FAILED", "path": "/api/v1/settings/company",
    "timestamp": "2026-10-10T10:15:00" }
  ```

* **401** (`UNAUTHORIZED`) = missing/invalid token; **403** (`ACCESS_DENIED`) =
  authenticated but wrong role.

---

## 2. Scope — what exists today

The `settings` package holds **three live submodules** and **two disabled ones**.

| Submodule | Table | Endpoints | Build UI? |
|---|---|---|---|
| `settings.attendance.*` | `attendance_settings` | `GET`/`PUT /settings/attendance` | **Yes** |
| `settings.leave.*` | `leave_settings` | `GET`/`PUT /settings/leave` | **Yes** |
| `settings.company.*` | `company_settings` | `GET`/`PUT /settings/company` | **Yes** |
| `settings.notification.*` | — | none (commented out) | **No** |
| `settings.workLogs.*` | — | none (commented out) | **No** |

* `GET /settings/notification` and `GET /settings/work-log` **do not exist**
  (the entity, DTO, mapper, repository, service and controller mappings are all
  commented out with `// DISABLED:` markers). Do not build these screens — the
  backend must be re-enabled first.
* There is **no POST, DELETE, activate/deactivate or history endpoint**. Each
  category is one row, always `active = true`; there is no API to deactivate it.
* Only `@RequestMapping("/api/v1/settings")` exists — there is no other settings
  mapping in the codebase.

### 2.1 Shared response fields

Every settings response is the category's own fields plus:

| Field | Type | Notes |
|---|---|---|
| `id` | number | Row id, auto-generated. |
| `active` | boolean | Always `true` in practice. **Not accepted** in any request body. |

There are no relationships, no enum fields, and no pagination in this module.

---

## 3. Permissions and page layout

| Capability | SUPER_ADMIN | HR_ADMIN | MANAGER | EMPLOYEE |
|---|---|---|---|---|
| View any settings tab | ✅ | ✅ | ✅ | ❌ |
| Update any settings tab | ✅ | ✅ | ✅ | ❌ |

Recommended page: a single **Settings** screen with three tabs —
**Attendance**, **Leave**, **Company** — hidden from `EMPLOYEE`.

> **Product decision, not enforced by the backend:** all three admin roles have
> identical read *and* write access. If HR_ADMIN or MANAGER should be read-only,
> the frontend must enforce it; the API will happily accept their `PUT`.

Each tab: load with the `GET` on mount, render a form, save with the `PUT` and
replace the form state from the response (`data` is the saved row).

---

## 4. Endpoints

| Method | Path | Roles | Purpose |
|---|---|---|---|
| `GET` | `/settings/attendance` | SUPER_ADMIN, HR_ADMIN, MANAGER | Read attendance settings → **200**. |
| `PUT` | `/settings/attendance` | SUPER_ADMIN, HR_ADMIN, MANAGER | Update attendance settings → **200**. |
| `GET` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Read leave policy → **200**. |
| `PUT` | `/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | Update leave policy → **200**. |
| `GET` | `/settings/company` | SUPER_ADMIN, HR_ADMIN, MANAGER | Read company profile → **200**. |
| `PUT` | `/settings/company` | SUPER_ADMIN, HR_ADMIN, MANAGER | Update company profile → **200**. |

All are **full-object replacements** — there is no partial update, no PATCH and
no field-level validation of "only what changed". Send the complete object on
every save (see §7.3).

---

## 5. Attendance settings

### 5.1 Response / request shape

Both `AttendanceSettingsResponse` and `AttendanceSettingsRequest` carry exactly
the same nine fields (plus `id` and `active` on the response only):

```json
{
  "success": true,
  "message": "Attendance settings fetched successfully.",
  "data": {
    "id": 1,
    "officeStartTime": "09:30:00",
    "officeEndTime": "18:30:00",
    "gracePeriodMinutes": 45,
    "minimumWorkingMinutes": 480,
    "halfDayWorkingMinutes": 240,
    "checkoutCutoffMinutes": 180,
    "overtimeEnabled": false,
    "weekendAttendanceAllowed": false,
    "holidayAttendanceAllowed": false,
    "active": true
  },
  "timestamp": "2026-10-10T10:15:00"
}
```

Times are `HH:mm:ss` (`java.time.LocalTime`). Save payload = the same object
without `id` and `active`.

### 5.2 Fields, defaults and impact

| Field | Type | Seeded default | What it changes |
|---|---|---|---|
| `officeStartTime` | `HH:mm:ss` | `09:30:00` | Basis for `LATE` status and late-minutes. |
| `officeEndTime` | `HH:mm:ss` | `18:30:00` | Early exit, overtime, missed-checkout scheduler, and the checkout-reminder notification text. |
| `gracePeriodMinutes` | number | `45` | Minutes after `officeStartTime` still counted on time. |
| `minimumWorkingMinutes` | number | `480` | Working minutes needed to keep `PRESENT`. |
| `halfDayWorkingMinutes` | number | `240` | Working minutes needed for `HALF_DAY` instead of `ABSENT`. |
| `checkoutCutoffMinutes` | number | `180` | Minutes after `officeEndTime` when a missing checkout is auto-marked `MISSED_CHECKOUT`. |
| `overtimeEnabled` | boolean | `false` | When `false`, overtime minutes are not calculated at all. |
| `weekendAttendanceAllowed` | boolean | `false` | When `false`, Saturday/Sunday check-in is rejected. |
| `holidayAttendanceAllowed` | boolean | `false` | When `false`, check-in on a non-attendance holiday is rejected. |

Verified readers: `AttendanceServiceImpl`,
`AttendanceValidationServiceImpl` and `NotificationServiceImpl`. **No other
module reads attendance settings.**

### 5.3 Validation — important

`AttendanceSettingsRequest` has **no validation annotations at all**, and the
mapper copies every field straight onto the entity. Every column is
`NOT NULL`.

* Send **all nine fields on every `PUT`.** A partial payload stores `null` into a
  `NOT NULL` column, which fails at commit and surfaces as the generic
  **HTTP 500** envelope (`INTERNAL_SERVER_ERROR`), not a `400`.
* Guard the form locally: integer fields `>= 0`, `officeEndTime` after
  `officeStartTime`, and never submit a blank/unset field.

> **Needs backend confirmation:** the exact HTTP status for
> "attendance settings missing/partial" was derived from the code
> (unvalidated DTO → DB `NOT NULL` violation → unmapped exception → 500),
> not observed at runtime. Treat it as "must send the full object" either way.

---

## 6. Leave policy settings

### 6.1 Response / request shape

```json
{
  "success": true,
  "message": "Leave settings updated successfully.",
  "data": {
    "id": 1,
    "carryForwardAllowed": false,
    "monthlyGuideline": 2,
    "annualPaidLeave": 24,
    "sandwichLeaveMondayEnabled": false,
    "sandwichLeaveFridayEnabled": false,
    "sandwichLeaveFridayMondayEnabled": false,
    "active": true
  },
  "timestamp": "2026-10-10T10:15:00"
}
```

### 6.2 Fields, validation and impact

| Field | Type | Required | Validation | Seeded default | What it changes |
|---|---|---|---|---|---|
| `carryForwardAllowed` | boolean | yes | `@NotNull` | `false` | When `true`, the month-end unused-allowance **report is skipped**. It does **not** credit anything to the annual balance. |
| `monthlyGuideline` | number | yes | `@NotNull`, `@Min(0)` | `2` | Paid days per employee per calendar month. Drives the leave PAID/LOP split, the month-end report, **and payroll LOP** (days above the guideline become LOP). |
| `annualPaidLeave` | number | yes | `@NotNull`, `@Min(0)` | `24` | Fallback annual allocation, used only for a **paid** leave type whose own `allocatedDays <= 0`. |
| `sandwichLeaveMondayEnabled` | boolean | no | `null` → stored as `false` | `false` | Monday leave also charges the preceding Sat+Sun (3 days). |
| `sandwichLeaveFridayEnabled` | boolean | no | `null` → stored as `false` | `false` | Friday leave also charges the following Sat+Sun (3 days). |
| `sandwichLeaveFridayMondayEnabled` | boolean | no | `null` → stored as `false` | `false` | Friday→Monday leave charges the intervening weekend once (4 days); takes precedence over the two rules above. |

Verified readers: `LeavePaidLopServiceImpl`, `LeaveExpiryServiceImpl`,
`LeaveValidationServiceImpl`, `LeaveAllocationServiceImpl` and
`PayrollServiceImpl` (`monthlyGuideline` only).

**UI guidance:** `monthlyGuideline` is the most consequential field in this
module — changing it retroactively affects how payroll classifies approved
leave as LOP. Warn the user before saving it and consider requiring a
confirmation. The sandwich toggles only affect **future** leave submissions;
pending requests are not re-evaluated.

Fields such as `halfDayLeaveAllowed`, `minimumAdvanceNoticeDays`,
`maximumConsecutiveLeaveDays`, `managerApprovalRequired`, `hrApprovalRequired`,
`allowLeaveOnHoliday`, `allowLeaveOnWeekend`, `autoApproveLeave`,
`allowNegativeLeaveBalance` and `allowBackdatedLeaveApplication` are commented
out in the entity and DTO — **do not render inputs for them**.

---

## 7. Company settings

### 7.1 Response shape

```json
{
  "success": true,
  "message": "Company settings fetched successfully.",
  "data": {
    "id": 1,
    "companyName": "HourlyRecruit",
    "companyCode": "MYHR",
    "email": "admin@example.com",
    "phoneNumber": "9876543210",
    "website": null,
    "addressLine1": null,
    "addressLine2": null,
    "city": null,
    "state": null,
    "country": null,
    "postalCode": null,
    "timeZone": "Asia/Kolkata",
    "currency": "INR",
    "workingDaysPerWeek": 5,
    "logoUrl": null,
    "active": true
  },
  "timestamp": "2026-10-10T10:15:00"
}
```

### 7.2 Fields, validation and impact

| Field | Type | Required | Validation | Seeded default |
|---|---|---|---|---|
| `companyName` | string (≤150) | yes | `@NotBlank` | `"HourlyRecruit"` |
| `companyCode` | string (≤30) | yes | `@NotBlank`; **UNIQUE** in the DB | `"MYHR"` |
| `email` | string (≤150) | yes | `@NotBlank`, `@Email` | the configured `app.super-admin.email` |
| `phoneNumber` | string (≤20) | yes | `@NotBlank`, `@Pattern(^[6-9]\d{9}$)` — 10-digit Indian mobile | `"9876543210"` |
| `website` | string (≤150) | no | `@URL` | `null` |
| `addressLine1` | string (≤255) | yes | `@NotBlank` | `null` |
| `addressLine2` | string (≤255) | yes | `@NotBlank` | `null` |
| `city` | string (≤100) | yes | `@NotBlank` | `null` |
| `state` | string (≤100) | yes | `@NotBlank` | `null` |
| `country` | string (≤100) | yes | `@NotBlank` | `null` |
| `postalCode` | string (≤20) | yes | `@NotBlank` | `null` |
| `timeZone` | string (≤50) | yes | `@NotBlank` | `"Asia/Kolkata"` |
| `currency` | string (≤10) | yes | `@NotBlank` | `"INR"` |
| `workingDaysPerWeek` | number | **yes in practice** | `@Min(1)`, `@Max(7)`, **no `@NotNull`** | `5` |
| `logoUrl` | string (≤500) | **not writable** | — | `null` |

Notes that affect the UI:

* **`logoUrl` is response-only.** It is absent from `CompanySettingsRequest`,
  so it cannot be changed through the API, and nothing in the backend reads it
  (the payslip logo is loaded from a classpath resource, not from this field).
  Render it read-only or omit it.
* **`workingDaysPerWeek` looks optional but is not.** The DTO has no `@NotNull`,
  yet the column is `NOT NULL` — omitting it stores `null` and produces a
  **500**. Always send it.
* **`companyCode` uniqueness is not pre-checked.** Sending a code that already
  exists on another row fails at commit and surfaces as a **500**, not a `400`.
  Validate uniqueness in the UI and prefer keeping the code unchanged.
* **Business readers:** `PayslipGenerator` and the payslip sample generator read
  `companyName`, `addressLine1` and `addressLine2` for the PDF header; if the
  active company row is missing, payslip generation fails with
  `PayslipGenerationException("Active company settings not found")` — the text
  fallback only covers the logo image. Every other
  company field (`companyCode`, `email`, `phoneNumber`, `website`, `city`,
  `state`, `country`, `postalCode`, `timeZone`, `currency`, `workingDaysPerWeek`,
  `logoUrl`) currently has **no business reader** — it is stored and returned
  for display only.

### 7.3 Fresh-install gotcha (read this before building the form)

A brand-new installation is seeded with `addressLine1`, `addressLine2`, `city`,
`state`, `country` and `postalCode` **all `null`**, but the `PUT` requires every
one of them. So on a fresh database:

* `GET /settings/company` succeeds and returns those fields as `null`;
* the first `PUT /settings/company` fails with `400 VALIDATION_FAILED` unless the
  user fills all six in.

The form must therefore treat those six as empty-but-required inputs, not as
"already configured".

---

## 8. Defaults, seeding and missing rows

* Seeding happens once at startup (`DataInitializer`, `@Order(1)`): if a
  category table already has any row, seeding is skipped.
  * Company: `HourlyRecruit` / `MYHR` / super-admin email / `9876543210` /
    `Asia/Kolkata` / `INR` / `5`.
  * Attendance: `09:30:00`, `18:30:00`, `45`, `480`, `240`, `180`, all three
    booleans `false`.
  * Leave: `carryForwardAllowed=false`, `monthlyGuideline=2`,
    `annualPaidLeave=24`, sandwich flags `false`.
* Reads use `findFirstByActiveTrue()`. If no active row exists the API answers
  **`404`**:
  * attendance → `"Attendance settings not found."` (`RESOURCE_NOT_FOUND`)
  * leave → `"Leave settings not found."` (`RESOURCE_NOT_FOUND`)
  * company → `"Company settings not found."` (**`COMPANY_NOT_FOUND`**)
  Note the company category uses a **different error code**; branch on the code,
  not on a shared string.
* There is **no optimistic locking** (no `@Version` anywhere) — concurrent saves
  are last-write-wins. Consider re-`GET`ting before saving, and warn when two
  admins edit the same tab.

---

## 9. Errors and frontend states

| HTTP | `errorCode` | Raised when |
|---|---|---|
| 200 | — | Successful read/update. |
| 400 | `VALIDATION_FAILED` | Bean-validation failure on `PUT /company` or `PUT /leave` (malformed email, bad phone pattern, blank required field, negative numbers). |
| 401 | `UNAUTHORIZED` | Missing/invalid token. |
| 403 | `ACCESS_DENIED` | `EMPLOYEE` (or any role outside the three) calling any settings endpoint. |
| 404 | `RESOURCE_NOT_FOUND` | Attendance/leave settings row missing. |
| 404 | `COMPANY_NOT_FOUND` | Company settings row missing. |
| 500 | `INTERNAL_SERVER_ERROR` | Partial attendance payload, `workingDaysPerWeek` omitted, duplicate `companyCode`, or any unmapped error. Message is prefixed `"An unexpected error occurred."`. |

Behaviour to design around:

* **Only the first field error is returned.** The global handler maps the first
  `FieldError` to `message`, so a form with several problems shows one message at
  a time. Validate all fields client-side to avoid a fix-one-at-a-time loop.
* **Load:** show a form skeleton; if the response `data` is missing fields, render
  them as empty rather than inventing defaults.
* **Save:** disable the button while the request is in flight (a double submit
  writes twice — there is no deduplication).
* **Success:** replace the form values with the returned `data` (the backend is
  the source of truth) and show "…settings updated successfully." from `message`.
* **Failure:** keep the user's input, show `message`, and highlight the offending
  field when it can be inferred (e.g. `phoneNumber` from the pattern message).
* **404 on load:** show a configuration/backend error state, not an empty form —
  the row is expected to exist.

---

## 10. Field-to-control mapping

| Tab | Field | Control |
|---|---|---|
| Attendance | `officeStartTime`, `officeEndTime` | Time picker, step 60s, output `HH:mm:ss`. |
| Attendance | `gracePeriodMinutes`, `minimumWorkingMinutes`, `halfDayWorkingMinutes`, `checkoutCutoffMinutes` | Number input, min 0, required. |
| Attendance | `overtimeEnabled`, `weekendAttendanceAllowed`, `holidayAttendanceAllowed` | Toggle, required (always sent). |
| Leave | `carryForwardAllowed` | Toggle, required. |
| Leave | `monthlyGuideline` | Number, min 0, required, with an LOP/payroll warning. |
| Leave | `annualPaidLeave` | Number, min 0, required. |
| Leave | three sandwich toggles | Toggle each; always send all three (omitting one resets it to `false`). |
| Company | `companyName`, `companyCode`, `email`, `phoneNumber`, `addressLine1`, `addressLine2`, `city`, `state`, `country`, `postalCode`, `timeZone`, `currency` | Text input, required (blank → 400). |
| Company | `website` | URL input, optional (must be a valid URL if filled). |
| Company | `workingDaysPerWeek` | Number 1–7, required. |
| Company | `logoUrl` | Read-only display (not writable). |

`timeZone` and `currency` are free-text strings validated only as non-blank —
there is no picker data source and no allowed-value list in the backend. Offer a
sensible client-side list but still allow free text.

**Length limits:** `email` (≤150), `phoneNumber` (≤20), `timeZone` (≤50) and
`currency` (≤10) come from the **database column**, not from request validation
— an over-long value passes bean validation and then fails at commit with a
**500**. Enforce those limits client-side. All other length limits in §7.2 are
enforced by `@Size` on the request and return a clean `400`.

---

## 11. Frontend testing checklist

**Access**
- [ ] Settings nav/tabs hidden for EMPLOYEE; direct navigation to the page is blocked.
- [ ] EMPLOYEE calling any settings `GET`/`PUT` shows the 403 state, not a blank form.
- [ ] MANAGER and HR_ADMIN can load and save all three tabs.

**Attendance**
- [ ] Load → edit → save round-trips all nine fields and the saved values are re-rendered.
- [ ] Submitting with any field blanked is blocked client-side (backend would 500).
- [ ] `officeEndTime` before `officeStartTime` is blocked client-side.
- [ ] Time values are sent as `HH:mm:ss`.

**Leave**
- [ ] All six fields round-trip; toggles save correctly.
- [ ] Omitting a sandwich toggle resets it to `false` and the UI reflects that.
- [ ] Negative `monthlyGuideline` / `annualPaidLeave` is blocked client-side (400).
- [ ] Changing `monthlyGuideline` shows the LOP/payroll warning before saving.

**Company**
- [ ] On a fresh database, the six address fields load as empty and the first save requires them.
- [ ] Invalid email and a non-Indian 10-digit phone are rejected with the backend message.
- [ ] `website` accepts empty and a valid URL, rejects an invalid URL.
- [ ] `workingDaysPerWeek` accepts 1–7 only and is always included in the payload.
- [ ] Changing `companyCode` to an existing value surfaces a clear error (backend returns 500).
- [ ] `logoUrl` renders read-only and is never included in the `PUT` body.

**States**
- [ ] Loading skeleton while a tab loads; save button disabled during the request.
- [ ] 404 on load shows a backend-config error state.
- [ ] 500/network failure shows a retryable toast and preserves the edited form.
- [ ] Two rapid saves do not fire two requests.
