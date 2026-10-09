# Design Review: Configurable Sandwich Leave Policy

**Reviewer:** Design Review Subagent  
**Date:** 2025-01-27  
**Design Document:** `design.md`

---

## Executive Summary

The design proposes adding three independent sandwich leave settings to the HRMS system. After source code verification and analysis, I have identified **6 HIGH severity findings** and **4 MEDIUM severity findings** that must be addressed before implementation.

**VERDICT: CHANGES_REQUESTED**

The core issues center around:
1. **Critical architectural flaw**: storing expanded dates in LeaveRequest breaks PAID/LOP calculation
2. **Missing signature details** for key methods
3. **Ambiguous algorithm logic** for the no-double-counting mechanism
4. **Unverified assumptions** about service layer integration
5. **Incomplete migration specification**

---

## Verified Assumptions

✅ **Verified by reading source code:**

1. **LeaveValidationServiceImpl.calculateLeaveDays()** does iterate through dates and skip weekends/holidays (lines 137-168 of LeaveValidationServiceImpl.java)
2. **LeaveApplicationContext** currently holds 4 fields: employee, leaveType, leaveBalance, totalDays (verified in context/LeaveApplicationContext.java)
3. **LeaveRequestServiceImpl.applyLeave()** stores startDate and endDate from the request (lines 62-63 of LeaveRequestServiceImpl.java)
4. **LeavePaidLopServiceImpl.classify()** walks working days between startDate and endDate (lines 107-145 of main repo LeavePaidLopServiceImpl.java)
5. **LeavePaidLopServiceImpl** uses the same weekend/holiday logic as LeaveValidationServiceImpl (lines 200-213 of main repo)
6. **LeaveRequestServiceImpl.managerAction()** checks attendance overlap by iterating from startDate to endDate (lines 191-207 of LeaveRequestServiceImpl.java)
7. **LeaveSettings.getActiveLeaveSettings()** method does NOT exist in LeaveSettingsService interface (verified - only getSettings() exists)
8. **Migration versioning** follows Flyway V{number}__ pattern (verified in db/migration/)
9. **DTO @NotNull annotations** are used for required fields (verified in LeaveSettingsRequest.java)
10. **Default values** in migrations use `DEFAULT false` syntax (verified in V6, V9 migrations)

---

## Unverified/Wrong Assumptions

❌ **Not verified or found to be incorrect:**

1. **WRONG**: Design claims to add `getActiveLeaveSettings()` method, but the existing service already has `getSettings()` which returns LeaveSettings entity. The design should use the existing method name.

2. **WRONG**: Design states "The Friday+Monday rule is checked first. If it fires, the Friday-only and Monday-only rules are skipped" but the implementation uses `hasFridayMondaySandwich` flag ONLY in the condition checks for rules 2 and 3. If Friday+Monday fires but doesn't find the pattern, it doesn't set the flag, so Friday-only and Monday-only could still fire. The algorithm logic is ambiguous.

3. **UNVERIFIED**: The design claims "The working-day counter will skip Saturday and Sunday (weekends)" but then says sandwich weekends should be counted. The modified calculateLeaveDays() includes a `forcedWorkingDays` parameter, but the signature is not fully specified (does it need to be public? private? what about the SandwichLeaveExpansion extraction?).

4. **UNVERIFIED**: Design claims LeavePaidLopServiceImpl will "walk the expanded range" but doesn't verify that the service receives startDate/endDate from LeaveRequest entity vs. the original request.

5. **UNVERIFIED**: Design assumes Frontend Settings.jsx has a GROUPS array structure with 'leave' group, but this was not verified by reading the file.

6. **UNVERIFIED**: The migration version number `{next}` is not specified - should be V10 based on the existing V9 migration.

---

## Findings

### HIGH Severity Issues

#### HIGH-1: Critical Architectural Flaw - Storing Expanded Dates Breaks PAID/LOP Calculation

**Location:** Section 5 (Backend - Service Layer), "Revised LeaveApplicationContext" and "Revised LeaveRequestServiceImpl.applyLeave()"

