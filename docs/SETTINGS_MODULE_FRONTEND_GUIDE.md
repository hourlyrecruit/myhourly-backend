# Settings Module – Frontend Implementation Guide

**Date:** 2026-09-09
**Backend source analyzed:** `src/main/java/com/my_hourly/settings/**` plus every module that references it (`attendance`, `leave`, `payroll`, `notification`, `common.initializer.DataInitializer`).
**Basis:** actual backend code only. Where something could not be determined from code, it is marked **"Not found in backend code"**.

---

## 1. Settings Module Overview (What the backend actually contains)

The `com.my_hourly.settings` package is a global application-configuration module. It stores
single-row configuration records (one active row per category) that other modules read when
executing business logic.

### Packages and components

| Package / class | Type | Purpose | Used by other modules? |
|---|---|---|---|
| `settings.BaseSettings` | Entity base class (`@MappedSuperclass`) | Adds the `active` flag to every settings entity | Internal (extended by the 3 live entities) |
| `settings.attendance.*` | Submodule (7 classes) | Office hours, grace period, working-minute thresholds, weekend/holiday check-in rules | **Yes** — `attendance`, `notification` modules |
| `settings.leave.*` | Submodule (7 classes) | Carry-forward policy, monthly leave guideline, annual paid leave | **Yes** — `leave`, `payroll` modules |
| `settings.company.*` | Submodule (7 classes) | Company profile (name, code, contact info, timezone, currency, working days) | **No** — REST API only |
| `settings.notification.*` | Submodule (7 classes) | Notification channel/type toggles | **No — entire submodule is commented out (DISABLED)** in the backend |
| `settings.workLogs.*` | Submodule (7 classes) | Work-log submission rules | **No — entire submodule is commented out (DISABLED)** in the backend |
| `settings.controller.SettingController` | REST controller | Exposes `GET`/`PUT` for attendance, leave, company; notification/work-log endpoints are commented out | Entry point for the frontend |

### Supporting components

| Component | Location | Purpose |
|---|---|---|
| `DataInitializer` (`common.initializer`) | `@Order(1)` `ApplicationRunner` | Seeds the four required settings rows once at application startup (notification/work-log seeding is commented out) |
| `ApiResponse<T>` (`common.payload.response`) | Wrapper for all success responses: `{ success, message, data, timestamp }` |
| `ApiError` (`common.payload.response`) | Wrapper for all error responses: `{ success, message, errorCode, path, timestamp }` |
| `GlobalExceptionHandler` (`common.exception`) | Converts exceptions/Bean-Validation failures into `ApiError` responses |
| `ErrorCode` (`common.enums`) | Enum of machine-readable error codes (see §7) |

**Not found in backend code:** settings-specific enums, dedicated validator classes, settings-specific configuration classes, or utility classes. Validation is done with Jakarta Bean Validation annotations on the request DTOs plus inline checks in the service implementations (currently none active for leave — see §7).

---

## 2. Usage Across the Project (verified, not assumed)

### Classification legend
**Used** = actively read by running business logic · **API-only** = reachable/visible only through the REST API · **Not used** = no reader anywhere · **Used only during initialization** = written only by the startup seeder.

### AttendanceSettings fields

| Field | Classification | Read by |
|---|---|---|
| `officeStartTime` | **Used** | `AttendanceServiceImpl` (late status + late-minutes calculation) |
| `officeEndTime` | **Used** | `AttendanceServiceImpl` (early-exit + overtime + missed-checkout scheduler), `NotificationServiceImpl` (checkout reminder text) |
| `gracePeriodMinutes` | **Used** | `AttendanceServiceImpl` (late status + late minutes) |
| `minimumWorkingMinutes` | **Used** | `AttendanceServiceImpl` (final status: PRESENT vs HALF_DAY/ABSENT) |
| `halfDayWorkingMinutes` | **Used** | `AttendanceServiceImpl` (final status: HALF_DAY vs ABSENT) |
| `checkoutCutoffMinutes` | **Used** | `AttendanceServiceImpl.markMissedCheckouts()` (scheduler) |
| `overtimeEnabled` | **Used** | `AttendanceServiceImpl` (overtime minutes) |
| `weekendAttendanceAllowed` | **Used** | `AttendanceValidationServiceImpl` (blocks/allows weekend check-in) |
| `holidayAttendanceAllowed` | **Used** | `AttendanceValidationServiceImpl` (blocks/allows holiday check-in) |

### LeaveSettings fields

| Field | Classification | Read by |
|---|---|---|
| `carryForwardAllowed` | **Used** | `LeaveExpiryServiceImpl` (if `true`, no monthly expiry happens) |
| `monthlyGuideline` | **Used** | `LeaveExpiryServiceImpl` (unused-leave expiry math), `PayrollServiceImpl` (allowed monthly paid leave → LOP) |
| `annualPaidLeave` | **Used** | `LeaveAllocationServiceImpl` (fallback allocation for paid leave types) |

### CompanySettings fields

