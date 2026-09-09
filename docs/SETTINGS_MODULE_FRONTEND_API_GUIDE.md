# Settings Module – Frontend API Implementation Guide

**Date:** 2026-09-09
**Base URL path:** `/api/v1/settings`
**Source of truth:** `SettingController` and the Settings request/response DTOs in the backend. Nothing here is invented; anything not defined in the backend is marked **"Not defined in backend"**.

> The backend also *had* notification and work-log settings endpoints. They are **disabled (commented out) in the backend** and return **404**. Do not integrate them.

**All success responses** are wrapped like this (envelope `ApiResponse<T>`):

```json
{
  "success": true,
  "message": "Attendance settings updated successfully.",
  "data": { ... },
  "timestamp": "2026-09-09T14:55:00.123456"
}
```

`data` is the payload documented in §4. All error responses use the format in §10.

---

## 1. API List

| Method | Endpoint | Purpose | Access Role |
| ------ | -------- | ------- | ----------- |
| GET | `/api/v1/settings/attendance` | Fetch active attendance settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/attendance` | Update attendance settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| GET | `/api/v1/settings/leave` | Fetch active leave settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/leave` | Update leave settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| GET | `/api/v1/settings/company` | Fetch active company settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| PUT | `/api/v1/settings/company` | Update company settings | SUPER_ADMIN, HR_ADMIN, MANAGER |
| ~~GET~~ | ~~`/api/v1/settings/notification`~~ | **Disabled in backend — returns 404** | — |
| ~~PUT~~ | ~~`/api/v1/settings/notification`~~ | **Disabled in backend — returns 404** | — |
| ~~GET~~ | ~~`/api/v1/settings/work-log`~~ | **Disabled in backend — returns 404** | — |
| ~~PUT~~ | ~~`/api/v1/settings/work-log`~~ | **Disabled in backend — returns 404** | — |

There are no other Settings APIs — no create, delete, list, search, pagination, or sorting endpoints exist in the backend.

---

## 2. API Details

### 2.1 Get Attendance Settings

| Item | Value |
|---|---|
| API name | Get Attendance Settings |
| HTTP method | GET |
| Endpoint | `/api/v1/settings/attendance` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | None |
| Response body | Envelope with `data` = `AttendanceSettingsResponse` (§4.1) |
| HTTP status codes | 200 on success; 404 if the settings row does not exist; 401/403 (see §10) |

### 2.2 Update Attendance Settings

| Item | Value |
|---|---|
| API name | Update Attendance Settings |
| HTTP method | PUT |
| Endpoint | `/api/v1/settings/attendance` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | `AttendanceSettingsRequest` (§3.1) |
| Response body | Envelope with `data` = `AttendanceSettingsResponse` (§4.1) |
| HTTP status codes | 200 on success; 400 for malformed body/time format; 404 if the settings row does not exist; 500 if required fields are sent as `null` |

### 2.3 Get Leave Settings

| Item | Value |
|---|---|
| API name | Get Leave Settings |
| HTTP method | GET |
| Endpoint | `/api/v1/settings/leave` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | None |
| Response body | Envelope with `data` = `LeaveSettingsResponse` (§4.2) |
| HTTP status codes | 200 on success; 404 if the settings row does not exist; 401/403 (see §10) |

### 2.4 Update Leave Settings

| Item | Value |
|---|---|
| API name | Update Leave Settings |
| HTTP method | PUT |
| Endpoint | `/api/v1/settings/leave` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | `LeaveSettingsRequest` (§3.2) |
| Response body | Envelope with `data` = `LeaveSettingsResponse` (§4.2) |
| HTTP status codes | 200 on success; 400 for validation failures; 404 if the settings row does not exist |

### 2.5 Get Company Settings

| Item | Value |
|---|---|
| API name | Get Company Settings |
| HTTP method | GET |
| Endpoint | `/api/v1/settings/company` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | None |
| Response body | Envelope with `data` = `CompanySettingsResponse` (§4.3) |
| HTTP status codes | 200 on success; 404 (`COMPANY_NOT_FOUND`) if the settings row does not exist; 401/403 (see §10) |

