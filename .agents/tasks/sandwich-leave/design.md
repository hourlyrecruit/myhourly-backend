# Technical Design: Configurable Sandwich Leave Policy

## Overview

This design adds three independently configurable sandwich leave policies to the existing HRMS Leave Management module. When enabled, these settings automatically expand leave requests taken on specific days of the week (Friday, Monday, or both) to include the intervening/adjacent weekend days, charging employees for those weekend days as part of their leave.

The feature integrates with the existing working-day calculation in `LeaveValidationServiceImpl.calculateLeaveDays()` and preserves all existing leave balance, PAID/LOP split, monthly guideline, and expiry logic.

**Note on Graphify MCP:** The task instructions require querying Graphify MCP for component dependencies before proceeding. Graphify MCP was not available in the current environment. This design proceeds based on direct source code analysis of the worktree at `c:\Users\User\Desktop\Office Projects\my_hourly\.worktrees\sandwich-leave\`.

## Technology Stack

- **Backend:** Java 17, Spring Boot 3.x, JPA/Hibernate
- **Database:** PostgreSQL (inferred from project structure)
- **Frontend:** React (JavaScript), using existing settings UI patterns
- **Build:** Maven
- **Testing:** JUnit 5, Mockito

## Affected Components

### 1. Database Schema

**Table:** `leave_settings`

**New columns to add:**

```sql
ALTER TABLE leave_settings 
ADD COLUMN sandwich_leave_monday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_monday_enabled BOOLEAN NOT NULL DEFAULT false;
```

**Table:** `leave_requests`

**New column to add:**

```sql
ALTER TABLE leave_requests
ADD COLUMN forced_working_days_json TEXT;
```

This column stores a JSON array of dates that should be counted as working days even if they fall on weekends (for sandwich leave weekends). Example: `["2023-10-28", "2023-10-29"]` for Saturday and Sunday.

**Migration file:** `src/main/resources/db/migration/V10__add_sandwich_leave_settings.sql`

### 2. Backend - Entity Layer

**File:** `src/main/java/com/my_hourly/settings/leave/entity/LeaveSettings.java`

Add three new boolean fields:

```java
/**
 * When enabled, taking leave on Monday counts the preceding Saturday and Sunday,
 * totaling 3 chargeable days.
 */
@Column(name = "sandwich_leave_monday_enabled", nullable = false)
@Builder.Default
private Boolean sandwichLeaveMondayEnabled = false;

/**
 * When enabled, taking leave on Friday counts the following Saturday and Sunday,
 * totaling 3 chargeable days.
 */
@Column(name = "sandwich_leave_friday_enabled", nullable = false)
@Builder.Default
private Boolean sandwichLeaveFridayEnabled = false;

/**
 * When enabled, taking leave on both Friday and Monday counts the intervening
 * Saturday and Sunday, totaling 4 chargeable days.
 */
@Column(name = "sandwich_leave_friday_monday_enabled", nullable = false)
@Builder.Default
private Boolean sandwichLeaveFridayMondayEnabled = false;
```

**File:** `src/main/java/com/my_hourly/leave/entity/LeaveRequest.java`

Add new field to store forced working days (sandwich leave weekends):

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

/**
 * JSON array of dates that should be counted as working days even if they fall on weekends.
 * Used for sandwich leave policy enforcement.
 */
@Column(name = "forced_working_days_json", columnDefinition = "TEXT")
private String forcedWorkingDaysJson;

/**
 * Transient field for in-memory use - deserialized from forcedWorkingDaysJson.
 */
@Transient
private Set<LocalDate> forcedWorkingDays;

// Helper methods to serialize/deserialize forcedWorkingDays
@PostLoad
@PostPersist
@PostUpdate
private void deserializeForcedWorkingDays() {
    if (forcedWorkingDaysJson != null && !forcedWorkingDaysJson.isEmpty()) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Register JavaTimeModule for LocalDate
            this.forcedWorkingDays = mapper.readValue(
                forcedWorkingDaysJson, 
                new TypeReference<Set<LocalDate>>() {}
            );
        } catch (Exception e) {
            this.forcedWorkingDays = new HashSet<>();
        }
    } else {
        this.forcedWorkingDays = new HashSet<>();
    }
}

@PrePersist
@PreUpdate
private void serializeForcedWorkingDays() {
    if (forcedWorkingDays != null && !forcedWorkingDays.isEmpty()) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            this.forcedWorkingDaysJson = mapper.writeValueAsString(forcedWorkingDays);
        } catch (Exception e) {
            this.forcedWorkingDaysJson = null;
        }
    } else {
        this.forcedWorkingDaysJson = null;
    }
}

public void setForcedWorkingDays(Set<LocalDate> forcedWorkingDays) {
    this.forcedWorkingDays = forcedWorkingDays;
}

public Set<LocalDate> getForcedWorkingDays() {
    if (forcedWorkingDays == null) {
        forcedWorkingDays = new HashSet<>();
    }
    return forcedWorkingDays;
}
```

