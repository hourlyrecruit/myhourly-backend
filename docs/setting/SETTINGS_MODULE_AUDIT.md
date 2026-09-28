# Settings Module Audit — Unused Code & Fields Report

**Date:** 2026-09-09 (rev. 2 — final state after second pass)
**Scope:** `com.my_hourly.settings` module and all submodules
**Branch:** `attendance_regularization/service`

## Revision history

| Rev | Date | Summary |
|---|---|---|
| 1 | 2026-09-09 | Initial audit: workLogs submodule + duplicate `active` fields disabled; API-only fields kept. |
| 2 | 2026-09-09 | Second pass per review: all API-only unused fields commented out in Attendance/Leave settings; NotificationSettings submodule disabled like workLogs; CompanySettings kept intact. Corrected inventory file counts. |

---

## 1. Objective

Verify how the `settings` module and its submodules are used across the project, identify unused
entity fields, review the related data initializers, and disable (comment out — never delete) any
confirmed-unused code, fields, or initialization logic.

**Rule applied for "used":** a field counts as used only if some business logic actually *reads*
it (service, scheduler, mapper of another module, or repository query). Fields that merely
round-trip through the settings REST API (Request DTO → entity → Response DTO) but are never
enforced anywhere are treated as unused.

**Safety rule:** nothing was deleted. Everything confirmed unused was commented out with a
`// DISABLED:` note explaining why, so it can be re-enabled easily.

**Review decisions (user-confirmed):**
- Pass 1: strict "no business-logic reader = unused" rule; Company & Notification kept (API-only).
- Pass 2: additionally comment out all API-only unused fields; CompanySettings kept as-is per
  review; NotificationSettings disabled entirely like workLogs.

---

## 2. Module inventory

The settings module contains 37 Java files organized as:

| Submodule | Files | Contents | Final state |
|---|---|---|---|
| *(root)* | 1 | `BaseSettings` (`@MappedSuperclass` with `active` column) | **active** (used by 3 entities) |
| `attendance` | 7 | Entity, 2 DTOs, mapper, repository, service + impl | **active** — trimmed to used fields |
| `company` | 7 | Same structure | **active** — untouched per review |
| `leave` | 7 | Same structure | **active** — trimmed to used fields |
| `notification` | 7 | Same structure | **DISABLED** (all files commented out) |
| `workLogs` | 7 | Same structure | **DISABLED** (all files commented out) |
| `controller` | 1 | `SettingController` (`/api/v1/settings/**`) | attendance, leave, company endpoints live |

Total: 5 × 7 + 2 = 37 files, matching the original module inventory.

---

## 3. Cross-module usage of the settings module

All external references were traced by searching for imports of `com.my_hourly.settings.*`
across the whole project and then reading each consumer call site.

### 3.1 `settings.attendance` — ACTIVELY USED

| Consumer | Fields read |
|---|---|
| `attendance.service.impl.AttendanceServiceImpl` | `officeStartTime`, `gracePeriodMinutes` (late status & late minutes), `minimumWorkingMinutes`, `halfDayWorkingMinutes` (final status), `officeEndTime`, `overtimeEnabled` (overtime minutes), `officeEndTime` + `checkoutCutoffMinutes` (`markMissedCheckouts()` scheduler) |
| `attendance.service.impl.AttendanceValidationServiceImpl` | `weekendAttendanceAllowed`, `holidayAttendanceAllowed` (check-in validation) |
| `notification.service.impl.NotificationServiceImpl` | `officeEndTime` (checkout reminder text) |

### 3.2 `settings.leave` — ACTIVELY USED

| Consumer | Fields read |
|---|---|
| `leave.service.impl.LeaveExpiryServiceImpl` | `carryForwardAllowed`, `monthlyGuideline` (month-end expiry algorithm) |
| `leave.service.impl.LeaveAllocationServiceImpl` | `annualPaidLeave` (fallback allocation for paid leave types) |
| `payroll.service.impl.PayrollServiceImpl` | `monthlyGuideline` (allowed monthly paid leave → LOP calculation, via `LeaveSettingsRepository.findFirstByActiveTrue()`) |

### 3.3 `settings.company` — API-ONLY (kept per review)

Exposed through `GET/PUT /api/v1/settings/company`. No business module reads any field.
Kept fully intact by explicit review decision (a frontend may consume it).

### 3.4 `settings.notification` — FULLY UNUSED → DISABLED

No business module reads any flag (notification behavior is hard-coded in the notification
module). Consumers were only SettingController and the DataInitializer seed — both disabled.

### 3.5 `settings.workLogs` — FULLY UNUSED → DISABLED

Controller endpoints were already commented out (pre-existing WIP); no other references; its
only writer was the DataInitializer seed.

---

## 4. Field-level changes per entity

### 4.1 AttendanceSettings — 7 fields commented out