All fields (`companyName`, `companyCode`, `email`, `phoneNumber`, `website`, `addressLine1`, `addressLine2`, `city`, `state`, `country`, `postalCode`, `timeZone`, `currency`, `workingDaysPerWeek`, `logoUrl`): **API-only** — kept per product decision, no backend module reads them. The row itself **is required** (the service throws if absent), and the payroll PDF flow does *not* consume it (verified: payslip generation does not read CompanySettings).

### Not used (entire submodules, commented out in backend)

* `settings.notification.*` — all 7 classes commented out; endpoints removed. No backend module reads any flag (notification behavior is hard-coded in the notification module).
* `settings.workLogs.*` — all 7 classes commented out; endpoints were already commented out before this cleanup.

Do **not** build UI for these two areas. If product wants them later, the backend must be re-enabled first (all disabled spots are marked `// DISABLED` in code).

---

## 3. Entities and Fields

All settings entities extend `BaseEntity` and `BaseSettings`:

**Common fields on every settings response** (from `BaseEntity` + `BaseSettings`):

| Field | Type | Notes |
|---|---|---|
| `id` | number (Long) | Primary key, auto-generated (`GenerationType.IDENTITY`) |
| `active` | boolean | Always `true` in practice; there is no API to deactivate |
| `createdAt` | string (`yyyy-MM-dd'T'HH:mm:ss`) | Audit field, set automatically |
| `updatedAt` | string (`yyyy-MM-dd'T'HH:mm:ss`) | Audit field, set automatically |

**Relationships:** none. Settings entities have no foreign keys and no relations to other entities. Each category is a standalone single-row table (`attendance_settings`, `leave_settings`, `company_settings`).

**Unique constraints:** `company_settings.company_code` is `unique = true` in the database. Nothing else is unique. Note: the service does **not** pre-check uniqueness — a duplicate `companyCode` submitted via `PUT /company` surfaces as a database error (HTTP 500, see §13).

**Enum fields:** none in settings. (The `ErrorCode` enum appears only in error payloads.)

---

### 3.1 AttendanceSettings (table `attendance_settings`)

| Entity | Field | Data Type | Required (DB) | Default Value | Used By | Purpose |
|---|---|---|---|---|---|---|
| AttendanceSettings | `officeStartTime` | LocalTime (`HH:mm:ss`) | NOT NULL | `09:30:00` (seeder) | AttendanceServiceImpl | Office start time; basis for LATE calculation |
| AttendanceSettings | `officeEndTime` | LocalTime (`HH:mm:ss`) | NOT NULL | `18:30:00` (seeder) | AttendanceServiceImpl, NotificationServiceImpl | Office end time; basis for early-exit, overtime, missed-checkout, checkout reminder |
| AttendanceSettings | `gracePeriodMinutes` | Integer | NOT NULL | `45` (seeder) | AttendanceServiceImpl | Minutes after office start still counted as on-time |
| AttendanceSettings | `minimumWorkingMinutes` | Integer | NOT NULL | `480` (seeder) | AttendanceServiceImpl | Working minutes needed to keep PRESENT status |
| AttendanceSettings | `halfDayWorkingMinutes` | Integer | NOT NULL | `240` (seeder) | AttendanceServiceImpl | Working minutes needed for HALF_DAY instead of ABSENT |
| AttendanceSettings | `checkoutCutoffMinutes` | Integer | NOT NULL | `180` (seeder) | AttendanceServiceImpl (scheduler) | Minutes after office end when a missing checkout is auto-marked MISSED_CHECKOUT |
| AttendanceSettings | `overtimeEnabled` | Boolean | NOT NULL | `false` (seeder) | AttendanceServiceImpl | Whether overtime minutes are calculated at all |
| AttendanceSettings | `weekendAttendanceAllowed` | Boolean | NOT NULL | `false` (seeder) | AttendanceValidationServiceImpl | If `false`, Saturday/Sunday check-in is rejected |
| AttendanceSettings | `holidayAttendanceAllowed` | Boolean | NOT NULL | `false` (seeder) | AttendanceValidationServiceImpl | If `false`, check-in on a non-attendance holiday is rejected |
| AttendanceSettings | `active` | Boolean | NOT NULL | `true` | Repositories (`findFirstByActiveTrue`) | Selects the active row |

Fields that existed earlier but are **commented out / unused** (do not render inputs for them): `attendanceRegularizationEnabled`, `multipleBreaksAllowed`, `maximumBreakMinutes`, `maximumBreaksPerDay`, `lateMarkEnabled`, `earlyExitEnabled`, `autoCheckoutEnabled`.

### 3.2 LeaveSettings (table `leave_settings`)

| Entity | Field | Data Type | Required (DB) | Default Value | Used By | Purpose |
|---|---|---|---|---|---|---|
| LeaveSettings | `carryForwardAllowed` | Boolean | NOT NULL | `false` (seeder) | LeaveExpiryServiceImpl | `true` = unused monthly leave carries to annual balance; `false` = expires month-end |
| LeaveSettings | `monthlyGuideline` | Integer | NOT NULL | `2` (seeder) | LeaveExpiryServiceImpl, PayrollServiceImpl | Recommended paid-leave days per month; drives expiry and payroll LOP |
| LeaveSettings | `annualPaidLeave` | Integer | NOT NULL | `24` (seeder) | LeaveAllocationServiceImpl | Fallback annual paid-leave days when a leave type has none |
| LeaveSettings | `active` | Boolean | NOT NULL | `true` | Repositories | Selects the active row |