### 3. Backend - DTO Layer

**File:** `src/main/java/com/my_hourly/settings/leave/dto/request/LeaveSettingsRequest.java`

Add three new fields:

```java
@NotNull
private Boolean sandwichLeaveMondayEnabled;

@NotNull
private Boolean sandwichLeaveFridayEnabled;

@NotNull
private Boolean sandwichLeaveFridayMondayEnabled;
```

**File:** `src/main/java/com/my_hourly/settings/leave/dto/response/LeaveSettingsResponse.java`

Add three new fields:

```java
private Boolean sandwichLeaveMondayEnabled;
private Boolean sandwichLeaveFridayEnabled;
private Boolean sandwichLeaveFridayMondayEnabled;
```

### 4. Backend - Mapper Layer

**File:** `src/main/java/com/my_hourly/settings/leave/mapper/LeaveSettingsMapper.java`

**In `toResponse()` method:**

```java
.sandwichLeaveMondayEnabled(entity.getSandwichLeaveMondayEnabled())
.sandwichLeaveFridayEnabled(entity.getSandwichLeaveFridayEnabled())
.sandwichLeaveFridayMondayEnabled(entity.getSandwichLeaveFridayMondayEnabled())
```

**In `updateEntity()` method:**

```java
entity.setSandwichLeaveMondayEnabled(request.getSandwichLeaveMondayEnabled());
entity.setSandwichLeaveFridayEnabled(request.getSandwichLeaveFridayEnabled());
entity.setSandwichLeaveFridayMondayEnabled(request.getSandwichLeaveFridayMondayEnabled());
```

### 5. Backend - Service Layer (Core Business Logic)

**File:** `src/main/java/com/my_hourly/leave/service/impl/LeaveValidationServiceImpl.java`

**Modification point:** The `calculateLeaveDays(LocalDate startDate, LocalDate endDate)` method currently calculates working days by:
1. Loading holidays between startDate and endDate
2. Iterating through each date
3. Skipping weekends (Saturday/Sunday)
4. Skipping holidays
5. Counting the remainder as working days

**New approach:** Before the existing calculation loop, expand the date range if sandwich leave rules apply.

**New private method to add:**

```java
/**
 * Expands leave date range based on sandwich leave settings.
 * Order of evaluation prevents double-counting:
 * 1. Friday+Monday rule (if both Friday AND Monday are in range)
 * 2. Friday-only rule (if Friday in range and Monday rule didn't fire)
 * 3. Monday-only rule (if Monday in range and Friday+Monday rule didn't fire)
 * 
 * @param startDate original start date
 * @param endDate original end date
 * @return expanded date range as [newStart, newEnd], or original if no rules apply
 */
private LocalDate[] expandForSandwichLeave(LocalDate startDate, LocalDate endDate) {
    
    LeaveSettings settings = leaveSettingsService.getActiveLeaveSettings();
    
    LocalDate expandedStart = startDate;
    LocalDate expandedEnd = endDate;
    
    // Collect all dates in the request range (original request only)
    Set<LocalDate> requestDates = new HashSet<>();
    LocalDate current = startDate;
    while (!current.isAfter(endDate)) {
        requestDates.add(current);
        current = current.plusDays(1);
    }
    
    // Check if range contains Friday AND Monday with only Saturday/Sunday between them
    boolean hasFridayMondaySandwich = false;
    if (settings.getSandwichLeaveFridayMondayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate nextMonday = date.plusDays(3);
                if (requestDates.contains(nextMonday)) {
                    // Found a Friday+Monday pair: expand to include Sat+Sun
                    // The Sat+Sun are already between Friday and Monday in the range,
                    // so no expansion needed - they're already included in [startDate, endDate]
                    // Just mark that this rule fired to prevent Friday-only or Monday-only from firing
                    hasFridayMondaySandwich = true;
                    break;
                }
            }
        }
    }
    
    // Friday-only rule: expand END to include following Sat+Sun
    if (!hasFridayMondaySandwich && settings.getSandwichLeaveFridayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate potentialNewEnd = date.plusDays(2); // Sunday after Friday
                if (potentialNewEnd.isAfter(expandedEnd)) {
                    expandedEnd = potentialNewEnd;
                }
                break; // Only extend once for first Friday found
            }
        }
    }
    
    // Monday-only rule: expand START to include preceding Sat+Sun
    if (!hasFridayMondaySandwich && settings.getSandwichLeaveMondayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                LocalDate potentialNewStart = date.minusDays(2); // Saturday before Monday
                if (potentialNewStart.isBefore(expandedStart)) {
                    expandedStart = potentialNewStart;
                }
                break; // Only extend once for first Monday found
            }
        }
    }
    
    return new LocalDate[] { expandedStart, expandedEnd };
}
```