| Field | Business-logic reader | Verdict |
|---|---|---|
| `officeStartTime` | AttendanceServiceImpl | **kept** |
| `officeEndTime` | AttendanceServiceImpl, NotificationServiceImpl | **kept** |
| `gracePeriodMinutes` | AttendanceServiceImpl | **kept** |
| `minimumWorkingMinutes` | AttendanceServiceImpl | **kept** |
| `halfDayWorkingMinutes` | AttendanceServiceImpl | **kept** |
| `checkoutCutoffMinutes` | AttendanceServiceImpl.markMissedCheckouts | **kept** |
| `overtimeEnabled` | AttendanceServiceImpl | **kept** |
| `weekendAttendanceAllowed` | AttendanceValidationServiceImpl | **kept** |
| `holidayAttendanceAllowed` | AttendanceValidationServiceImpl | **kept** |
| `attendanceRegularizationEnabled` | none | **commented out** (entity + DTOs + mapper + seed) |
| `multipleBreaksAllowed` | none | **commented out** |
| `maximumBreakMinutes` | none | **commented out** |
| `maximumBreaksPerDay` | none; never seeded (stayed `null` in DB) | **commented out** |
| `lateMarkEnabled` | none | **commented out** |
| `earlyExitEnabled` | none | **commented out** |
| `autoCheckoutEnabled` | none | **commented out** |
| duplicate `active` | shadowed inherited field | **commented out** (see §4.6) |

### 4.2 LeaveSettings — 11 fields commented out

| Field | Business-logic reader | Verdict |
|---|---|---|
| `carryForwardAllowed` | LeaveExpiryServiceImpl | **kept** |
| `monthlyGuideline` | LeaveExpiryServiceImpl, PayrollServiceImpl | **kept** |
| `annualPaidLeave` | LeaveAllocationServiceImpl | **kept** |
| `halfDayLeaveAllowed` | none | **commented out** |
| `minimumAdvanceNoticeDays` | none (was only self-validated) | **commented out** |
| `maximumAdvanceNoticeDays` | none | **commented out** |
| `maximumConsecutiveLeaveDays` | none | **commented out** |
| `managerApprovalRequired` | none | **commented out** |
| `hrApprovalRequired` | none | **commented out** |
| `allowLeaveOnHoliday` | none | **commented out** |
| `allowLeaveOnWeekend` | none | **commented out** |
| `autoApproveLeave` | none | **commented out** |
| `allowNegativeLeaveBalance` | none | **commented out** |
| `allowBackdatedLeaveApplication` | none | **commented out** |
| duplicate `active` | shadowed inherited field | **commented out** |

Consequence: `LeaveSettingsServiceImpl.validateLeaveSettings()` referenced only the removed
request fields — its body was commented out (method kept as a marked stub so the update flow
still calls it safely).

### 4.3 CompanySettings — untouched per review

All 14 business fields kept (entity, DTOs, mapper, service, endpoints, seed). Only the
duplicate `active` was commented out in pass 1.

### 4.4 NotificationSettings — entire submodule disabled

All files commented out (package lines preserved):
entity, `NotificationSettingsRequest`, `NotificationSettingsResponse`, mapper, repository,
service interface, `NotificationSettingsServiceImpl`. Also disabled: the two controller
endpoints + field + imports in `SettingController`, and the imports/field/`seedNotificationSettings()`
in `DataInitializer`.

### 4.5 WorkLogSettings — entire submodule disabled (pass 1)

All 7 files commented out with the same pattern; DataInitializer seed and controller remnants
disabled.

### 4.6 The duplicate `active` problem (all entities)

Each entity redeclared an `active` field that already exists in `BaseSettings`. The subclass
member *shadowed* the inherited one (`this.active` vs `super.active`):

* Hibernate mapped the **inherited** column (test-run SQL shows `select ... active ... where active=true`).
* The subclass fields were dead weight; in `CompanySettings`/`NotificationSettings` they had no
  initializer, risking a null write if ever set directly.

Duplicates were commented out in all entities with an explanatory note. **No effective JPA
mapping changed**, so with `ddl-auto=update` the leftover columns simply stay (harmless); with
`create/create-drop` the removed columns would no longer be created.

---

## 5. Data initializers (`common.initializer.DataInitializer`)

Two `ApplicationRunner`s exist: `DataInitializer` (`@Order(1)`) and `SeedRunner` (`@Order(2)`,
users only — does not touch settings).

| Seed method | Required? | Reason | Action |
|---|---|---|---|
| `seedCompanySettings()` | **Yes** | service throws `ResourceNotFoundException` without a row | kept (untouched) |
| `seedAttendanceSettings()` | **Yes** | check-in/out flow depends on the row | kept — unused builder lines commented |
| `seedLeaveSettings()` | **Yes** | leave expiry/allocation + payroll depend on the row | kept — unused builder lines commented |
| `seedNotificationSettings()` | No | fed only the disabled submodule | **commented out** (incl. imports + field) |
| `seedWorkLogSettings()` | No | fed only the disabled submodule | **commented out** (incl. imports + field) |
| `initializeHoliday()` | out of scope | unrelated to settings | kept |

Note: the `.active(true)` builder calls were commented out because plain `@Builder` does not
expose inherited `BaseSettings` fields. Behavior-neutral: the `active` column defaults to
`true` in `BaseSettings`. (`@SuperBuilder` was evaluated but would require converting the whole
`BaseEntity` hierarchy — rejected as too invasive.)