**Problem:** The design proposes storing the expanded startDate/endDate (including sandwich weekends) in the LeaveRequest entity. However, this BREAKS the PAID/LOP calculation logic in LeavePaidLopServiceImpl.

**Why it breaks:**
- LeavePaidLopServiceImpl.classify() walks each date from startDate to endDate and applies the `workingDays()` method which **skips weekends** (line 202-213 of LeavePaidLopServiceImpl.java).
- If LeaveRequest stores expandedStart (Saturday) and expandedEnd (Monday), the classifier will walk [Saturday, Sunday, Monday] but then skip Saturday and Sunday as weekends, counting only Monday as 1 working day.
- The design's "forcedWorkingDays" logic exists only in LeaveValidationServiceImpl - it does NOT exist in LeavePaidLopServiceImpl.
- **Result:** A Monday sandwich leave would calculate totalDays=3 in validation, but the PAID/LOP classifier would only see 1 working day, causing a data inconsistency.

**Concrete fix:**
- **DO NOT** store expanded dates in LeaveRequest.
- Keep storing the **original user-selected** startDate/endDate.
- Store the expanded totalDays (which includes sandwich weekends).
- The LeavePaidLopServiceImpl.workingDays() method must be modified to accept a `Set<LocalDate> forcedWorkingDays` parameter and count those dates even if they are weekends.
- Alternatively, store the forcedWorkingDays as a separate JSON column in leave_requests table, and pass it to the PAID/LOP classifier.

**Recommended approach:**
```java
// LeaveRequest entity - DO NOT change startDate/endDate
.startDate(request.getStartDate())  // Original user selection
.endDate(request.getEndDate())      // Original user selection
.totalDays(context.totalDays())     // Expanded count (3 or 4 days)

// Store forced working days separately
private String forcedWorkingDaysJson;  // JSON array of dates
```

---

#### HIGH-2: Missing Method Signature for calculateLeaveDays with forcedWorkingDays

**Location:** Section 5 (Backend - Service Layer), "Modified `calculateLeaveDays()` method"

**Problem:** The design shows a modified `calculateLeaveDays()` that accepts `forcedWorkingDays` parameter:
```java
Integer totalDays = calculateLeaveDays(
        expansion.expandedStart(),
        expansion.expandedEnd(),
        expansion.forcedWorkingDays());
```

But the method signature is not defined. The current signature is:
```java
private Integer calculateLeaveDays(LocalDate startDate, LocalDate endDate)
```

**What's missing:**
1. The new signature with `Set<LocalDate> forcedWorkingDays` parameter is not shown
2. The existing 2-parameter method is still called from other places - does it need an overload?
3. The design doesn't specify whether the old 2-parameter version should delegate to the new 3-parameter version with an empty set

**Concrete fix:**
```java
// New 3-parameter version (private)
private Integer calculateLeaveDays(
        LocalDate startDate,
        LocalDate endDate,
        Set<LocalDate> forcedWorkingDays) {
    // Implementation as shown in design
}

// Keep old 2-parameter version for backward compatibility (if needed elsewhere)
private Integer calculateLeaveDays(
        LocalDate startDate,
        LocalDate endDate) {
    return calculateLeaveDays(startDate, endDate, Set.of());
}
```

---

#### HIGH-3: Ambiguous Double-Counting Prevention Logic

**Location:** Section 5 (Backend - Service Layer), `expandForSandwichLeave()` algorithm

**Problem:** The design states "If Friday+Monday rule fires, set `hasFridayMondaySandwich = true` to prevent rules 2 and 3 from firing." However, the code shows:

```java
if (settings.getSandwichLeaveFridayMondayEnabled()) {
    for (LocalDate date : requestDates) {
        if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
            LocalDate nextMonday = date.plusDays(3);
            if (requestDates.contains(nextMonday)) {
                // Force Sat+Sun as working days
                forcedWorkingDays.add(saturday);
                forcedWorkingDays.add(sunday);
                hasFridayMondaySandwich = true;
                break;
            }
        }
    }
}
```