**Modified `calculateLeaveDays()` method:**

```java
private Integer calculateLeaveDays(
        LocalDate startDate,
        LocalDate endDate) {

    // SANDWICH LEAVE EXPANSION
    LocalDate[] expanded = expandForSandwichLeave(startDate, endDate);
    LocalDate effectiveStart = expanded[0];
    LocalDate effectiveEnd = expanded[1];

    Set<LocalDate> holidayDates =
            holidayRepository
                    .findByHolidayDateBetween(
                            effectiveStart,
                            effectiveEnd)
                    .stream()
                    .map(Holiday::getHolidayDate)
                    .collect(Collectors.toSet());

    int totalDays = 0;

    LocalDate current = effectiveStart;

    while (!current.isAfter(effectiveEnd)) {

        if (isWeekend(current)) {
            current = current.plusDays(1);
            continue;
        }

        if (holidayDates.contains(current)) {
            current = current.plusDays(1);
            continue;
        }

        totalDays++;

        current = current.plusDays(1);
    }

    if (totalDays == 0) {
        throw new BadRequestException(
                "No working days found between selected dates.", 
                ErrorCode.RESOURCE_NOT_FOUND);
    }

    return totalDays;
}
```

**New dependency injection required:**

Add to `LeaveValidationServiceImpl` constructor:

```java
private final LeaveSettingsService leaveSettingsService;
```

**Service interface method to add to `LeaveSettingsService`:**

```java
/**
 * Retrieves the active leave settings for the current company.
 * @return active LeaveSettings entity
 * @throws ResourceNotFoundException if no active settings found
 */
LeaveSettings getActiveLeaveSettings();
```

**Implementation in `LeaveSettingsServiceImpl`:**

```java
@Override
@Transactional(readOnly = true)
public LeaveSettings getActiveLeaveSettings() {
    return leaveSettingsRepository.findByActiveTrue()
            .orElseThrow(() -> new ResourceNotFoundException(
                    "Active leave settings not found.",
                    ErrorCode.RESOURCE_NOT_FOUND
            ));
}
```

### 6. Frontend - Settings UI

**File:** `HRMS/src/pages/Settings.jsx`

**In the GROUPS array, under the 'leave' group, modify the `sections` array:**

Add a new section after 'Leave Rules':

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

**In the GROUPS array, under the 'leave' group, add to the `fields` array:**

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
  hint: 'Taking leave on both Friday and Monday charges Saturday and Sunday (4 days total).'
}
```

## Algorithm Details

### Sandwich Leave Expansion Logic

The expansion algorithm runs **before** the working-day counting loop. It operates on the original user-selected date range `[startDate, endDate]` and produces an expanded range `[effectiveStart, effectiveEnd]` that is then fed into the existing working-day calculation.

**Three rules, evaluated in order:**

1. **Friday+Monday Rule (highest priority):**
   - **Condition:** Both `sandwichLeaveFridayMondayEnabled = true` AND the request range contains a Friday with the following Monday also in the range (only Saturday and Sunday between them).
   - **Action:** No range expansion needed—the Saturday and Sunday are already between Friday and Monday in the original range. The working-day counter will skip them as weekends, so the total working days = (Friday=1) + (Monday=1) = 2, but the **user perception** is that they are charged for 4 consecutive days (Fri, Sat, Sun, Mon).
   - **Side effect:** Set a flag `hasFridayMondaySandwich = true` to prevent rules 2 and 3 from firing.

2. **Friday-only Rule:**
   - **Condition:** `sandwichLeaveFridayEnabled = true` AND `hasFridayMondaySandwich = false` AND the request range contains a Friday.
   - **Action:** Extend `effectiveEnd` to include the following Sunday (Friday + 2 days).
   - **Result:** The working-day counter will count Friday (1 working day) and skip Saturday/Sunday, but the user is charged for Friday only. The expansion ensures that if the user picks Friday alone, they are "charged" for the weekend by making the date range span into it.

3. **Monday-only Rule:**
   - **Condition:** `sandwichLeaveMondayEnabled = true` AND `hasFridayMondaySandwich = false` AND the request range contains a Monday.
   - **Action:** Extend `effectiveStart` to include the preceding Saturday (Monday - 2 days).
   - **Result:** The working-day counter will count Monday (1 working day) and skip Saturday/Sunday, but the expansion visually/logically ties the weekend to the Monday leave.

**Double-counting prevention:**

- The Friday+Monday rule is checked first. If it fires, the Friday-only and Monday-only rules are skipped.
- If a user applies for Friday, Saturday, Sunday, and Monday as a 4-day continuous leave, the algorithm detects the Friday+Monday pair and treats it as one unit. The Friday-only and Monday-only rules do NOT additionally expand the range.

**Edge case: User manually includes the weekend**

If the user manually selects Friday, Saturday, Sunday, and Monday in their leave request, the current system's `calculateLeaveDays()` will skip Saturday and Sunday (weekends) and count only Friday and Monday as 2 working days. The sandwich leave logic does not change this behavior—it only expands the range when the user picks specific weekdays (Friday or Monday), ensuring weekends are "charged" as part of the user's intent.

**Integration with PAID/LOP split:**

The expanded date range is passed to `calculateLeaveDays()`, which returns a `totalDays` count. This count is stored in `LeaveRequest.totalDays` and is later used by the PAID/LOP classifier (which the main repository's `LeavePaidLopServiceImpl` handles—not present in the worktree but referenced in tests). The classifier walks each working day in the expanded range and applies the monthly guideline and balance deduction. Since sandwich leave expansion happens at the validation stage, the PAID/LOP split operates on the expanded working-day list and behaves correctly without modification.

## Database Migration

**File:** `src/main/resources/db/migration/V{next}_add_sandwich_leave_settings.sql`

```sql
-- Add sandwich leave policy settings to leave_settings table
-- These settings control whether weekends are automatically charged
-- when leave is taken on specific days of the week.