### 2.6 Update Company Settings

| Item | Value |
|---|---|
| API name | Update Company Settings |
| HTTP method | PUT |
| Endpoint | `/api/v1/settings/company` |
| Access role | SUPER_ADMIN, HR_ADMIN, MANAGER |
| Path parameters | None |
| Query parameters | None |
| Request body | `CompanySettingsRequest` (§3.3) |
| Response body | Envelope with `data` = `CompanySettingsResponse` (§4.3) |
| HTTP status codes | 200 on success; 400 for validation failures; 404 if the settings row does not exist; 500 if `companyCode` violates the unique constraint |

---

## 3. Request

### 3.1 AttendanceSettingsRequest (PUT `/api/v1/settings/attendance`)

| Field | Data Type | Required | Description |
| ----- | --------- | -------- | ----------- |
| `officeStartTime` | String (`HH:mm:ss`) | Yes* | Office start time; after `officeStartTime + gracePeriodMinutes`, check-in counts as LATE |
| `officeEndTime` | String (`HH:mm:ss`) | Yes* | Office end time; base for early-exit, overtime, missed-checkout cutoff |
| `gracePeriodMinutes` | Integer | Yes* | Minutes after office start still treated as on time |
| `minimumWorkingMinutes` | Integer | Yes* | Working minutes required to keep PRESENT status at checkout |
| `halfDayWorkingMinutes` | Integer | Yes* | Working minutes required for HALF_DAY instead of ABSENT |
| `checkoutCutoffMinutes` | Integer | Yes* | Minutes after office end when a missing checkout is auto-marked MISSED_CHECKOUT |
| `overtimeEnabled` | Boolean | Yes* | Whether overtime minutes are calculated |
| `weekendAttendanceAllowed` | Boolean | Yes* | Whether check-in is allowed on Saturday/Sunday |
| `holidayAttendanceAllowed` | Boolean | Yes* | Whether check-in is allowed on holidays that disallow attendance |

\* The backend declares **no** Bean-Validation annotations on these fields ("Required" here reflects database `NOT NULL` columns, not API validation). However, the update mapper copies **every** field from the request into the entity — omitted fields become `null` and the database rejects them with HTTP 500. **Always send all 9 fields with values.**

**JSON request example:**

```json
{
  "officeStartTime": "09:30:00",
  "officeEndTime": "18:30:00",
  "gracePeriodMinutes": 45,
  "minimumWorkingMinutes": 480,
  "halfDayWorkingMinutes": 240,
  "checkoutCutoffMinutes": 180,
  "overtimeEnabled": false,
  "weekendAttendanceAllowed": false,
  "holidayAttendanceAllowed": false
}
```

### 3.2 LeaveSettingsRequest (PUT `/api/v1/settings/leave`)

| Field | Data Type | Required | Description |
| ----- | --------- | -------- | ----------- |
| `carryForwardAllowed` | Boolean | Yes (`@NotNull`) | `true` = unused monthly leave carries into the annual balance; `false` = expires at month-end |
| `monthlyGuideline` | Integer | Yes (`@NotNull`, `@Min(0)`) | Recommended paid-leave days per month; also used by payroll as allowed monthly paid leave |
| `annualPaidLeave` | Integer | Yes (`@NotNull`, `@Min(0)`) | Total annual paid-leave days; used as fallback allocation for paid leave types |

**JSON request example:**

```json
{
  "carryForwardAllowed": false,
  "monthlyGuideline": 2,
  "annualPaidLeave": 24
}
```

### 3.3 CompanySettingsRequest (PUT `/api/v1/settings/company`)