**Ambiguity:**
- If `sandwichLeaveFridayMondayEnabled = true` but the loop doesn't find a Friday+Monday pair, `hasFridayMondaySandwich` remains false
- Then rules 2 and 3 will still fire
- **Question:** Is this intended? Should the flag be set when the RULE is enabled, or when the PATTERN is detected?

**Current understanding:** The flag is set when the PATTERN is detected (Friday + Monday both in range). This is correct.

**But there's another issue:** What if the user selects Friday, Saturday, Sunday, Monday, Tuesday? The algorithm will:
1. Detect Friday+Monday pattern → add Sat+Sun to forcedWorkingDays
2. Skip Friday-only rule (correct)
3. Skip Monday-only rule (correct)
4. Result: count 5 working days (Fri, Sat, Sun, Mon, Tue) ✓ Correct

**But what if the user selects ONLY Friday and Saturday (not Sunday or Monday)?**
- Rule 1: No Friday+Monday pattern → skip
- Rule 2: Friday detected → extend to Sunday, add Sat+Sun to forcedWorkingDays
- Rule 3: No Monday → skip
- **Problem:** The user selected Friday+Saturday (2 consecutive days). Rule 2 forces Sat+Sun as working days. But Saturday was ALREADY in the original request. This could cause confusion.

**Concrete fix:**
Add a clarification comment:
```java
// Rule 2: Friday-only sandwich (only if user selected Friday but NOT Monday)
// Note: If user manually includes Saturday in their selection, it will be counted
// once as a forced working day. This is correct - the user selected a weekend day.
```

**Alternative interpretation:** Should Rule 2 only fire if the user selected ONLY Friday (not Sat/Sun)? This needs clarification.

---

#### HIGH-4: LeaveApplicationContext Field Names Don't Match Usage

**Location:** Section 5 (Backend - Service Layer), "Revised `LeaveApplicationContext`"

**Problem:** The design proposes adding two new fields to LeaveApplicationContext:
```java
public record LeaveApplicationContext(
        Employee employee,
        LeaveType leaveType,
        LeaveBalance leaveBalance,
        LocalDate effectiveStartDate,   // NEW
        LocalDate effectiveEndDate,     // NEW
        Integer totalDays
) {}
```

But then in the same section, it shows:
```java
LeaveRequest leaveRequest =
        LeaveRequest.builder()
                .startDate(context.effectiveStartDate())   // Using expanded dates
                .endDate(context.effectiveEndDate())
```

**This contradicts HIGH-1's finding.** If we store expanded dates in LeaveRequest, it breaks PAID/LOP calculation.

**Concrete fix:**
Either:
1. **Option A (recommended):** Do NOT add effectiveStartDate/effectiveEndDate to LeaveApplicationContext. Keep storing original startDate/endDate in LeaveRequest. Store forcedWorkingDays separately.
2. **Option B:** Add effectiveStartDate/effectiveEndDate to context, but also add originalStartDate/originalEndDate, and store BOTH sets in LeaveRequest entity for auditing.

**I recommend Option A** to avoid schema complexity.

---

#### HIGH-5: Missing Service Method Definition

**Location:** Section 5 (Backend - Service Layer), "New dependency injection required"

**Problem:** The design states:
```java
/**
 * Retrieves the active leave settings for the current company.
 * @return active LeaveSettings entity
 * @throws ResourceNotFoundException if no active settings found
 */
LeaveSettings getActiveLeaveSettings();
```

But the existing LeaveSettingsService interface already has:
```java
LeaveSettings getSettings();
```

**Verified by reading source:** LeaveSettingsService interface does NOT have `getActiveLeaveSettings()`. It has `getSettings()` (which likely returns the active settings based on the BaseSettings.active field).

**Ambiguity:**
- Should the design use the existing `getSettings()` method?
- Or add a new `getActiveLeaveSettings()` method that is explicitly named?
- Does `getSettings()` already filter by active=true?