Fields commented out / unused: `halfDayLeaveAllowed`, `minimumAdvanceNoticeDays`, `maximumAdvanceNoticeDays`, `maximumConsecutiveLeaveDays`, `managerApprovalRequired`, `hrApprovalRequired`, `allowLeaveOnHoliday`, `allowLeaveOnWeekend`, `autoApproveLeave`, `allowNegativeLeaveBalance`, `allowBackdatedLeaveApplication`.

### 3.3 CompanySettings (table `company_settings`)

| Entity | Field | Data Type | Required (DB) | Default Value | Used By | Purpose |
|---|---|---|---|---|---|---|
| CompanySettings | `companyName` | String (≤150) | NOT NULL | `"MyHourly"` (seeder) | **Unused** (API only) | Company display name |
| CompanySettings | `companyCode` | String (≤30) | NOT NULL, **UNIQUE** | `"MHR"` (seeder) | **Unused** (API only) | Short company code |
| CompanySettings | `email` | String (≤150) | NOT NULL | value of `app.super-admin.email` property (seeder) | **Unused** (API only) | Company contact email |
| CompanySettings | `phoneNumber` | String (≤20) | nullable | `"9876543210"` (seeder) | **Unused** (API only) | Company phone |
| CompanySettings | `website` | String (≤150) | nullable | — | **Unused** (API only) | Company website |
| CompanySettings | `addressLine1` | String (≤255) | nullable | — | **Unused** (API only) | Address line 1 |
| CompanySettings | `addressLine2` | String (≤255) | nullable | — | **Unused** (API only) | Address line 2 |
| CompanySettings | `city` | String (≤100) | nullable | — | **Unused** (API only) | City |
| CompanySettings | `state` | String (≤100) | nullable | — | **Unused** (API only) | State |
| CompanySettings | `country` | String (≤100) | nullable | — | **Unused** (API only) | Country |
| CompanySettings | `postalCode` | String (≤20) | nullable | — | **Unused** (API only) | Postal code |
| CompanySettings | `timeZone` | String (≤50) | NOT NULL | `"Asia/Kolkata"` (seeder) | **Unused** (API only) | Company timezone (informational; schedulers use server time) |
| CompanySettings | `currency` | String (≤10) | NOT NULL | `"INR"` (seeder) | **Unused** (API only) | Company currency |
| CompanySettings | `workingDaysPerWeek` | Integer | NOT NULL | `5` (seeder) | **Unused** (API only) | Number of working days per week (1–7) |
| CompanySettings | `logoUrl` | String (≤500) | nullable | — | **Unused** (API only) | Logo URL — **response-only; cannot be updated through the API** (not present in the request DTO) |
| CompanySettings | `active` | Boolean | NOT NULL | `true` | Repositories | Selects the active row |

**Validation rules on CompanySettings request** (from Bean Validation annotations on `CompanySettingsRequest`):

| Field | Rules |
|---|---|
| `companyName` | `@NotBlank` ("Company name is required."), `@Size(max=150)` |
| `companyCode` | `@NotBlank` ("Company code is required."), `@Size(max=30)` |
| `email` | `@NotBlank` ("Email is required."), `@Email` ("Invalid email format.") |
| `phoneNumber` | `@Pattern(regexp="^[0-9]{10,15}$")` ("Phone number must contain 10 to 15 digits.") — optional (null passes) |
| `website` | `@Size(max=150)` |
| `addressLine1` / `addressLine2` | `@Size(max=255)` |
| `city` / `state` / `country` | `@Size(max=100)` |
| `postalCode` | `@Size(max=20)` |
| `timeZone` | `@NotBlank` ("Time zone is required.") |
| `currency` | `@NotBlank` ("Currency is required.") |
| `workingDaysPerWeek` | `@Min(1)`, `@Max(7)` |

---

## 4. APIs

Base path: **`/api/v1/settings`** — controller: `SettingController`.
All success responses are wrapped in `ApiResponse<T>`; all errors are `ApiError` (see §7).

| Method | Endpoint | Purpose | Request body | Response `data` | Auth (roles) |
|---|---|---|---|---|---|
| GET | `/api/v1/settings/attendance` | Load attendance settings | — | `AttendanceSettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/attendance` | Update attendance settings | `AttendanceSettingsRequest` | `AttendanceSettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |
| GET | `/api/v1/settings/leave` | Load leave settings | — | `LeaveSettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/leave` | Update leave settings | `LeaveSettingsRequest` | `LeaveSettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |
| GET | `/api/v1/settings/company` | Load company settings | — | `CompanySettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/company` | Update company settings | `CompanySettingsRequest` | `CompanySettingsResponse` | SUPER_ADMIN, HR_ADMIN, MANAGER |