ALTER TABLE leave_settings 
ADD COLUMN sandwich_leave_monday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_monday_enabled BOOLEAN NOT NULL DEFAULT false;

-- Add comments for documentation
COMMENT ON COLUMN leave_settings.sandwich_leave_monday_enabled IS 
  'When enabled, taking leave on Monday counts the preceding Saturday and Sunday, totaling 3 chargeable days.';

COMMENT ON COLUMN leave_settings.sandwich_leave_friday_enabled IS 
  'When enabled, taking leave on Friday counts the following Saturday and Sunday, totaling 3 chargeable days.';

COMMENT ON COLUMN leave_settings.sandwich_leave_friday_monday_enabled IS 
  'When enabled, taking leave on both Friday and Monday counts the intervening Saturday and Sunday, totaling 4 chargeable days.';
```

## Test Strategy

### Unit Tests

**File:** `src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java` (new file)

Test cases:

1. **No rules enabled:** Verify that leave calculation works exactly as before (weekends not charged).

2. **Monday rule only:**
   - Apply for Monday → expect 3 working days (Sat, Sun, Mon all skipped as weekends, but user charged for Monday + weekend conceptually).
   - Actually, the **correct expectation** is: the range expands to [Saturday, Monday], and the working-day counter skips Saturday and Sunday (weekends), counting only Monday = 1 working day. The "3 chargeable days" means the **user perception** that they're charged for the weekend, but the system still only counts working days.
   - **Correction:** The requirement states "totaling 3 chargeable days." This implies that Saturday and Sunday should somehow be counted. Let me re-read the requirement.

**Requirement clarification needed:**

The requirement says:
- "Monday Leave: When enabled, taking leave on Monday counts the preceding Saturday and Sunday, totaling 3 chargeable days."

This could mean:
- **Interpretation A:** The system counts Saturday and Sunday as chargeable days (even though they're weekends), so `totalDays = 3`.
- **Interpretation B:** The system expands the range to include Saturday and Sunday, but still only counts working days, so `totalDays = 1` (Monday). The "3 chargeable days" is a user-facing description, not the actual count.

**Chosen interpretation:** **Interpretation A** is correct. The sandwich leave policy **must count weekend days as chargeable days** when the rules apply. This means the `calculateLeaveDays()` method must **not skip weekends** when they are part of a sandwich leave expansion.

**Revised algorithm:**

The `expandForSandwichLeave()` method should return both the expanded date range AND a set of dates that should be treated as working days even if they fall on weekends.

**Revised design for `LeaveValidationServiceImpl`:**

```java
/**
 * Expands leave date range based on sandwich leave settings and identifies
 * weekend dates that should be counted as chargeable days.
 * 
 * @param startDate original start date
 * @param endDate original end date
 * @return SandwichLeaveExpansion containing expanded range and forced working days
 */