**Concrete fix:**
1. **Read the LeaveSettingsServiceImpl** to verify what `getSettings()` does
2. If it returns active settings, use `leaveSettingsService.getSettings()` in the sandwich leave logic
3. If it returns all settings, add a new method:
```java
// In LeaveSettingsService interface
LeaveSettings getActiveSettings();

// In LeaveSettingsServiceImpl
@Override
public LeaveSettings getActiveSettings() {
    return leaveSettingsRepository.findByActiveTrue()
        .orElseThrow(() -> new ResourceNotFoundException(...));
}
```

**Decision needed:** Use existing `getSettings()` or add new `getActiveSettings()`. The design must specify which approach and provide the implementation if a new method is needed.

---

#### HIGH-6: Migration Version Number Not Specified

**Location:** Section 1 (Database Schema), migration file name

**Problem:** The design shows:
```sql
-- Migration file: src/main/resources/db/migration/V{next}_add_sandwich_leave_settings.sql
```

`{next}` is a placeholder. Flyway requires an actual version number.

**Verified:** The latest migration in the worktree is V9__leave_request_paid_lop_and_approver.sql (from main repo).

**Concrete fix:**
```sql
-- Migration file: src/main/resources/db/migration/V10__add_sandwich_leave_settings.sql
```

---

### MEDIUM Severity Issues

#### MEDIUM-1: Incomplete Test Case Specification

**Location:** Section "Test Strategy", test cases 6 and 7

**Problem:** 
- Test case 6 states "Public holiday on Saturday" but doesn't specify HOW to set up this scenario (holidays are stored in the holidays table, but the test needs to create a holiday record)
- Test case 7 states "crosses into November" but doesn't specify the exact dates to test

**Concrete fix:**
```java
// Test case 6: Public holiday on Saturday
@Test
void mondayLeave_withSaturdayPublicHoliday_countsAllThreeDays() {
    // Setup: Create a public holiday on Saturday Oct 28, 2023
    Holiday saturdayHoliday = Holiday.builder()
        .holidayDate(LocalDate.of(2023, 10, 28))  // Saturday
        .name("Test Holiday")
        .build();
    holidayRepository.save(saturdayHoliday);
    
    // Apply for Monday Oct 30, 2023
    // Expected: 3 days (Sat-holiday, Sun, Mon)
    // Sandwich weekends are counted regardless of public holidays
}

// Test case 7: Month boundary
@Test
void fridayLeave_crossingMonthBoundary_expandsIntoNextMonth() {
    // Friday Oct 31, 2023 (last day of October)
    // Sandwich rule expands to Sat Nov 1, Sun Nov 2
    LocalDate friday = LocalDate.of(2023, 10, 31);
    // Expected: totalDays = 3, spanning Oct and Nov
}
```

---

#### MEDIUM-2: Missing Error Handling for Settings Retrieval

**Location:** Section 5 (Backend - Service Layer), `expandForSandwichLeave()` method

**Problem:** The code calls:
```java
LeaveSettings settings = leaveSettingsService.getActiveLeaveSettings();
```

But what if no active settings exist? The design mentions throwing ResourceNotFoundException, but doesn't specify how the sandwich leave logic should behave in this case.

**Questions:**
1. Should sandwich leave expansion fail if settings are missing?
2. Should it default to all rules disabled?
3. Should it propagate the exception up to the user?

**Concrete fix:**
```java
private SandwichLeaveExpansion expandForSandwichLeave(LocalDate startDate, LocalDate endDate) {
    
    LeaveSettings settings;
    try {
        settings = leaveSettingsService.getSettings();
    } catch (ResourceNotFoundException e) {
        // If settings are missing, default to no sandwich leave expansion
        log.warn("Leave settings not found, sandwich leave rules disabled");
        return new SandwichLeaveExpansion(startDate, endDate, Set.of());
    }
    
    // Rest of the logic...
}
```