| Field | Data Type | Required | Description |
| ----- | --------- | -------- | ----------- |
| `companyName` | String (≤150) | Yes (`@NotBlank`) | Company display name |
| `companyCode` | String (≤30) | Yes (`@NotBlank`) | Short company code — **unique in the database** |
| `email` | String (≤150) | Yes (`@NotBlank`, `@Email`) | Company contact email |
| `phoneNumber` | String (≤20) | No | Digits only, 10–15 characters (`^[0-9]{10,15}$`) |
| `website` | String (≤150) | No | Company website |
| `addressLine1` | String (≤255) | No | Address line 1 |
| `addressLine2` | String (≤255) | No | Address line 2 |
| `city` | String (≤100) | No | City |
| `state` | String (≤100) | No | State |
| `country` | String (≤100) | No | Country |
| `postalCode` | String (≤20) | No | Postal code |
| `timeZone` | String (≤50) | Yes (`@NotBlank`) | Company time zone (informational display) |
| `currency` | String (≤10) | Yes (`@NotBlank`) | Company currency |
| `workingDaysPerWeek` | Integer | No (`@Min(1)`, `@Max(7)`) | Working days per week (1–7) |

Note: `logoUrl` is **not part of the request** — it cannot be changed through the API.

**JSON request example:**

```json
{
  "companyName": "MyHourly",
  "companyCode": "MHR",
  "email": "admin@myhourly.com",
  "phoneNumber": "9876543210",
  "website": "https://myhourly.com",
  "addressLine1": "Plot 12, Tech Park",
  "addressLine2": "HITEC City",
  "city": "Hyderabad",
  "state": "Telangana",
  "country": "India",
  "postalCode": "500081",
  "timeZone": "Asia/Kolkata",
  "currency": "INR",
  "workingDaysPerWeek": 5
}
```

---

## 4. Response

All responses use the envelope from the top of this document; the tables below describe `data`.

### 4.1 AttendanceSettingsResponse

| Field | Data Type | Nullable | Description |
| ----- | --------- | -------- | ----------- |
| `id` | Long | No | Settings row ID |
| `officeStartTime` | String (`HH:mm:ss`) | No | Office start time |
| `officeEndTime` | String (`HH:mm:ss`) | No | Office end time |
| `gracePeriodMinutes` | Integer | No | Grace period in minutes |
| `minimumWorkingMinutes` | Integer | No | Full-day working-minute threshold |
| `halfDayWorkingMinutes` | Integer | No | Half-day working-minute threshold |
| `checkoutCutoffMinutes` | Integer | No | Missed-checkout cutoff in minutes after office end |
| `overtimeEnabled` | Boolean | No | Whether overtime is calculated |
| `weekendAttendanceAllowed` | Boolean | No | Whether weekend check-in is allowed |
| `holidayAttendanceAllowed` | Boolean | No | Whether holiday check-in is allowed |
| `active` | Boolean | No | Always `true` in practice; no API to change it |

**JSON response example (the `data` object):**

```json
{
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
}
```

Success `message` values: `"Attendance settings fetched successfully."` (GET), `"Attendance settings updated successfully."` (PUT).

### 4.2 LeaveSettingsResponse

| Field | Data Type | Nullable | Description |
| ----- | --------- | -------- | ----------- |
| `id` | Long | No | Settings row ID |
| `carryForwardAllowed` | Boolean | No | Whether unused monthly leave carries forward |
| `monthlyGuideline` | Integer | No | Recommended monthly paid-leave days |
| `annualPaidLeave` | Integer | No | Annual paid-leave days (fallback allocation) |
| `active` | Boolean | No | Always `true` in practice |

**JSON response example (the `data` object):**

```json
{
  "id": 1,
  "carryForwardAllowed": false,
  "monthlyGuideline": 2,
  "annualPaidLeave": 24,
  "active": true
}
```

Success `message` values: `"Leave settings fetched successfully."` (GET), `"Leave settings updated successfully."` (PUT).

### 4.3 CompanySettingsResponse