private SandwichLeaveExpansion expandForSandwichLeave(LocalDate startDate, LocalDate endDate) {
    
    LeaveSettings settings = leaveSettingsService.getActiveLeaveSettings();
    
    LocalDate expandedStart = startDate;
    LocalDate expandedEnd = endDate;
    Set<LocalDate> forcedWorkingDays = new HashSet<>();
    
    // Collect all dates in the request range (original request only)
    Set<LocalDate> requestDates = new HashSet<>();
    LocalDate current = startDate;
    while (!current.isAfter(endDate)) {
        requestDates.add(current);
        current = current.plusDays(1);
    }
    
    // Rule 1: Friday+Monday sandwich (highest priority)
    boolean hasFridayMondaySandwich = false;
    if (settings.getSandwichLeaveFridayMondayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate saturday = date.plusDays(1);
                LocalDate sunday = date.plusDays(2);
                LocalDate monday = date.plusDays(3);
                
                if (requestDates.contains(monday)) {
                    // Friday+Monday sandwich detected: force Sat+Sun as working days
                    forcedWorkingDays.add(saturday);
                    forcedWorkingDays.add(sunday);
                    hasFridayMondaySandwich = true;
                    break;
                }
            }
        }
    }
    
    // Rule 2: Friday-only sandwich
    if (!hasFridayMondaySandwich && settings.getSandwichLeaveFridayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate saturday = date.plusDays(1);
                LocalDate sunday = date.plusDays(2);
                
                // Extend range and force Sat+Sun as working days
                if (sunday.isAfter(expandedEnd)) {
                    expandedEnd = sunday;
                }
                forcedWorkingDays.add(saturday);
                forcedWorkingDays.add(sunday);
                break;
            }
        }
    }
    
    // Rule 3: Monday-only sandwich
    if (!hasFridayMondaySandwich && settings.getSandwichLeaveMondayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                LocalDate saturday = date.minusDays(2);
                LocalDate sunday = date.minusDays(1);
                
                // Extend range and force Sat+Sun as working days
                if (saturday.isBefore(expandedStart)) {
                    expandedStart = saturday;
                }
                forcedWorkingDays.add(saturday);
                forcedWorkingDays.add(sunday);
                break;
            }
        }
    }
    
    return new SandwichLeaveExpansion(expandedStart, expandedEnd, forcedWorkingDays);
}

/**
 * Internal DTO for sandwich leave expansion results.
 */