**Alternative:** Document that LeaveSettings MUST exist before any leave can be applied (prerequisite).

---

#### MEDIUM-3: Frontend Integration Not Verified

**Location:** Section 6 (Frontend - Settings UI)

**Problem:** The design specifies exact JavaScript code to add to Settings.jsx:
```javascript
{
  title: 'Sandwich Leave Policy',
  subtitle: '...',
  icon: CalendarDays,
  fields: [...]
}
```

But the design document states: "This was not verified by reading the file."

**Risk:** The GROUPS array structure, field naming conventions, or icon imports might be different from what the design assumes.

**Concrete fix:**
Add a note in the implementation checklist:
```markdown
- [ ] **BEFORE implementing frontend**: Read HRMS/src/pages/Settings.jsx and verify:
  - GROUPS array structure exists
  - 'leave' group exists
  - Field type 'boolean' renders as a toggle switch
  - Icon 'CalendarDays' is imported or available
```

**Fallback:** If Settings.jsx structure differs, the implementer must adapt the code to match the existing pattern.

---

#### MEDIUM-4: Missing Validation for Conflicting Settings

**Location:** Section 6 (Frontend - Settings UI) and Section 3 (Backend - DTO Layer)

**Problem:** The three sandwich leave settings can be enabled independently, but there's no validation to prevent illogical combinations.

**Example illogical states:**
- All three rules enabled simultaneously (technically allowed, but confusing - the Friday+Monday rule will always take precedence)
- No rules enabled (this is valid - no sandwich leave)

**Question:** Should the backend validate that at least one rule is enabled? Or is "all disabled" a valid state?

**Current design:** All three disabled is valid (system behaves as before).

**Potential issue:** Admin enables all three rules thinking they're additive, but doesn't understand the precedence order.

**Concrete fix:**
Add a UI hint:
```javascript
{
  name: 'sandwichLeaveFridayMondayEnabled',
  label: 'Friday+Monday Leave (with weekend between)',
  type: 'boolean',
  hint: 'Taking leave on both Friday and Monday charges Saturday and Sunday (4 days total). Note: This rule takes precedence over the individual Friday and Monday rules when both days are in the leave request.'
}
```

**Alternative:** Add backend validation:
```java
// In LeaveSettingsServiceImpl.updateLeaveSettings()
if (request.getSandwichLeaveFridayMondayEnabled() 
    && (request.getSandwichLeaveFridayEnabled() || request.getSandwichLeaveMondayEnabled())) {
    log.warn("Friday+Monday rule enabled along with individual rules - Friday+Monday will take precedence");
    // Not an error, just a warning
}
```

**Decision:** This is a UX concern, not a data integrity issue. Mark as MEDIUM severity. The implementation should include the UI hint.

---

### NIT Issues

#### NIT-1: Inconsistent Method Naming

**Location:** Section 5, `SandwichLeaveExpansion` record

**Problem:** The inner record uses `expandedStart()` and `expandedEnd()` (past tense), but `forcedWorkingDays()` (present tense).

**Suggestion:** Use consistent naming:
```java
private record SandwichLeaveExpansion(
    LocalDate expandedStart,
    LocalDate expandedEnd,
    Set<LocalDate> forcedWorkingDays  // or expandedWorkingDays
) {}
```

---

#### NIT-2: Comment Clarity in Migration

**Location:** Section "Database Migration", COMMENT ON COLUMN

**Problem:** The comment says "totaling 3 chargeable days" but the system still only counts working days. This could confuse DBAs.

**Suggestion:**
```sql
COMMENT ON COLUMN leave_settings.sandwich_leave_monday_enabled IS 
  'When enabled, taking leave on Monday forces the preceding Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days.';
```

---

#### NIT-3: Missing Import Statement for DayOfWeek

**Location:** Section 5 (Backend - Service Layer), `expandForSandwichLeave()` method

**Problem:** The code uses `DayOfWeek.FRIDAY` and `DayOfWeek.MONDAY` but doesn't show the import.