| Field | Data Type | Nullable | Description |
| ----- | --------- | -------- | ----------- |
| `id` | Long | No | Settings row ID |
| `companyName` | String | No | Company display name |
| `companyCode` | String | No | Short company code (unique) |
| `email` | String | No | Company contact email |
| `phoneNumber` | String | Yes | Company phone |
| `website` | String | Yes | Company website |
| `addressLine1` | String | Yes | Address line 1 |
| `addressLine2` | String | Yes | Address line 2 |
| `city` | String | Yes | City |
| `state` | String | Yes | State |
| `country` | String | Yes | Country |
| `postalCode` | String | Yes | Postal code |
| `timeZone` | String | No | Company time zone |
| `currency` | String | No | Company currency |
| `workingDaysPerWeek` | Integer | No | Working days per week (1–7) |
| `logoUrl` | String | Yes | Logo URL — **read-only**; not updatable via the API |
| `active` | Boolean | No | Always `true` in practice |

**JSON response example (the `data` object):**

```json
{
  "id": 1,
  "companyName": "MyHourly",
  "companyCode": "MHR",
  "email": "admin@myhourly.com",
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
}
```

Success `message` values: `"Company settings fetched successfully."` (GET), `"Company settings updated successfully."` (PUT).

*(The timestamp field of `createdAt`/`updatedAt` audit columns is not exposed in any Settings response — only `id` and `active` from the base class are mapped.)*

---

## 5. Enums

Settings request/response DTOs contain **no enum fields**. The only enum that can appear in a Settings API exchange is the error code in error responses:

| Enum | Values | Used In |
| ---- | ------ | ------- |
| `ErrorCode` (in error payloads) | Relevant to Settings: `VALIDATION_FAILED`, `INVALID_REQUEST`, `RESOURCE_NOT_FOUND`, `COMPANY_NOT_FOUND`, `ACCESS_DENIED`, `INTERNAL_SERVER_ERROR` | `errorCode` field of every error response (§10) |

(The backend `ErrorCode` enum contains many more values used by other modules; only the ones above can be returned by Settings APIs.)

---

## 6. Access Roles

Roles are enforced per endpoint with `@PreAuthorize` in the backend. All six Settings endpoints share the same role set:

| API | Role/Permission | Access |
| --- | --------------- | ------ |
| GET `/api/v1/settings/attendance` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Allowed for all three; denied for all other roles |
| PUT `/api/v1/settings/attendance` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Same as above |
| GET `/api/v1/settings/leave` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Same as above |
| PUT `/api/v1/settings/leave` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Same as above |
| GET `/api/v1/settings/company` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Same as above |
| PUT `/api/v1/settings/company` | `SUPER_ADMIN`, `HR_ADMIN`, `MANAGER` | Same as above |

There are no finer-grained permissions (no read-only role, no per-field restrictions) defined in the backend. Settings are **global** — the same values are visible to and editable by every user with one of these roles.

---

## 7. Datatypes

| Data Type | JSON appearance | Settings fields using it |
| --------- | --------------- | ------------------------ |
| Long (number) | `1` | `id` in all responses |
| Integer (number) | `45` | `gracePeriodMinutes`, `minimumWorkingMinutes`, `halfDayWorkingMinutes`, `checkoutCutoffMinutes`, `monthlyGuideline`, `annualPaidLeave`, `workingDaysPerWeek` |
| Boolean | `true` / `false` | `overtimeEnabled`, `weekendAttendanceAllowed`, `holidayAttendanceAllowed`, `carryForwardAllowed`, `active` |
| String (time) | `"09:30:00"` | `officeStartTime`, `officeEndTime` |
| String | `"MyHourly"` | All company fields |
| Date/DateTime | `"2026-09-09T14:55:00.123456"` | `timestamp` in the response envelope |
| Enum | `"VALIDATION_FAILED"` | `errorCode` in error responses only |
| Double / BigDecimal / UUID / List / Object | — | **Not used in any Settings API** |

**Date/time formats defined by the backend:**