**Not available (do not implement):** `GET/PUT /api/v1/settings/notification`, `GET/PUT /api/v1/settings/work-log` (commented out in backend → 404). There are **no** create, delete, list, search, pagination, or sorting endpoints for settings — the module manages exactly one active row per category.

### 4.1 Attendance settings objects

`AttendanceSettingsRequest` (PUT body — all fields optional per annotations, but see the ⚠️ note below):

| Field | Type | Validation |
|---|---|---|
| `officeStartTime` | string `HH:mm:ss` | none (backend) |
| `officeEndTime` | string `HH:mm:ss` | none (backend) |
| `gracePeriodMinutes` | number | none (backend) |
| `minimumWorkingMinutes` | number | none (backend) |
| `halfDayWorkingMinutes` | number | none (backend) |
| `checkoutCutoffMinutes` | number | none (backend) |
| `overtimeEnabled` | boolean | none (backend) |
| `weekendAttendanceAllowed` | boolean | none (backend) |
| `holidayAttendanceAllowed` | boolean | none (backend) |

⚠️ **Important:** the mapper (`AttendanceSettingsMapper.updateEntity`) copies **every** field from the request into the entity, including `null`s. Sending a partial body will null out the omitted fields and fail on the database (`NOT NULL` columns → HTTP 500). **The frontend must always GET first, modify, and PUT the complete object.**

`AttendanceSettingsResponse` (both GET and PUT return this):

| Field | Type |
|---|---|
| `id` | number |
| `officeStartTime` | string `HH:mm:ss` |
| `officeEndTime` | string `HH:mm:ss` |
| `gracePeriodMinutes` | number |
| `minimumWorkingMinutes` | number |
| `halfDayWorkingMinutes` | number |
| `checkoutCutoffMinutes` | number |
| `overtimeEnabled` | boolean |
| `weekendAttendanceAllowed` | boolean |
| `holidayAttendanceAllowed` | boolean |
| `active` | boolean |

### 4.2 Leave settings objects

`LeaveSettingsRequest` (PUT body — all required):

| Field | Type | Validation |
|---|---|---|
| `carryForwardAllowed` | boolean | `@NotNull` |
| `monthlyGuideline` | number | `@NotNull`, `@Min(0)` |
| `annualPaidLeave` | number | `@NotNull`, `@Min(0)` |

`LeaveSettingsResponse`:

| Field | Type |
|---|---|
| `id` | number |
| `carryForwardAllowed` | boolean |
| `monthlyGuideline` | number |
| `annualPaidLeave` | number |
| `active` | boolean |

### 4.3 Company settings objects

`CompanySettingsRequest` (PUT body — validation table in §3.3; `logoUrl` is **not** updatable):

| Field | Type |
|---|---|
| `companyName` | string |
| `companyCode` | string |
| `email` | string |
| `phoneNumber` | string \| null |
| `website` | string \| null |
| `addressLine1` | string \| null |
| `addressLine2` | string \| null |
| `city` | string \| null |
| `state` | string \| null |
| `country` | string \| null |
| `postalCode` | string \| null |
| `timeZone` | string |
| `currency` | string |
| `workingDaysPerWeek` | number |

`CompanySettingsResponse`: all of the above **plus** `id`, `logoUrl` (string \| null), `active` (boolean).

---

## 5. Frontend Screen Requirements

Suggested structure: one **Settings** page with three sections/tabs — **Attendance**, **Leave**, **Company**. Each section is a single edit form bound to one settings row. There is no list/table, no search, no add/delete — the backend supports none of those.

### 5.1 Attendance Settings page

* **Page name suggestion:** Settings → Attendance
* **UI components:** time pickers (`officeStartTime`, `officeEndTime`), number inputs (grace, minimum-working, half-day, checkout-cutoff), toggle/checkbox switches (`overtimeEnabled`, `weekendAttendanceAllowed`, `holidayAttendanceAllowed`), Save button.
* **Fields:** the 9 request fields from §4.1. Do **not** render inputs for the disabled fields listed in §3.1.
* **Validation (frontend):** the backend does *not* validate these fields, so the frontend must: require all 9 fields; validate time format `HH:mm:ss`; sensible ranges (e.g. grace/checkout-cutoff ≥ 0; half-day ≤ minimum-working is a natural expectation — **not enforced by backend**).
* **Save behavior:** PUT the **complete** object (GET → edit → PUT). Never send a partial body.
* **Loading state:** skeleton/spinner while GET runs; disable Save during PUT.
* **Empty/error state:** if GET returns 404 (no settings row — only possible if seeding failed), show "Settings not initialized. Contact your administrator." and hide the form.
* **Success:** show the `message` from the response ("Attendance settings updated successfully.") and refresh the form from the returned `data`.

### 5.2 Leave Settings page

* **Page name suggestion:** Settings → Leave
* **UI components:** toggle (`carryForwardAllowed`), number inputs (`monthlyGuideline`, `annualPaidLeave`), Save button.
* **Validation (frontend + backend):** all three fields required; `monthlyGuideline ≥ 0`; `annualPaidLeave ≥ 0`. Backend returns 400 `VALIDATION_FAILED` if violated.
* **Helper text to show users (from backend behavior):** "When carry-forward is ON, unused monthly leave rolls into the annual balance. When OFF, unused guideline leave expires at month-end using the monthly guideline."
* **Same save/empty/loading rules as §5.1.**