**Suggestion:** Add to implementation checklist:
```java
import java.time.DayOfWeek;
```

(This is already imported in the existing class, so not a real issue - just a documentation nit.)

---

#### NIT-4: Risk Assessment Mentions "User Guide" But No User Guide Is Specified

**Location:** Section "Risk Assessment and Mitigation", Risk 2

**Problem:** The mitigation states "Document this behavior in the user guide" but the design doesn't include a user guide or specify where to document it.

**Suggestion:** Add to implementation checklist:
```markdown
- [ ] **Documentation:** Add sandwich leave policy explanation to:
  - User-facing help text in the Settings UI
  - Admin documentation (if exists)
  - API documentation (Swagger/OpenAPI)
```

---

## Summary of Required Changes

Before implementation can proceed, the design must:

1. **[HIGH-1]** Revise the approach to storing expanded dates - DO NOT store expanded startDate/endDate in LeaveRequest, keep original dates and handle forcedWorkingDays separately
2. **[HIGH-2]** Specify the complete method signature for `calculateLeaveDays(startDate, endDate, forcedWorkingDays)`
3. **[HIGH-3]** Clarify the double-counting prevention logic and document edge cases (e.g., user manually selects weekend days)
4. **[HIGH-4]** Remove effectiveStartDate/effectiveEndDate from LeaveApplicationContext (follows from HIGH-1)
5. **[HIGH-5]** Clarify whether to use existing `getSettings()` or create new `getActiveSettings()` method, and provide implementation
6. **[HIGH-6]** Specify migration version as V10
7. **[MEDIUM-1]** Complete test case specifications with exact dates and setup code
8. **[MEDIUM-2]** Add error handling for missing LeaveSettings
9. **[MEDIUM-3]** Add verification step for frontend Settings.jsx structure before implementation
10. **[MEDIUM-4]** Add UI hints about rule precedence

---

## Correctness Assessment

### No-Double-Counting Algorithm
The algorithm structure (Rule 1 → Rule 2 → Rule 3 with flag) is **fundamentally sound**, but the implementation details in HIGH-3 need clarification for edge cases.

### Integration Completeness
**INCOMPLETE** - The design does not correctly integrate with:
- LeavePaidLopServiceImpl.classify() (HIGH-1 issue)
- The design assumes expanded dates can be stored in LeaveRequest, but this breaks the PAID/LOP walking logic

### DB Migration Safety
**CORRECT** - The DEFAULT false approach is safe and matches existing patterns.

### API Contract Backward Compatibility
**CORRECT** - Adding new boolean fields with @NotNull in DTOs is backward-compatible (clients must provide values, but existing APIs don't break).

### Test Coverage
**INCOMPLETE** - Test case descriptions are high-level. Test cases 6 and 7 need concrete implementations (MEDIUM-1).

---

## Recommendation

**DO NOT PROCEED with implementation** until the 6 HIGH severity issues are resolved. The core architectural issue (HIGH-1) invalidates large portions of the design's Service Layer modifications.

The design author should:
1. Revise the Service Layer approach to avoid storing expanded dates in LeaveRequest
2. Determine how to pass forcedWorkingDays to LeavePaidLopServiceImpl (either modify its interface or store forced days in a separate column)
3. Clarify all method signatures and error handling
4. Re-submit for review

Once HIGH issues are fixed, the MEDIUM and NIT issues can be addressed during implementation or in a follow-up review.

---

## Verification Checklist

- [x] Read LeaveValidationServiceImpl source
- [x] Read LeaveRequestServiceImpl source  
- [x] Read LeaveApplicationContext source
- [x] Read LeavePaidLopServiceImpl source (main repo)
- [x] Read LeaveSettings entity and service interface
- [x] Verified migration versioning pattern
- [x] Verified DTO annotation patterns
- [x] Checked attendance overlap logic in managerAction()
- [ ] Did NOT read Settings.jsx (frontend) - marked as MEDIUM-3
- [x] Verified weekend detection logic (isWeekend method)