| Type | Format | Notes |
|---|---|---|
| Time (`LocalTime`) | `HH:mm:ss` | Malformed values return 400 with message `"Invalid time format. Expected format: HH:mm:ss (example: 18:30:00)"` |
| DateTime (`LocalDateTime`) | `yyyy-MM-dd'T'HH:mm:ss` (ISO, e.g. `2026-09-09T14:55:00.123456`) | Used for the envelope `timestamp` |
| LocalDate | `yyyy-MM-dd` | Not used by Settings (global error-handler format listed for completeness) |

---

## 8. Validation

### Attendance (PUT `/attendance`) — backend validates **nothing**

| Field | Required | Validation |
| ----- | -------- | ---------- |
| all 9 fields | Effectively yes (see §3.1 warning) | **Not defined in backend** — the frontend must enforce: time format `HH:mm:ss`, non-negative minutes, sensible ranges. Sending `null` for any field causes HTTP 500 (DB `NOT NULL`). |

### Leave (PUT `/leave`) — validated by backend

| Field | Required | Validation |
| ----- | -------- | ---------- |
| `carryForwardAllowed` | Yes | `@NotNull` |
| `monthlyGuideline` | Yes | `@NotNull`, `@Min(0)` |
| `annualPaidLeave` | Yes | `@NotNull`, `@Min(0)` |

### Company (PUT `/company`) — validated by backend

| Field | Required | Validation |
| ----- | -------- | ---------- |
| `companyName` | Yes | `@NotBlank` ("Company name is required."), `@Size(max = 150)` |
| `companyCode` | Yes | `@NotBlank` ("Company code is required."), `@Size(max = 30)`; unique in DB (no friendly duplicate error — see §10) |
| `email` | Yes | `@NotBlank` ("Email is required."), `@Email` ("Invalid email format.") |
| `phoneNumber` | No | `@Pattern(regexp = "^[0-9]{10,15}$")` ("Phone number must contain 10 to 15 digits.") |
| `website` | No | `@Size(max = 150)` |
| `addressLine1` | No | `@Size(max = 255)` |
| `addressLine2` | No | `@Size(max = 255)` |
| `city` | No | `@Size(max = 100)` |
| `state` | No | `@Size(max = 100)` |
| `country` | No | `@Size(max = 100)` |
| `postalCode` | No | `@Size(max = 20)` |
| `timeZone` | Yes | `@NotBlank` ("Time zone is required.") |
| `currency` | Yes | `@NotBlank` ("Currency is required.") |
| `workingDaysPerWeek` | No | `@Min(1)`, `@Max(7)` |

**Behavior on validation failure:** the backend returns **only the first** failing message (HTTP 400). Run complete frontend validation first so users can fix everything in one pass.

---

## 9. Default Values

These defaults are inserted **once** by the backend's startup data initializer when the table is empty (they are the values a fresh environment returns):

| Setting | Data Type | Default Value |
| ------- | --------- | ------------- |
| `officeStartTime` | String (time) | `"09:30:00"` |
| `officeEndTime` | String (time) | `"18:30:00"` |
| `gracePeriodMinutes` | Integer | `45` |
| `minimumWorkingMinutes` | Integer | `480` |
| `halfDayWorkingMinutes` | Integer | `240` |
| `checkoutCutoffMinutes` | Integer | `180` |
| `overtimeEnabled` | Boolean | `false` |
| `weekendAttendanceAllowed` | Boolean | `false` |
| `holidayAttendanceAllowed` | Boolean | `false` |
| `carryForwardAllowed` | Boolean | `false` |
| `monthlyGuideline` | Integer | `2` |
| `annualPaidLeave` | Integer | `24` |
| `companyName` | String | `"MyHourly"` |
| `companyCode` | String | `"MHR"` |
| `email` (company) | String | Value of the backend property `app.super-admin.email` |
| `phoneNumber` | String | `"9876543210"` |
| `timeZone` | String | `"Asia/Kolkata"` |
| `currency` | String | `"INR"` |
| `workingDaysPerWeek` | Integer | `5` |
| `active` (all settings rows) | Boolean | `true` |