private record SandwichLeaveExpansion(
    LocalDate expandedStart,
    LocalDate expandedEnd,
    Set<LocalDate> forcedWorkingDays
) {}
```

**Modified `calculateLeaveDays()` with forced working days:**

```java
private Integer calculateLeaveDays(
        LocalDate startDate,
        LocalDate endDate) {

    // SANDWICH LEAVE EXPANSION
    SandwichLeaveExpansion expansion = expandForSandwichLeave(startDate, endDate);
    LocalDate effectiveStart = expansion.expandedStart();
    LocalDate effectiveEnd = expansion.expandedEnd();
    Set<LocalDate> forcedWorkingDays = expansion.forcedWorkingDays();

    Set<LocalDate> holidayDates =
            holidayRepository
                    .findByHolidayDateBetween(
                            effectiveStart,
                            effectiveEnd)
                    .stream()
                    .map(Holiday::getHolidayDate)
                    .collect(Collectors.toSet());

    int totalDays = 0;

    LocalDate current = effectiveStart;

    while (!current.isAfter(effectiveEnd)) {

        // Forced working days (sandwich weekends) are always counted
        if (forcedWorkingDays.contains(current)) {
            totalDays++;
            current = current.plusDays(1);
            continue;
        }

        if (isWeekend(current)) {
            current = current.plusDays(1);
            continue;
        }

        if (holidayDates.contains(current)) {
            current = current.plusDays(1);
            continue;
        }

        totalDays++;

        current = current.plusDays(1);
    }

    if (totalDays == 0) {
        throw new BadRequestException(
                "No working days found between selected dates.", 
                ErrorCode.RESOURCE_NOT_FOUND);
    }

    return totalDays;
}
```

### Test Cases

**File:** `src/test/java/com/my_hourly/leave/LeaveSandwichPolicyTest.java`

1. **Baseline - No rules enabled:**
   - Leave: Friday only → 1 working day
   - Leave: Monday only → 1 working day
   - Leave: Friday + Monday → 2 working days
   - Leave: Thursday + Friday → 2 working days

2. **Monday rule enabled:**
   - Leave: Monday only → 3 working days (Sat, Sun, Mon)
   - Leave: Tuesday only → 1 working day (no expansion)
   - Leave: Monday + Tuesday → 4 working days (Sat, Sun, Mon, Tue)
   - Leave: Saturday + Sunday + Monday (manual) → 3 working days (all three counted)

3. **Friday rule enabled:**
   - Leave: Friday only → 3 working days (Fri, Sat, Sun)
   - Leave: Thursday only → 1 working day (no expansion)
   - Leave: Thursday + Friday → 4 working days (Thu, Fri, Sat, Sun)

4. **Friday+Monday rule enabled:**
   - Leave: Friday only → 1 working day (no expansion, Monday not in range)
   - Leave: Monday only → 1 working day (no expansion, Friday not in range)
   - Leave: Friday + Monday → 4 working days (Fri, Sat, Sun, Mon)
   - Leave: Friday + Saturday + Sunday + Monday (manual) → 4 working days (same)

5. **All three rules enabled:**
   - Leave: Friday + Monday → 4 working days (Friday+Monday rule fires, others blocked)
   - Leave: Friday only → 3 working days (Friday rule fires)
   - Leave: Monday only → 3 working days (Monday rule fires)
   - Leave: Thursday + Friday + Monday + Tuesday → 6 working days (Thu, Fri, Sat, Sun, Mon, Tue)

6. **Edge case: Public holiday on Saturday:**
   - Monday rule enabled, leave: Monday only → 3 working days (Sat and Sun are forced as working days by sandwich rule, public holiday check doesn't apply to forced days)
   - **Alternative interpretation:** Forced sandwich days should still respect public holidays.
   - **Chosen approach:** Sandwich weekends are counted regardless of public holidays. The policy intent is to charge for the weekend as part of the sandwich, even if Saturday or Sunday is a declared public holiday.

7. **Edge case: Month boundary:**
   - Friday rule enabled, leave: Last Friday of October → 3 working days, crosses into November (Fri Oct 31, Sat Nov 1, Sun Nov 2)
   - Verify PAID/LOP split correctly attributes days to October and November.

8. **Integration test: Leave balance deduction:**
   - Employee has 5 days remaining balance.
   - Monday rule enabled, apply for Monday → 3 days deducted.
   - Remaining balance → 2 days.

9. **Integration test: Insufficient balance:**
   - Employee has 2 days remaining balance.
   - Monday rule enabled, apply for Monday → should fail validation with "Insufficient leave balance" (requires 3 days).

10. **Settings mapper test:**
    - Save all three sandwich flags as true → retrieve and verify all are true.
    - Update one flag to false → verify only that flag changes.

### Manual Testing Checklist

1. **Settings UI:**
   - Navigate to Settings → Leave → Sandwich Leave Policy section.
   - Toggle each of the three settings on and off.
   - Save and reload the page to confirm persistence.
   - Verify that the toggle states match the backend data.

2. **Leave application flow:**
   - Enable Monday rule, apply for Monday → verify "Total Days: 3" in the UI.
   - Enable Friday rule, apply for Friday → verify "Total Days: 3" in the UI.
   - Enable Friday+Monday rule, apply for Friday + Monday → verify "Total Days: 4" in the UI.
   - Disable all rules, apply for Friday + Monday → verify "Total Days: 2" in the UI.

3. **Leave approval and balance deduction:**
   - Manager approves a sandwich leave request → verify correct number of days deducted from balance.
   - Check leave transaction history → verify correct totalDays recorded.

4. **PAID/LOP split:**
   - Employee with monthly guideline = 2, applies for Monday with Monday rule enabled (3 days) → verify PAID/LOP split if this is the first leave of the month (2 PAID, 1 LOP).
   - If employee has already used 1 PAID day this month, the sandwich leave should allocate 1 PAID, 2 LOP.

## Risk Assessment and Mitigation

### Risk 1: Double-counting in edge cases

**Risk:** If a user manually selects Friday, Saturday, Sunday, and Monday, and all three rules are enabled, there's a risk that multiple rules could fire and count weekends multiple times.

**Mitigation:** The algorithm explicitly checks for Friday+Monday sandwich first and sets a flag to prevent Friday-only and Monday-only rules from firing. The order of evaluation (Friday+Monday → Friday-only → Monday-only) ensures precedence.

**Validation:** Unit test case #5 explicitly tests this scenario.

### Risk 2: Existing leave requests with pending approval

**Scenario:** A leave request was submitted before sandwich leave rules were enabled. It's still in PENDING status. After enabling sandwich leave, the manager approves it.

**Current behavior:** The `totalDays` was calculated at submission time (before sandwich leave rules), so the approval flow will deduct the old `totalDays` value.

**Risk:** The sandwich leave rules will NOT retroactively apply to pending requests.

**Mitigation:** This is the correct behavior. Sandwich leave rules apply at the time of leave application (validation stage), not at approval stage. The `totalDays` is immutable once the request is created. Document this behavior in the user guide.

**Alternative approach (not recommended):** Recalculate `totalDays` at approval time. This would require modifying `LeaveRequestServiceImpl.managerAction()` to re-run validation, which could introduce breaking changes and inconsistencies.

### Risk 3: Integration with LeavePaidLopServiceImpl

**Issue:** The `LeavePaidLopServiceImpl` (from main repository, not in worktree) walks working days between `leaveRequest.getStartDate()` and `leaveRequest.getEndDate()` to classify PAID/LOP. If the sandwich leave expansion modifies the `totalDays` but the `startDate`/`endDate` stored in `LeaveRequest` entity remain unchanged, there's a mismatch.

**Analysis:** Reviewing the `LeaveRequestServiceImpl.applyLeave()` method:

```java
LeaveRequest leaveRequest =
        LeaveRequest.builder()
                .employee(employee)
                .leaveType(context.leaveType())
                .startDate(request.getStartDate())  // Original user-selected start
                .endDate(request.getEndDate())      // Original user-selected end
                .totalDays(context.totalDays())      // Calculated total (with sandwich expansion)
                .reason(request.getReason().trim())
                .status(LeaveStatus.PENDING)
                .build();