---

## 6. Dead code elsewhere related to settings

* `SettingController`: workLog endpoints were already commented out (user WIP); notification
  endpoints/field/imports now commented out too. attendance/leave/company endpoints live.
* No raw SQL, migrations, or other initializers reference the settings tables.
* `BaseSettings` is used by the three live entities — kept.
* `DataInitializer` also contains large commented-out user/HR/manager seeding blocks —
  unrelated to settings, left as-is.

---

## 7. Deliberately NOT changed

* **CompanySettings** — kept fully intact per explicit review decision.
* **Used fields** in Attendance/Leave settings — untouched.
* **`BaseSettings`** — used, untouched.
* Dead user/HR/manager seeding blocks in `DataInitializer` — out of scope.

---

## 8. Changes made (file-by-file)

| File | Change |
|---|---|
| `settings/attendance/entity/AttendanceSettings.java` | comment out 7 unused fields + duplicate `active` |
| `settings/attendance/dto/request/AttendanceSettingsRequest.java` | comment out same 7 fields |
| `settings/attendance/dto/response/AttendanceSettingsResponse.java` | comment out same 7 fields |
| `settings/attendance/mapper/AttendanceSettingsMapper.java` | comment out the 7 fields' mapping lines (both directions) |
| `settings/company/entity/CompanySettings.java` | comment out duplicate `active` only (module otherwise untouched per review) |
| `settings/leave/entity/LeaveSettings.java` | comment out 11 unused fields + duplicate `active` |
| `settings/leave/dto/request/LeaveSettingsRequest.java` | comment out same 11 fields (with their `@NotNull`/`@Min`) |
| `settings/leave/dto/response/LeaveSettingsResponse.java` | comment out same 11 fields |
| `settings/leave/mapper/LeaveSettingsMapper.java` | comment out the 11 fields' mapping lines (both directions) |
| `settings/leave/service/impl/LeaveSettingsServiceImpl.java` | comment out body of `validateLeaveSettings` (only referenced removed fields) |
| `settings/notification/**` (7 files) | entire submodule commented out (package lines kept) |
| `settings/workLogs/**` (7 files) | entire submodule commented out (package lines kept) |
| `settings/controller/SettingController.java` | comment out notification + workLog imports, notification field, notification endpoints |
| `common/initializer/DataInitializer.java` | comment out workLog + notification imports/fields/seed calls; unused attendance/leave seed lines; `.active(true)` builder calls |

Nothing was deleted; every removal is a comment with a `// DISABLED:` marker and a reason.

---

## 9. Verification

* `mvnw compile` — **passes** (same warnings as the pre-change baseline).
* `mvnw test` — 22 tests: **20 pass**, 2 fail. The 2 failures
  (`AttendanceRegularizationServiceTest.approveDetail_Success` / `rejectDetail_Success`) are a
  **pre-existing** null-mock issue in the attendance module's test setup — identical before and
  after every pass of this audit. None of the changed files participate in those tests.
* The Spring context-load test (`MyHourlyApplicationTests`) **passes**, proving the application
  boots with the trimmed Attendance/Leave entities, without the two disabled submodules, and
  with `DataInitializer` seeding only the three required settings rows.
* User WIP in `SettingController` and `PayrollServiceImpl` was preserved untouched.

---

## 10. How to re-enable

1. **workLogs / notification submodules:** uncomment the bodies of the files under
   `com/my_hourly/settings/{workLogs,notification}/**` (each file kept its `package` line), then
   uncomment the matching imports/fields/endpoints in `SettingController` and the imports,
   repository fields and seed methods in `DataInitializer`. Search for `DISABLED` — every spot
   is marked.
2. **Attendance/Leave unused fields:** uncomment the marked field blocks in the entity, both
   DTOs, the mapper (both directions), the corresponding `.builder(...)` lines in
   `DataInitializer`, and `validateLeaveSettings()` in `LeaveSettingsServiceImpl` (leave only).
3. **Duplicate `active` fields:** uncomment the marked block in the respective entity. The
   preferred long-term fix is `@SuperBuilder` across the `BaseEntity` → `BaseSettings`
   hierarchy, which would also let builders set `active` directly.
4. **`.active(true)` seed calls:** re-enable together with `@SuperBuilder` (default is already
   `true`, so re-enabling is optional).

---

## 11. Recommendations (follow-up, not done here)

* Delete (rather than comment) the disabled workLogs/notification submodules once the team
  confirms they will not return; commented-out code rots quickly.
* Introduce `@SuperBuilder` on `BaseEntity`/`BaseSettings` so inherited fields (notably
  `active`) are settable through builders, eliminating the shadowing pattern that caused the
  entity changes in this audit.
* API surface shrank by design: `/api/v1/settings/notification` and `/api/v1/settings/work-log`
  now return 404. If a frontend still calls them, re-enable the modules or update the clients.
* Enforce or retire CompanySettings fields the same way once its fate is decided.
* Fix the pre-existing `AttendanceRegularizationServiceTest` null-mock failures.
* Consider removing the large dead user/HR/manager seeding blocks in `DataInitializer`.