No default is defined in the backend for: `website`, `addressLine1`, `addressLine2`, `city`, `state`, `country`, `postalCode`, `logoUrl` (all stay `null`). There is **no "reset to defaults" API** — the table is for reference/placeholders only.

---

## 10. Error Response

Every error (all Settings endpoints) uses this shape (`ApiError`):

```json
{
  "success": false,
  "message": "Time zone is required.",
  "errorCode": "VALIDATION_FAILED",
  "path": "/api/v1/settings/company",
  "timestamp": "2026-09-09T14:55:00.123456"
}
```

| HTTP Status | When it happens | `errorCode` | Example `message` |
| ----------- | --------------- | ----------- | ----------------- |
| 400 | Bean-Validation failure on leave/company PUT (`@NotNull`, `@NotBlank`, `@Email`, `@Min`, `@Size`, `@Pattern`) | `VALIDATION_FAILED` | `"Company code is required."` — only the **first** failing field's message is returned |
| 400 | Service-level rule violation | `VALIDATION_FAILED` | *Currently none active for Settings — leave settings validation is commented out in the backend* |
| 400 | Malformed JSON body, wrong time format, wrong datatype | `INVALID_REQUEST` | `"Invalid time format. Expected format: HH:mm:ss (example: 18:30:00)"` / `"Invalid request format"` |
| 401 | Missing/expired authentication token | (security-layer response) | Handle globally in the app, same as other APIs |
| 403 | Authenticated user without SUPER_ADMIN / HR_ADMIN / MANAGER role | `ACCESS_DENIED` | `"Access denied."` |
| 404 | Settings row does not exist (initialization never ran / row missing) | `RESOURCE_NOT_FOUND` (attendance, leave) | `"Attendance settings not found."` / `"Leave settings not found."` |
| 404 | Company settings row does not exist | `COMPANY_NOT_FOUND` | `"Company settings not found."` |
| 500 | Unexpected backend error — including a duplicate `companyCode` violating the DB unique constraint on PUT `/company` | `INTERNAL_SERVER_ERROR` | `"An unexpected error occurred. (...)"` |

**Frontend handling checklist per call:** show `message` from the error body in a toast/inline alert; on 403 hide the form; on 404 show a "Settings not initialized" empty state; on 500 show a generic error (for company updates, warn the user if they changed `companyCode`).

---

## Final API Reference

| API | Method | Endpoint | Role | Request | Response |
| --- | ------ | -------- | ---- | ------- | -------- |
| Get Attendance Settings | GET | `/api/v1/settings/attendance` | SUPER_ADMIN, HR_ADMIN, MANAGER | — | `AttendanceSettingsResponse` |
| Update Attendance Settings | PUT | `/api/v1/settings/attendance` | SUPER_ADMIN, HR_ADMIN, MANAGER | `AttendanceSettingsRequest` | `AttendanceSettingsResponse` |
| Get Leave Settings | GET | `/api/v1/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | — | `LeaveSettingsResponse` |
| Update Leave Settings | PUT | `/api/v1/settings/leave` | SUPER_ADMIN, HR_ADMIN, MANAGER | `LeaveSettingsRequest` | `LeaveSettingsResponse` |
| Get Company Settings | GET | `/api/v1/settings/company` | SUPER_ADMIN, HR_ADMIN, MANAGER | — | `CompanySettingsResponse` |
| Update Company Settings | PUT | `/api/v1/settings/company` | SUPER_ADMIN, HR_ADMIN, MANAGER | `CompanySettingsRequest` | `CompanySettingsResponse` |
| Notification settings | GET/PUT | `/api/v1/settings/notification` | **Disabled in backend — 404** | — | — |
| Work-log settings | GET/PUT | `/api/v1/settings/work-log` | **Disabled in backend — 404** | — | — |