### 5.3 Company Settings page

* **Page name suggestion:** Settings → Company
* **UI components:** text inputs, email input, phone input, number input (working days 1–7), Save button. Optional fields should be clearable.
* **Fields:** the 14 request fields from §4.3. Show `logoUrl` as **read-only** if you display it at all (cannot be changed through the API).
* **Validation (frontend):** mirror §3.3 rules exactly (required: companyName, companyCode, email, timeZone, currency; phone `^[0-9]{10,15}$` if provided; sizes; working days 1–7). Backend enforces the same via Bean Validation.
* **Same save/empty/loading rules as §5.1.**

### Common component behavior

| Aspect | Backend-supported behavior |
|---|---|
| Reset/Cancel | Re-fetch GET and repopulate (frontend-only; no backend reset API) |
| Delete | **Not supported** — do not build |
| Add/Create | **Not supported** — PUT updates the existing row only |
| Multi-row management | **Not supported** — exactly one active row per category |
| Pagination/filter/sort | **Not applicable** |
| Per-user settings | **Not found in backend code** — settings are global (single row) |

---

## 6. Data Initializers and Default Settings

`common.initializer.DataInitializer` runs **once at application startup** (`ApplicationRunner`, `@Order(1)`), before `SeedRunner` (`@Order(2)`, users only). Every seed is **idempotent**: it checks `repository.count() > 0` and skips if any row exists. There is no API to re-run seeding.

| Setting | Default Value | Purpose | Used By | Required for Application |
|---|---|---|---|---|
| Attendance settings row | officeStartTime `09:30:00`, officeEndTime `18:30:00`, gracePeriodMinutes `45`, minimumWorkingMinutes `480`, halfDayWorkingMinutes `240`, checkoutCutoffMinutes `180`, overtimeEnabled `false`, weekendAttendanceAllowed `false`, holidayAttendanceAllowed `false`, active `true` | Baseline attendance rules | Check-in validation, status calculation, missed-checkout scheduler, checkout reminders | **Yes** — check-in/out fails with 404-based errors without it |
| Leave settings row | carryForwardAllowed `false`, monthlyGuideline `2`, annualPaidLeave `24`, active `true` | Baseline leave policy | Leave expiry scheduler, leave allocation fallback, payroll LOP | **Yes** — leave expiry/allocation and payroll abort or fall back without it |
| Company settings row | companyName `"MyHourly"`, companyCode `"MHR"`, email = `${app.super-admin.email}`, phoneNumber `"9876543210"`, timeZone `"Asia/Kolkata"`, currency `"INR"`, workingDaysPerWeek `5`, active `true` | Company profile | None (API-only display) | **Yes** — GET/PUT company returns 404 `COMPANY_NOT_FOUND` without it |
| Work-log settings row | (seeding **commented out**) | — | — | No — submodule disabled |
| Notification settings row | (seeding **commented out**) | — | — | No — submodule disabled |

Additional notes:

* The attendance seed does **not** set values for the fields that are now disabled (they never had defaults; `maximumBreaksPerDay` was never seeded at all historically).
* `initializeHoliday()` in the same initializer seeds one holiday row ("Independence Day") — unrelated to settings but runs in the same startup step.
* Which settings appear "unused"? The **work-log** and **notification** rows/seeders were disabled; all other seeded settings are consumed by running logic except the Company profile fields (API-only, kept by product decision).

---

## 7. Business Rules (in plain language)

1. **One active row per category.** Services load settings via `findFirstByActiveTrue()`. There is no versioning, no history, no multi-tenant/per-organization rows.
2. **Update-only.** PUT modifies the existing row. If the row is missing (seeding never ran), the service throws:
   * Attendance → 404, message `"Attendance settings not found."`, code `RESOURCE_NOT_FOUND`
   * Leave → 404, `"Leave settings not found."`, `RESOURCE_NOT_FOUND`
   * Company → 404, `"Company settings not found."`, `COMPANY_NOT_FOUND`
3. **How attendance settings affect employees (explain these in UI hints if desired):**
   * Check-in after `officeStartTime + gracePeriodMinutes` → marked LATE.
   * At checkout: working minutes ≥ `minimumWorkingMinutes` keeps PRESENT; ≥ `halfDayWorkingMinutes` → HALF_DAY; otherwise ABSENT.
   * If `weekendAttendanceAllowed` is `false`, checking in on Saturday/Sunday is rejected.
   * If `holidayAttendanceAllowed` is `false`, checking in on a holiday that disallows attendance is rejected.
   * If `overtimeEnabled` is `true`, minutes worked after `officeEndTime` count as overtime; if `false`, always 0.
   * After `officeEndTime + checkoutCutoffMinutes`, unchecked-out employees are auto-marked MISSED_CHECKOUT by a scheduler.
   * Checkout reminder notifications quote `officeEndTime` in their text.