```

The `startDate` and `endDate` are the **original user-selected dates**, while `totalDays` is the expanded count. This creates a discrepancy.

**Correct approach:** Store the **expanded** `startDate` and `endDate` in the `LeaveRequest` entity, so that the PAID/LOP classifier and all downstream logic operate on the expanded range.

**Revised `LeaveApplicationContext`:**

```java
public record LeaveApplicationContext(
        Employee employee,
        LeaveType leaveType,
        LeaveBalance leaveBalance,
        LocalDate effectiveStartDate,   // Expanded start (for sandwich leave)
        LocalDate effectiveEndDate,     // Expanded end (for sandwich leave)
        Integer totalDays
) {}
```

**Revised `LeaveValidationServiceImpl.validateLeaveApplication()`:**

```java
@Override
public LeaveApplicationContext validateLeaveApplication(
        Employee employee,
        LeaveRequestRequest request) {

    LeaveType leaveType =
            validateLeaveType(request.getLeaveTypeId());

    validateLeaveDates(
            request.getStartDate(),
            request.getEndDate());

    validateLeaveOverlap(
            employee,
            request.getStartDate(),
            request.getEndDate());

    // Calculate leave days with sandwich expansion
    SandwichLeaveExpansion expansion = expandForSandwichLeave(
            request.getStartDate(),
            request.getEndDate());
    
    Integer totalDays = calculateLeaveDays(
            expansion.expandedStart(),
            expansion.expandedEnd(),
            expansion.forcedWorkingDays());

    LeaveBalance leaveBalance =
            validateLeaveBalance(
                    employee,
                    leaveType,
                    totalDays);

    return new LeaveApplicationContext(
            employee,
            leaveType,
            leaveBalance,
            expansion.expandedStart(),   // Store expanded start
            expansion.expandedEnd(),     // Store expanded end
            totalDays);
}
```

**Revised `LeaveRequestServiceImpl.applyLeave()`:**

```java
LeaveRequest leaveRequest =
        LeaveRequest.builder()
                .employee(employee)
                .leaveType(context.leaveType())
                .startDate(context.effectiveStartDate())   // Expanded start
                .endDate(context.effectiveEndDate())       // Expanded end
                .totalDays(context.totalDays())
                .reason(request.getReason().trim())
                .status(LeaveStatus.PENDING)
                .build();
