# Implementation Plan: Configurable Sandwich Leave Policy

This plan implements three independently configurable sandwich leave settings that automatically expand leave requests to include adjacent/intervening weekend days, charging employees for those weekend days as part of their leave.

**Technology Stack:** Java 21, Spring Boot 4.0.7, PostgreSQL, JPA/Hibernate, Flyway, React (frontend)

**Build & Test:** Maven (`mvnw.cmd` on Windows), Spring Boot Test

---

## Implementation Steps

- [ ] 1. **Create database migration to add sandwich leave columns**
      
      Add three boolean columns to `leave_settings` table and one TEXT column to `leave_requests` table for storing forced working days.
      
      Files:
      - Create: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\resources\db\migration\V10__add_sandwich_leave_settings.sql`
      
      Content:
      ```sql
      -- Add sandwich leave policy settings to leave_settings table
      ALTER TABLE leave_settings 
      ADD COLUMN sandwich_leave_monday_enabled BOOLEAN NOT NULL DEFAULT false,
      ADD COLUMN sandwich_leave_friday_enabled BOOLEAN NOT NULL DEFAULT false,
      ADD COLUMN sandwich_leave_friday_monday_enabled BOOLEAN NOT NULL DEFAULT false;

      -- Add column to store forced working days (sandwich leave weekends)
      ALTER TABLE leave_requests
      ADD COLUMN forced_working_days_json TEXT;

      -- Add comments for documentation
      COMMENT ON COLUMN leave_settings.sandwich_leave_monday_enabled IS 
        'When enabled, taking leave on Monday forces the preceding Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days.';

      COMMENT ON COLUMN leave_settings.sandwich_leave_friday_enabled IS 
        'When enabled, taking leave on Friday forces the following Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days.';

      COMMENT ON COLUMN leave_settings.sandwich_leave_friday_monday_enabled IS 
        'When enabled, taking leave on both Friday and Monday forces the intervening Saturday and Sunday to be counted as working days, resulting in 4 total chargeable days. This rule takes precedence over the individual Friday and Monday rules.';

      COMMENT ON COLUMN leave_requests.forced_working_days_json IS
        'JSON array of dates (YYYY-MM-DD format) that should be counted as working days even if they fall on weekends. Used for sandwich leave policy enforcement. Example: ["2023-10-28", "2023-10-29"]';
      ```
      
      Verify: Run `mvnw.cmd clean compile` from worktree root and confirm no Flyway migration errors in logs. Check that Spring Boot application starts without database errors.

- [ ] 2. **Update LeaveSettings entity with three new boolean fields**
      
      Add `sandwichLeaveMondayEnabled`, `sandwichLeaveFridayEnabled`, and `sandwichLeaveFridayMondayEnabled` fields to the LeaveSettings entity with appropriate annotations and defaults.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\settings\leave\entity\LeaveSettings.java`
      
      Add these three fields with JavaDoc comments explaining each rule, `@Column` annotations matching the migration column names, and `@Builder.Default` set to `false`.
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors. Start the application and verify Hibernate schema validation passes.

- [ ] 3. **Update LeaveRequest entity to store forced working days**
      
      Add `forcedWorkingDaysJson` (persistent) and `forcedWorkingDays` (transient) fields with Jackson serialization/deserialization lifecycle hooks.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\leave\entity\LeaveRequest.java`
      
      Add: 
      - `@Column(name = "forced_working_days_json", columnDefinition = "TEXT")` String field
      - `@Transient Set<LocalDate> forcedWorkingDays` field
      - `@PostLoad/@PostPersist/@PostUpdate` method to deserialize JSON to Set
      - `@PrePersist/@PreUpdate` method to serialize Set to JSON
      - Getter/setter for `forcedWorkingDays` with null-safe initialization
      - Import Jackson `ObjectMapper` and `TypeReference`
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors. Test JSON serialization with a unit test.

- [ ] 4. **Update LeaveSettings DTOs (Request and Response)**
      
      Add the three new boolean fields to `LeaveSettingsRequest` and `LeaveSettingsResponse` DTOs.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\settings\leave\dto\request\LeaveSettingsRequest.java`
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\settings\leave\dto\response\LeaveSettingsResponse.java`
      
      In `LeaveSettingsRequest`, add three `@NotNull private Boolean` fields with matching names. In `LeaveSettingsResponse`, add three `private Boolean` fields with matching names.
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors.

- [ ] 5. **Update LeaveSettingsMapper to map new fields**
      
      Add mapping logic for the three new fields in both `toResponse()` and `updateEntity()` methods.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\settings\leave\mapper\LeaveSettingsMapper.java`
      
      In `toResponse()`, add three `.sandwichLeaveMondayEnabled(entity.getSandwichLeaveMondayEnabled())` builder calls (and Friday/FridayMonday variants). In `updateEntity()`, add three `entity.setSandwichLeaveMondayEnabled(request.getSandwichLeaveMondayEnabled())` calls (and variants).
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors.