4. **How leave settings affect employees:**
   * `carryForwardAllowed = true` → no monthly expiry ever runs.
   * `carryForwardAllowed = false` → each month, unused days beyond the `monthlyGuideline` expire.
   * `annualPaidLeave` is only a **fallback** — a leave type with its own allocated days overrides it.
   * Payroll treats `monthlyGuideline` as the allowed paid-leave days per month; extra leave days become loss-of-pay.
5. **Deactivation is not exposed.** `active` cannot be changed through any API; all rows are active.
6. **`logoUrl` cannot be changed** through the API (request DTO has no such field). It is response-only.
7. **Time format.** Time values are `HH:mm:ss`. A malformed time in a PUT body returns 400 with message `"Invalid time format. Expected format: HH:mm:ss (example: 18:30:00)"` and code `INVALID_REQUEST` (from `GlobalExceptionHandler`).
8. **No cross-field validation is active.** The leave service's internal validator (`validateLeaveSettings`) is fully commented out in the current code. The backend accepts, e.g., `monthlyGuideline` larger than `annualPaidLeave`. The frontend should decide and enforce sensible pairings, or this must be clarified with the backend team (§14).
9. **Settings changes take effect immediately** for the next calculation (no restart, no caching layer found in code).

### Error response format (all settings endpoints)

Success (`ApiResponse<T>`):

```json
{
  "success": true,
  "message": "Attendance settings updated successfully.",
  "data": { "...": "..." },
  "timestamp": "2026-09-09T14:55:00.123"
}
```

Error (`ApiError`):

```json
{
  "success": false,
  "message": "Time zone is required.",
  "errorCode": "VALIDATION_FAILED",
  "path": "/api/v1/settings/company",
  "timestamp": "2026-09-09T14:55:00.123"
}
```

HTTP status codes to handle:

| Status | When | Payload |
|---|---|---|
| 200 | Successful GET/PUT | `ApiResponse<T>` (`success: true`) |
| 400 | Bean Validation failure (`@NotNull`, `@NotBlank`, `@Email`, `@Min`…), service `BadRequestException`/`ValidationException`, malformed JSON/time | `ApiError`, code usually `VALIDATION_FAILED` or `INVALID_REQUEST`. For Bean Validation, `message` is the first field error (e.g. `"Company code is required."`) — only **one** message is returned, not a per-field map |
| 401 | Missing/invalid authentication | Security chain response (project-standard; settings endpoints are not reachable unauthenticated) |
| 403 | Authenticated but role not in SUPER_ADMIN/HR_ADMIN/MANAGER | `ApiError`, message `"Access denied."`, code `ACCESS_DENIED` |
| 404 | Settings row missing | `ApiError`, codes `RESOURCE_NOT_FOUND` / `COMPANY_NOT_FOUND` |
| 500 | Unexpected errors — including DB constraint violations (e.g. duplicate `companyCode`) | `ApiError`, code `INTERNAL_SERVER_ERROR` |

Relevant `ErrorCode` enum values seen by settings flows: `VALIDATION_FAILED`, `INVALID_REQUEST`, `RESOURCE_NOT_FOUND`, `COMPANY_NOT_FOUND`, `ACCESS_DENIED`, `INTERNAL_SERVER_ERROR`.

---

## 8. Authentication and Authorization

* **Every settings endpoint requires authentication** and one of the roles **SUPER_ADMIN, HR_ADMIN, MANAGER** (enforced with `@PreAuthorize` on each method).
* All six endpoints share the **same** role set — there are no read-only vs admin distinctions, and no MANAGER-only or SUPER_ADMIN-only operation in this module.
* There are **no user-level or organization-level settings**: the data is global (single row per category). Every user with one of the three roles sees and can change the same values.
* **Frontend guidance:** render the Settings menu only for users with one of the three roles. For other roles, hide the menu (the backend will answer 403 anyway). On 403 responses, show a generic "Access denied." message rather than the form.

---

## 9. Dependency and Impact Analysis

| Settings Component | Dependent Module | What Is Used | Impact of Changing It |
|---|---|---|---|
| `officeStartTime`, `gracePeriodMinutes` | attendance (`AttendanceServiceImpl`) | Late status & late-minutes calculation | Next check-ins classify differently; existing records unchanged |
| `minimumWorkingMinutes`, `halfDayWorkingMinutes` | attendance (`AttendanceServiceImpl`) | Final day status at checkout | Checkout results change immediately |
| `officeEndTime` | attendance + notification (`NotificationServiceImpl`) | Early-exit, overtime base, missed-checkout scheduler, reminder text | Reminder texts and cutoffs shift |
| `checkoutCutoffMinutes` | attendance scheduler (`AttendanceScheduler` → `markMissedCheckouts`) | When missing checkouts are auto-closed | Later/earlier auto-MISSED_CHECKOUT |
| `overtimeEnabled` | attendance | Overtime minutes calculation | Toggling off zeroes overtime instantly |
| `weekendAttendanceAllowed`, `holidayAttendanceAllowed` | attendance validation | Check-in rejection on weekends/holidays | Toggling enables/blocks weekend/holiday check-ins |
| `carryForwardAllowed`, `monthlyGuideline` | leave (`LeaveExpiryServiceImpl`, scheduler) | Monthly unused-leave expiry | Directly changes how much leave expires each month-end |
| `annualPaidLeave` | leave (`LeaveAllocationServiceImpl`) | Fallback allocation for paid leave types | New allocations without explicit days use this number |
| `monthlyGuideline` | payroll (`PayrollServiceImpl`) | Allowed monthly paid leave → LOP | Payroll loss-of-pay changes for the next run |
| CompanySettings (any field) | **none** | — | No backend impact; display only |
| NotificationSettings / WorkLogSettings | **disabled** | — | No impact (endpoints 404) |