```

**Implication:** The `LeaveRequest` entity now stores the expanded date range. This means:
- The leave calendar UI will show the expanded range (including sandwich weekends).
- The PAID/LOP classifier will walk the expanded range.
- The attendance marking logic will mark the expanded range (including sandwich weekends).

**Edge case:** If Saturday or Sunday is marked as attendance, the sandwich leave approval should fail.

**Mitigation:** The existing attendance overlap check in `LeaveRequestServiceImpl.managerAction()` already handles this:

```java
LocalDate date = leaveRequest.getStartDate();
while (!date.isAfter(leaveRequest.getEndDate())) {
    if (attendanceRepository.existsByEmployeeAndAttendanceDate(
            leaveRequest.getEmployee(),
            date)) {
        throw new ValidationException(
                "Attendance already exists on " + date +
                        ". Leave cannot be approved.",
                ErrorCode.VALIDATION_FAILED
        );
    }
    date = date.plusDays(1);
}
```

Since `leaveRequest.getStartDate()` and `getEndDate()` now contain the expanded range (including sandwich weekends), this check will correctly prevent approval if attendance exists on any sandwich weekend day.

### Risk 4: UI confusion - displayed date range vs selected date range

**Scenario:** User selects Monday in the leave application form. With Monday rule enabled, the backend expands to Saturday-Monday (3 days). The user sees "Total Days: 3" but the date picker still shows only Monday selected.

**Risk:** User may be confused about which dates are actually being charged.

**Mitigation:** Add a UI hint or info message in the leave application form that explains when sandwich leave rules are active. Example:

```
"Note: Sandwich Leave policy is enabled. Taking leave on Monday will automatically charge the preceding Saturday and Sunday."
```

**Alternative:** Modify the frontend leave application form to visually highlight the expanded date range when sandwich leave rules apply. This requires frontend API changes and is out of scope for this design.

**Chosen approach:** Document the behavior in the user guide. The "Total Days" field already indicates the correct charge, and the leave request confirmation should show the expanded date range.

### Risk 5: Performance impact on leave validation

**Impact:** The `expandForSandwichLeave()` method adds a loop through the request date range to detect Friday/Monday patterns. For typical leave requests (1-10 days), this adds negligible overhead.

**Worst case:** A user applies for a full year of leave (365 days). The algorithm loops through 365 dates to check for Friday/Monday patterns.

**Mitigation:** The algorithm is O(n) where n = number of days in the request. For n < 100, performance impact is negligible. For very large requests (n > 100), the overhead is still acceptable (< 1ms).

**Optimization (if needed):** Early-exit on the first Friday or Monday detection, since the rules only apply once per request (not per Friday/Monday occurrence). Already implemented in the design (break statement after detecting the first match).

### Risk 6: Database migration rollback

**Scenario:** The migration is applied in production, then needs to be rolled back due to a critical bug.

**Risk:** Rolling back the migration will drop the three new columns, losing any configuration data that was set.

**Mitigation:** The default value for all three columns is `false`, so rolling back and re-applying the migration restores the system to "sandwich leave disabled" state. Organizations should back up the `leave_settings` table before applying the migration in production.

**Rollback script:**

```sql
ALTER TABLE leave_settings 
DROP COLUMN IF EXISTS sandwich_leave_monday_enabled,
DROP COLUMN IF EXISTS sandwich_leave_friday_enabled,
DROP COLUMN IF EXISTS sandwich_leave_friday_monday_enabled;
```

### Risk 7: Attendance marking for sandwich weekends

**Scenario:** Manager approves a Monday leave with Monday rule enabled. The system marks attendance as "LEAVE" for Saturday, Sunday, and Monday. However, the employee actually worked on Saturday (a weekend day).

**Risk:** The system prevents weekend attendance by default (based on `AttendanceSettings.weekendAttendanceAllowed`). If weekend attendance is disallowed, the employee cannot log attendance for Saturday, so this conflict doesn't arise. If weekend attendance is allowed, the existing attendance overlap check (Risk 3 mitigation) will prevent leave approval if attendance already exists.

**Conclusion:** No additional mitigation needed. The existing safeguards cover this scenario.

## Implementation Checklist

1. **Database migration:**
   - [ ] Create Flyway migration script with the three new columns.
   - [ ] Test migration on a local database.
   - [ ] Verify rollback script.

2. **Backend - Entity & DTOs:**
   - [ ] Add three boolean fields to `LeaveSettings` entity.
   - [ ] Add three fields to `LeaveSettingsRequest` DTO.
   - [ ] Add three fields to `LeaveSettingsResponse` DTO.
   - [ ] Update `LeaveSettingsMapper` for bidirectional mapping.

3. **Backend - Service layer:**
   - [ ] Add `getActiveLeaveSettings()` method to `LeaveSettingsService`.
   - [ ] Implement `getActiveLeaveSettings()` in `LeaveSettingsServiceImpl`.
   - [ ] Add `expandForSandwichLeave()` and `SandwichLeaveExpansion` record to `LeaveValidationServiceImpl`.
   - [ ] Modify `calculateLeaveDays()` to use expansion and forced working days.
   - [ ] Update `LeaveApplicationContext` to include effective start/end dates.
   - [ ] Update `LeaveRequestServiceImpl.applyLeave()` to use effective dates.
   - [ ] Inject `LeaveSettingsService` into `LeaveValidationServiceImpl`.

4. **Frontend - Settings UI:**
   - [ ] Add three new field definitions to the 'leave' group in `Settings.jsx`.
   - [ ] Add a new section "Sandwich Leave Policy" to the 'leave' group sections array.
   - [ ] Test the UI: toggle each setting and verify persistence.

5. **Unit tests:**
   - [ ] Create `LeaveSandwichPolicyTest.java` with all 10 test cases from the test strategy.
   - [ ] Run tests and verify all pass.
   - [ ] Add test cases to existing `LeaveValidationServiceTest` for the modified `calculateLeaveDays()`.

6. **Integration tests:**
   - [ ] Test leave application with sandwich rules enabled.
   - [ ] Test leave approval and balance deduction.
   - [ ] Test PAID/LOP split with sandwich leave.
   - [ ] Test insufficient balance scenario.

7. **Manual testing:**
   - [ ] Follow the manual testing checklist above.
   - [ ] Test with different combinations of rule enablement.
   - [ ] Verify leave calendar displays expanded ranges correctly.

8. **Documentation:**
   - [ ] Update user guide with sandwich leave policy explanation.
   - [ ] Add API documentation for the new leave settings fields.
   - [ ] Document the precedence of the three rules (Friday+Monday > Friday-only > Monday-only).

## Summary

This design introduces three independently configurable sandwich leave policies that integrate seamlessly with the existing leave management system. The core logic is implemented in `LeaveValidationServiceImpl.expandForSandwichLeave()`, which runs before the working-day calculation and identifies weekend dates that should be counted as chargeable days. The expanded date range is stored in the `LeaveRequest` entity, ensuring all downstream logic (PAID/LOP split, attendance marking, balance deduction) operates on the correct range.

The design preserves all existing functionality, prevents double-counting through explicit rule precedence, and includes comprehensive test coverage for all scenarios.