- [ ] 6. **Update LeaveApplicationContext to include forcedWorkingDays**
      
      Add `Set<LocalDate> forcedWorkingDays` parameter to the `LeaveApplicationContext` record.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\leave\context\LeaveApplicationContext.java`
      
      Change the record declaration to add `Set<LocalDate> forcedWorkingDays` as the fifth parameter after `totalDays`.
      
      Verify: Run `mvnw.cmd clean compile` and fix any compilation errors in classes that construct `LeaveApplicationContext` (they will need to pass the new parameter).

- [ ] 7. **Implement sandwich leave expansion logic in LeaveValidationServiceImpl**
      
      Add `expandForSandwichLeave()` private method, `SandwichLeaveExpansion` record, modify `calculateLeaveDays()` to accept forcedWorkingDays, and inject `LeaveSettingsService`.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\leave\service\impl\LeaveValidationServiceImpl.java`
      
      Changes:
      1. Add `private final LeaveSettingsService leaveSettingsService;` field to constructor
      2. Add private `SandwichLeaveExpansion` record with `expandedStart`, `expandedEnd`, `forcedWorkingDays` fields
      3. Add private `expandForSandwichLeave(LocalDate startDate, LocalDate endDate)` method implementing the three-rule algorithm (Friday+Monday precedence, then Friday-only, then Monday-only)
      4. Add overloaded `calculateLeaveDays(LocalDate startDate, LocalDate endDate, Set<LocalDate> forcedWorkingDays)` that checks `forcedWorkingDays.contains(current)` before `isWeekend(current)` in the loop
      5. Keep existing `calculateLeaveDays(LocalDate, LocalDate)` as a delegate that passes `Set.of()`
      6. Modify `validateLeaveApplication()` to call `expandForSandwichLeave()`, pass results to the 3-param `calculateLeaveDays()`, and include `forcedWorkingDays` in the returned context
      
      Verify: Run `mvnw.cmd clean test -Dtest=LeaveValidationServiceImplTest` (or the appropriate test class) and confirm existing tests pass. Add a simple smoke test for the new method.

- [ ] 8. **Update LeaveRequestServiceImpl.applyLeave() to store forced working days**
      
      Set `forcedWorkingDays` on the `LeaveRequest` entity before saving.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\leave\service\impl\LeaveRequestServiceImpl.java`
      
      In the `applyLeave()` method, after building the `LeaveRequest` object, add `leaveRequest.setForcedWorkingDays(context.forcedWorkingDays());` before calling `leaveRequestRepository.save(leaveRequest)`.
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors.

- [ ] 9. **Update LeaveRequestServiceImpl.managerAction() to check attendance overlap for forced working days**
      
      Extend the existing attendance overlap check to also validate forced working days (sandwich weekends).
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\main\java\com\my_hourly\leave\service\impl\LeaveRequestServiceImpl.java`
      
      After the existing while-loop that checks attendance from `startDate` to `endDate`, add a new for-loop that iterates `leaveRequest.getForcedWorkingDays()`, skipping dates already checked in the main range, and throwing `ValidationException` if attendance exists on any forced date.
      
      Verify: Run `mvnw.cmd clean compile` and confirm no compilation errors.

- [ ] 10. **Create comprehensive unit tests for sandwich leave logic**
      
      Create a new test class with 10+ test cases covering all three rules, edge cases (month boundaries, public holidays on weekends), and integration with leave balance.
      
      Files:
      - Create: `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\src\test\java\com\my_hourly\leave\LeaveSandwichPolicyTest.java`
      
      Test cases:
      1. Baseline - No rules enabled (Friday=1 day, Monday=1 day, Fri+Mon=2 days)
      2. Monday rule enabled (Mon=3 days, Tue=1 day, Mon+Tue=4 days)
      3. Friday rule enabled (Fri=3 days, Thu=1 day, Thu+Fri=4 days)
      4. Friday+Monday rule enabled (Fri+Mon=4 days, Fri-only=1 day, Mon-only=1 day)
      5. All three rules enabled (precedence check)
      6. Public holiday on Saturday with Monday rule (still 3 days)
      7. Month boundary crossing (Friday Oct 31 expands into November)
      8. Leave balance deduction integration (5 days balance - Monday rule = 2 remaining)
      9. Insufficient balance validation (2 days balance + Monday rule = validation fails)
      10. Settings mapper round-trip (save and retrieve all three flags)
      
      Use `@SpringBootTest` or `@DataJpaTest` with mocked dependencies as appropriate. Each test should set up leave settings, create a leave request, call validation, and assert the `totalDays` and `forcedWorkingDays` values.
      
      Verify: Run `mvnw.cmd test -Dtest=LeaveSandwichPolicyTest` and confirm all tests pass.