---

## 10. Unused Code Analysis (evidence-based, nothing deleted)

| Item | Status | Evidence |
|---|---|---|
| `settings.notification.*` (7 classes) | **Not used — commented out** | All classes commented; controller endpoints commented; seeder commented; zero imports elsewhere in the project |
| `settings.workLogs.*` (7 classes) | **Not used — commented out** | Same pattern; endpoints were already commented before the cleanup |
| 7 attendance fields (`attendanceRegularizationEnabled`, `multipleBreaksAllowed`, `maximumBreakMinutes`, `maximumBreaksPerDay`, `lateMarkEnabled`, `earlyExitEnabled`, `autoCheckoutEnabled`) | **Not used** — commented out of entity/DTOs/mapper | Project-wide getter search found no reader outside the settings module; `maximumBreaksPerDay` was additionally never seeded (stayed `null` historically) |
| 11 leave fields (`halfDayLeaveAllowed`, notice-day trio, approval flags, holiday/weekend/auto-approve/negative-balance/backdated flags) | **Not used** — commented out | No reader outside the settings module; only self-referential validation existed (now commented) |
| `LeaveSettingsServiceImpl.validateLeaveSettings()` body | **Not used** (method kept as empty stub) | Referenced only the removed request fields |
| Duplicate `active` fields formerly in each entity | **Removed (commented)** | Shadowed the inherited `BaseSettings.active`; Hibernate mapped the inherited column (verified in runtime SQL) |
| `logoUrl` (CompanySettings) | **Partially used** — readable in response, not writable | Present in response DTO/mapper; absent from request DTO and `updateEntity` |
| CompanySettings fields as a group | **API-only** | No backend reader; row still required (service throws without it) |
| Repository method `findFirstByActiveTrue()` on each live repository | **Used** | Called by each settings service; `PayrollServiceImpl` also calls it on `LeaveSettingsRepository` |
| `seedWorkLogSettings()` / `seedNotificationSettings()` | **Not used** | Commented out with their repository fields/imports |

---

## 11. Frontend Implementation Flow

1. User opens the Settings page (visible only for SUPER_ADMIN / HR_ADMIN / MANAGER).
2. Fire the three GET calls in parallel: `/attendance`, `/leave`, `/company`.
3. While loading, show skeletons and disable forms.
4. Populate the three forms from `data` of each response. Show `logoUrl` read-only.
5. User edits; validate per §3.3 / §5 rules before enabling Save.
6. On Save: PUT the **complete** object for that category (GET-value merged with edits — never a partial body).
7. On 200: update the form from the response `data`, show `message` as a success toast ("... updated successfully.").
8. On 400: show `message` from `ApiError` near the offending area. Because the backend returns only the **first** validation message, run full frontend validation first so users fix everything in one pass.
9. On 403: show "Access denied." and keep the form read-only/hidden per permission model.
10. On 404: show "Settings not initialized" empty state and hide the form.
11. On 500: show a generic error; for Company, a duplicate `companyCode` may surface here (backend does not pre-check uniqueness — see §14).
12. Cancel/Reset: re-run the GET for that section and repopulate.

---

## 12. Frontend API Integration Checklist

**API service methods**
- [ ] `getAttendanceSettings()` — GET `/api/v1/settings/attendance`
- [ ] `updateAttendanceSettings(body)` — PUT `/api/v1/settings/attendance`
- [ ] `getLeaveSettings()` — GET `/api/v1/settings/leave`
- [ ] `updateLeaveSettings(body)` — PUT `/api/v1/settings/leave`
- [ ] `getCompanySettings()` — GET `/api/v1/settings/company`
- [ ] `updateCompanySettings(body)` — PUT `/api/v1/settings/company`

**Models / interfaces**
- [ ] `AttendanceSettingsRequest` / `AttendanceSettingsResponse` (9 fields + id/active)
- [ ] `LeaveSettingsRequest` / `LeaveSettingsResponse` (3 fields + id/active)
- [ ] `CompanySettingsRequest` (14 fields) / `CompanySettingsResponse` (14 + id/logoUrl/active)
- [ ] Generic `ApiResponse<T>` and `ApiError` wrappers
- [ ] Time fields typed as `"HH:mm:ss"` strings (or a `LocalTimeString` branded type)

**Pages/components**
- [ ] Settings page with Attendance / Leave / Company sections
- [ ] TimePicker, NumberInput, Toggle, TextInput, EmailInput components
- [ ] Loading skeletons, empty state (404), error banner, success toast

**Validation**
- [ ] Attendance: all 9 fields required, time format, non-negative numbers (frontend-enforced; backend does not)
- [ ] Leave: 3 fields required, `≥ 0` (backend-enforced too)
- [ ] Company: full mirror of §3.3 rules (backend-enforced)

**Permissions**
- [ ] Gate the menu/route on role ∈ {SUPER_ADMIN, HR_ADMIN, MANAGER}
- [ ] Handle 403 gracefully

**Error handling**
- [ ] Parse `ApiError` (`message`, `errorCode`) for all non-2xx
- [ ] 400 → inline/highlight message; 404 → empty state; 500 → generic fallback

**Defaults**
- [ ] There is no "reset to default" API. Seeder defaults (§6) can be shown as placeholder hints only.

---

## 13. Important Implementation Notes (read before starting)

1. **Partial PUT bodies corrupt data.** All mappers (`AttendanceSettingsMapper`, `LeaveSettingsMapper`, `CompanySettingsMapper`) copy every request field unconditionally. Omitted fields become `null` in the DB. Always send the full object.
2. **Attendance request fields have no backend validation.** Sending `null`s or nonsense values will either fail on DB `NOT NULL` constraints (500) or silently produce odd behavior (e.g. grace = −5). Validate fully on the frontend.
3. **Leave/company PUT bodies are validated** and will 400 with a single first-error message.
4. **Time values are `HH:mm:ss` strings** in both directions; a bad format yields the exact message quoted in §7.
5. **`companyCode` uniqueness is DB-only.** There is no friendly duplicate check; a duplicate likely surfaces as 500 `INTERNAL_SERVER_ERROR`. Avoid letting users change `companyCode` casually, or confirm desired behavior with the backend team.
6. **`logoUrl` is read-only** via API; uploading/managing a logo is out of scope for this module (no upload endpoint found in the settings code).
7. **`timeZone` is informational** — schedulers use server time; changing it does not reschedule anything (verified: no code reads it).
8. **Settings are global.** Nothing is per-user or per-organization; there is no settings history/audit trail beyond `createdAt`/`updatedAt`.
9. **Notification and Work-Log settings do not exist** as endpoints (404). Do not build tabs for them until backend re-enables them.
10. **No DELETE, no create, no listing, no pagination** anywhere in the module.
11. **Response `message` strings are stable** and user-friendly; they can be shown directly in toasts.
12. **Only the first Bean-Validation error** is returned per request — plan the UX accordingly.
13. **No cross-field checks are active** (e.g. half-day vs full-day minutes, monthly vs annual leave). The frontend should either enforce sensible rules or get product/backend sign-off.

---

## 14. Final Summary

### Settings Module Overview
A global configuration module storing one active row each for **Attendance**, **Leave**, and **Company** settings, seeded at startup, editable via `GET`/`PUT` under `/api/v1/settings/*`, and consumed directly by the attendance, leave, and payroll engines. Notification and Work-Log settings submodules exist in the codebase but are fully disabled (commented out).

### Frontend Development Scope
One Settings page, three forms, six API calls. No lists, no CRUD beyond update, no pagination. Full frontend validation for attendance (backend does none there), mirrored validation for leave/company, complete-object PUTs, role-gated menu (SUPER_ADMIN / HR_ADMIN / MANAGER), and explicit handling of 400/403/404/500.

### APIs Required
`GET/PUT /api/v1/settings/attendance` · `GET/PUT /api/v1/settings/leave` · `GET/PUT /api/v1/settings/company` — all with roles SUPER_ADMIN, HR_ADMIN, MANAGER.

### Entities/Models Required (frontend interfaces)
`AttendanceSettingsRequest/Response`, `LeaveSettingsRequest/Response`, `CompanySettingsRequest/Response`, plus `ApiResponse<T>` and `ApiError` wrappers (field lists in §4).

### Dependencies
Attendance engine (late/half-day/overtime/missed-checkout), attendance validation (weekend/holiday), checkout-reminder notifications, leave expiry scheduler, leave allocation fallback, payroll LOP calculation. Company settings currently affect nothing backend-side.

### Open Questions / Backend Issues
1. **Duplicate `companyCode`** has no friendly error path (DB constraint → 500). Should the backend pre-check and return 400?
2. **No cross-field validation** for attendance minutes or leave numbers — confirm intended rules (e.g. `halfDayWorkingMinutes < minimumWorkingMinutes`, `monthlyGuideline ≤ annualPaidLeave`).
3. **`logoUrl` is not updatable** — is logo management planned elsewhere?
4. **`timeZone` and other Company fields are API-only** — confirm they are for display only or future enforcement.
5. **Notification/Work-Log settings** are disabled in backend — confirm they will not be needed by the frontend roadmap before deleting the idea of those tabs.
6. Attendance GET/PUT responses expose `active`; there is no API to change it — confirm deactivation is intentionally out of scope.