- [ ] 11. **Update frontend Settings.jsx to add Sandwich Leave Policy section**
      
      Add a new section to the 'leave' group in the GROUPS array with three boolean toggle fields.
      
      Files:
      - Modify: `c:\Users\User\Desktop\Office Projects\my_hourly\HRMS\src\pages\Settings.jsx`
      
      In the GROUPS array, find the 'leave' group object. In its `fields` array, add three new field definitions:
      ```javascript
      {
        name: 'sandwichLeaveMondayEnabled',
        label: 'Monday Leave (with preceding weekend)',
        type: 'boolean',
        hint: 'Taking leave on Monday charges Saturday and Sunday (3 days total).'
      },
      {
        name: 'sandwichLeaveFridayEnabled',
        label: 'Friday Leave (with following weekend)',
        type: 'boolean',
        hint: 'Taking leave on Friday charges Saturday and Sunday (3 days total).'
      },
      {
        name: 'sandwichLeaveFridayMondayEnabled',
        label: 'Friday+Monday Leave (with weekend between)',
        type: 'boolean',
        hint: 'Taking leave on both Friday and Monday charges Saturday and Sunday (4 days total). This rule takes precedence over the individual Friday and Monday rules.'
      }
      ```
      
      In the same 'leave' group, in its `sections` array, add a new section object after 'Leave Rules':
      ```javascript
      {
        title: 'Sandwich Leave Policy',
        subtitle: 'Automatically charge weekends when leave is taken on specific days',
        icon: CalendarDays,
        fields: [
          'sandwichLeaveMondayEnabled',
          'sandwichLeaveFridayEnabled',
          'sandwichLeaveFridayMondayEnabled'
        ]
      }
      ```
      
      Verify: Start the frontend dev server, navigate to Settings → Leave, and confirm the new "Sandwich Leave Policy" card appears with three toggle switches. Toggle each switch, save, and reload the page to confirm persistence.

- [ ] 12. **Run full integration test suite**
      
      Execute the complete test suite to ensure no regressions in existing leave management functionality.
      
      Command: `mvnw.cmd clean test` from `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave`
      
      Verify: All tests pass. Pay special attention to existing leave validation, leave balance, and leave approval tests. If any tests fail, investigate and fix before proceeding.

- [ ] 13. **Manual end-to-end testing**
      
      Start the backend and frontend applications, configure sandwich leave settings, and test the complete leave application and approval flow.
      
      Test scenarios:
      1. Enable Monday rule, apply for Monday leave → verify 3 days charged
      2. Enable Friday rule, apply for Friday leave → verify 3 days charged
      3. Enable Friday+Monday rule, apply for Friday+Monday leave → verify 4 days charged
      4. Verify leave balance deduction matches the expanded day count
      5. Test manager approval flow with attendance overlap validation
      6. Verify Settings UI saves and loads all three toggle states correctly
      
      Verify: All scenarios work as expected. Document any issues found.

- [ ] 14. **Commit changes to feature branch**
      
      Stage all modified and new files, create a commit with a descriptive message following Conventional Commits format.
      
      Command: 
      ```
      cd c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave
      git add src/main/resources/db/migration/V10__add_sandwich_leave_settings.sql
      git add src/main/java/com/my_hourly/settings/leave/
      git add src/main/java/com/my_hourly/leave/
      git add src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java
      git add ../../HRMS/src/pages/Settings.jsx
      git commit -m "feat: implement configurable sandwich leave policy

Add three independent settings to expand leave requests:
- Monday leave includes preceding weekend (3 days)
- Friday leave includes following weekend (3 days)  
- Friday+Monday leave includes intervening weekend (4 days)

Changes:
- Database migration V10 adds three columns to leave_settings
- Added forced_working_days_json to leave_requests table
- Updated entities, DTOs, mapper, and services
- Implemented expansion logic with rule precedence
- Added attendance overlap validation for sandwich weekends
- Updated frontend Settings UI with new section
- Added comprehensive unit tests

Closes #[issue-number]"
      ```
      
      Verify: Run `git log` and confirm the commit exists. Run `git status` and confirm working directory is clean. Do NOT push yet - that will be done in the finalize step.

---

## Verification Summary

After completing all steps:

1. **Build:** `mvnw.cmd clean compile` succeeds with no errors
2. **Tests:** `mvnw.cmd test` passes all tests including new `LeaveSandwichPolicyTest`
3. **Database:** Migration V10 applies cleanly on a fresh database
4. **Backend API:** GET/PUT `/settings/leave` correctly handles the three new fields
5. **Frontend UI:** Settings page displays Sandwich Leave Policy section with working toggles
6. **Integration:** Leave application with sandwich rules enabled correctly calculates and stores forced working days
7. **Git:** All changes committed to `feature/sandwich-leave` branch

## Notes

- The design document specifies that if `LeavePaidLopServiceImpl` exists in the main repository, it must also be modified to respect `forcedWorkingDays`. Since this service is not present in the worktree, this modification is deferred to a follow-up task if needed.
- The implementation preserves all existing leave management functionality - sandwich leave rules are opt-in and default to disabled.
- The three rules follow strict precedence: Friday+Monday > Friday-only > Monday-only, preventing double-counting.
- Forced working days (sandwich weekends) are counted even if they coincide with public holidays, as specified in the design.
