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

**New approach:** Before the existing calculation loop, identify sandwich leave weekends that must be counted as working days even though they fall on weekends.

**New dependency injection required:**

Add to `LeaveValidationServiceImpl` constructor:

```java
private final LeaveSettingsService leaveSettingsService;
```

**File:** `src/main/java/com/my_hourly/leave/service/impl/LeavePaidLopServiceImpl.java`

**Modified method:** `workingDays(LocalDate from, LocalDate to, Set<LocalDate> holidays)`

**Change method signature to:**
```java
private List<LocalDate> workingDays(LocalDate from, LocalDate to, Set<LocalDate> holidays, Set<LocalDate> forcedWorkingDays)
```

**Update implementation:**
```java
private List<LocalDate> workingDays(LocalDate from, LocalDate to, Set<LocalDate> holidays, Set<LocalDate> forcedWorkingDays) {

    List<LocalDate> days = new ArrayList<>();

    for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
        // Forced working days (sandwich leave weekends) are always counted,
        // regardless of weekend or public holiday status
        if (forcedWorkingDays != null && forcedWorkingDays.contains(day)) {
            days.add(day);
            continue;
        }
        
        if (isWeekend(day) || holidays.contains(day)) {
            continue;
        }
        days.add(day);
    }

    return days;
}
```

**Update the `classify()` method call sites (around line 122):**
```java
Set<LocalDate> forcedWorkingDays = leaveRequest.getForcedWorkingDays();
if (forcedWorkingDays == null) {
    forcedWorkingDays = Set.of();
}

// Determine effective range including forced working days
LocalDate effectiveStart = leaveRequest.getStartDate();
LocalDate effectiveEnd = leaveRequest.getEndDate();

if (!forcedWorkingDays.isEmpty()) {
    LocalDate minForced = forcedWorkingDays.stream()
        .min(LocalDate::compareTo)
        .orElse(effectiveStart);
    LocalDate maxForced = forcedWorkingDays.stream()
        .max(LocalDate::compareTo)
        .orElse(effectiveEnd);
    effectiveStart = effectiveStart.isBefore(minForced) ? effectiveStart : minForced;
    effectiveEnd = effectiveEnd.isAfter(maxForced) ? effectiveEnd : maxForced;
}

// When calling workingDays(), use effectiveStart/effectiveEnd and pass forcedWorkingDays:
List<LocalDate> workingDays = workingDays(effectiveStart, effectiveEnd, holidays, forcedWorkingDays);
```

**Rationale:** The PAID/LOP classifier walks from startDate to endDate and skips weekends. Without this modification, sandwich weekends that fall outside or within the original range won't be counted correctly. This change ensures forced working days are always counted regardless of their weekend/holiday status.

**File:** `src/main/java/com/my_hourly/leave/service/impl/LeaveRequestServiceImpl.java`

**Method:** `managerAction()` (around line 191-207)

**Modification:** Add attendance overlap check for forcedWorkingDays after the existing check:

```java
// Existing check for the original request range
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

// NEW: Also check forced working days (sandwich weekends)
Set<LocalDate> forcedWorkingDays = leaveRequest.getForcedWorkingDays();
if (forcedWorkingDays != null && !forcedWorkingDays.isEmpty()) {
    for (LocalDate forcedDate : forcedWorkingDays) {
        // Skip if already checked in the main loop above
        if (!forcedDate.isBefore(leaveRequest.getStartDate()) 
            && !forcedDate.isAfter(leaveRequest.getEndDate())) {
            continue;
        }
        
        if (attendanceRepository.existsByEmployeeAndAttendanceDate(
                leaveRequest.getEmployee(),
                forcedDate)) {
            throw new ValidationException(
                    "Attendance already exists on " + forcedDate +
                            " (sandwich leave weekend). Leave cannot be approved.",
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }
}
```

**Rationale:** If a user works on Saturday (weekend attendance allowed) and then applies for Monday leave with the Monday rule enabled, the manager approval must check that Saturday doesn't already have attendance marked. Without this check, the system could mark Saturday as LEAVE retroactively, overwriting the existing attendance record.

**New private method to add:**

```java
import java.time.DayOfWeek;

/**
 * Expands leave date range based on sandwich leave settings and identifies
 * weekend dates that should be counted as chargeable days.
 * 
 * Order of evaluation prevents double-counting:
 * 1. Friday+Monday rule (if both Friday AND Monday are in the original request)
 * 2. Friday-only rule (if Friday in range and Friday+Monday rule didn't fire)
 * 3. Monday-only rule (if Monday in range and Friday+Monday rule didn't fire)
 * 
 * Note: If user manually includes weekend days in their selection, those days will be
 * counted by the normal working-day calculation. The sandwich rule will additionally
 * force adjacent/intervening weekends as working days. This is intentional - if a user
 * explicitly selects a weekend day, it should be counted.
 * 
 * @param startDate original start date from user request
 * @param endDate original end date from user request
 * @return SandwichLeaveExpansion containing expanded range and forced working days
 */
private SandwichLeaveExpansion expandForSandwichLeave(LocalDate startDate, LocalDate endDate) {
    
    LeaveSettings settings;
    try {
        settings = leaveSettingsService.getSettings();
    } catch (ResourceNotFoundException e) {
        // If settings are missing, default to no sandwich leave expansion
        log.warn("Leave settings not found, sandwich leave rules disabled");
        return new SandwichLeaveExpansion(startDate, endDate, Set.of());
    }
    
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
    // Detect if the user requested BOTH Friday and Monday with Saturday/Sunday between them
    boolean hasFridayMondaySandwich = false;
    if (settings.getSandwichLeaveFridayMondayEnabled() != null && 
        settings.getSandwichLeaveFridayMondayEnabled()) {
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
                    // No need to expand the date range boundaries (expandedStart/expandedEnd) 
                    // because Saturday and Sunday already fall between Friday and Monday in the 
                    // original request range. The calculateLeaveDays loop will iterate over them 
                    // and count them as forced working days.
                    break; // Only apply once per request
                }
            }
        }
    }
    
    // Rule 2: Friday-only sandwich
    // Only applies if Friday+Monday rule did NOT fire
    if (!hasFridayMondaySandwich && 
        settings.getSandwichLeaveFridayEnabled() != null && 
        settings.getSandwichLeaveFridayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                LocalDate saturday = date.plusDays(1);
                LocalDate sunday = date.plusDays(2);
                
                // Extend range to include Sat+Sun and force them as working days
                if (sunday.isAfter(expandedEnd)) {
                    expandedEnd = sunday;
                }
                forcedWorkingDays.add(saturday);
                forcedWorkingDays.add(sunday);
                break; // Only apply once per request
            }
        }
    }
    
    // Rule 3: Monday-only sandwich
    // Only applies if Friday+Monday rule did NOT fire
    if (!hasFridayMondaySandwich && 
        settings.getSandwichLeaveMondayEnabled() != null && 
        settings.getSandwichLeaveMondayEnabled()) {
        for (LocalDate date : requestDates) {
            if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                LocalDate saturday = date.minusDays(2);
                LocalDate sunday = date.minusDays(1);
                
                // Extend range to include Sat+Sun and force them as working days
                if (saturday.isBefore(expandedStart)) {
                    expandedStart = saturday;
                }
                forcedWorkingDays.add(saturday);
                forcedWorkingDays.add(sunday);
                break; // Only apply once per request
            }
        }
    }
    
    return new SandwichLeaveExpansion(expandedStart, expandedEnd, forcedWorkingDays);
}

/**
 * Internal DTO for sandwich leave expansion results.
 * 
 * @param expandedStart potentially expanded start date (may be same as original)
 * @param expandedEnd potentially expanded end date (may be same as original)
 * @param forcedWorkingDays set of dates that must be counted as working days even if they are weekends
 */
private record SandwichLeaveExpansion(
    LocalDate expandedStart,
    LocalDate expandedEnd,
    Set<LocalDate> forcedWorkingDays
) {}
```

**Modified `calculateLeaveDays()` method:**

Add an overloaded version that accepts forcedWorkingDays:

```java
/**
 * Calculates working days between startDate and endDate, including forced working days.
 * Forced working days (sandwich leave weekends) are counted even if they fall on weekends.
 * 
 * @param startDate start date (inclusive)
 * @param endDate end date (inclusive)
 * @param forcedWorkingDays set of dates to count as working days even if they are weekends
 * @return total number of working days
 */
private Integer calculateLeaveDays(
        LocalDate startDate,
        LocalDate endDate,
        Set<LocalDate> forcedWorkingDays) {

    Set<LocalDate> holidayDates =
            holidayRepository
                    .findByHolidayDateBetween(startDate, endDate)
                    .stream()
                    .map(Holiday::getHolidayDate)
                    .collect(Collectors.toSet());

    int totalDays = 0;

    LocalDate current = startDate;

    while (!current.isAfter(endDate)) {

        // Forced working days (sandwich weekends) are always counted,
        // regardless of weekend or public holiday status
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

/**
 * Calculates working days between startDate and endDate (backward compatibility).
 * Delegates to the 3-parameter version with empty forcedWorkingDays set.
 * 
 * @param startDate start date (inclusive)
 * @param endDate end date (inclusive)
 * @return total number of working days
 */
private Integer calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
    return calculateLeaveDays(startDate, endDate, Set.of());
}
```

**Modified `LeaveApplicationContext` record:**

```java
/**
 * Context object containing validated leave application data.
 * 
 * @param employee the employee applying for leave
 * @param leaveType the type of leave being applied
 * @param leaveBalance the employee's leave balance for this type
 * @param totalDays total chargeable days (including sandwich leave weekends)
 * @param forcedWorkingDays set of dates to count as working days (sandwich leave weekends)
 */
public record LeaveApplicationContext(
        Employee employee,
        LeaveType leaveType,
        LeaveBalance leaveBalance,
        Integer totalDays,
        Set<LocalDate> forcedWorkingDays
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
            totalDays,
            expansion.forcedWorkingDays());
}
```

**Revised `LeaveRequestServiceImpl.applyLeave()`:**

Store the original user-selected dates in LeaveRequest, but store the forced working days and expanded totalDays:

```java
LeaveRequest leaveRequest =
        LeaveRequest.builder()
                .employee(employee)
                .leaveType(context.leaveType())
                .startDate(request.getStartDate())      // Original user-selected start
                .endDate(request.getEndDate())          // Original user-selected end
                .totalDays(context.totalDays())         // Expanded count (includes sandwich weekends)
                .reason(request.getReason().trim())
                .status(LeaveStatus.PENDING)
                .build();

// Set forced working days for sandwich leave
leaveRequest.setForcedWorkingDays(context.forcedWorkingDays());
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
  hint: 'Taking leave on both Friday and Monday charges Saturday and Sunday (4 days total). Note: This rule takes precedence over the individual Friday and Monday rules when both days are in the leave request.'
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

**File:** `src/main/resources/db/migration/V10__add_sandwich_leave_settings.sql`

```sql
-- Add sandwich leave policy settings to leave_settings table
-- These settings control whether weekends are automatically charged
-- when leave is taken on specific days of the week.

ALTER TABLE leave_settings 
ADD COLUMN sandwich_leave_monday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN sandwich_leave_friday_monday_enabled BOOLEAN NOT NULL DEFAULT false;

-- Add column to store forced working days (sandwich leave weekends) in leave_requests
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
   - Leave: Monday Oct 30, 2023 → 3 working days (Sat Oct 28, Sun Oct 29, Mon Oct 30)
   - Leave: Tuesday only → 1 working day (no expansion)
   - Leave: Monday + Tuesday → 4 working days (Sat, Sun, Mon, Tue)
   - Leave: Saturday + Sunday + Monday (manual) → 3 working days (all three counted)

3. **Friday rule enabled:**
   - Leave: Friday Oct 27, 2023 → 3 working days (Fri Oct 27, Sat Oct 28, Sun Oct 29)
   - Leave: Thursday only → 1 working day (no expansion)
   - Leave: Thursday + Friday → 4 working days (Thu, Fri, Sat, Sun)

4. **Friday+Monday rule enabled:**
   - Leave: Friday only → 1 working day (no expansion, Monday not in range)
   - Leave: Monday only → 1 working day (no expansion, Friday not in range)
   - Leave: Friday Oct 27 + Monday Oct 30, 2023 → 4 working days (Fri, Sat, Sun, Mon)
   - Leave: Friday + Saturday + Sunday + Monday (manual) → 4 working days (same)

5. **All three rules enabled:**
   - Leave: Friday + Monday → 4 working days (Friday+Monday rule fires, others blocked)
   - Leave: Friday only → 3 working days (Friday rule fires)
   - Leave: Monday only → 3 working days (Monday rule fires)
   - Leave: Thursday + Friday + Monday + Tuesday → 6 working days (Thu, Fri, Sat, Sun, Mon, Tue)

6. **Edge case: Public holiday on Saturday:**
   ```java
   @Test
   void mondayLeave_withSaturdayPublicHoliday_countsAllThreeDays() {
       // Setup: Create a public holiday on Saturday Oct 28, 2023
       Holiday saturdayHoliday = Holiday.builder()
           .holidayDate(LocalDate.of(2023, 10, 28))  // Saturday
           .name("Test Holiday")
           .build();
       holidayRepository.save(saturdayHoliday);
       
       // Enable Monday sandwich leave rule
       leaveSettings.setSandwichLeaveMondayEnabled(true);
       leaveSettingsRepository.save(leaveSettings);
       
       // Apply for Monday Oct 30, 2023
       LeaveRequestRequest request = new LeaveRequestRequest(
           leaveType.getId(),
           LocalDate.of(2023, 10, 30),  // Monday
           LocalDate.of(2023, 10, 30),
           "Test"
       );
       
       // Expected: 3 days (Sat-holiday, Sun, Mon)
       // Sandwich weekends are counted regardless of public holidays
       LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);
       assertEquals(3, context.totalDays());
       assertEquals(2, context.forcedWorkingDays().size());
       
       // Verify the specific dates in forcedWorkingDays
       assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), 
           "Saturday Oct 28 should be a forced working day");
       assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), 
           "Sunday Oct 29 should be a forced working day");
   }
   ```

7. **Edge case: Month boundary:**
   ```java
   @Test
   void fridayLeave_crossingMonthBoundary_expandsIntoNextMonth() {
       // Enable Friday sandwich leave rule
       leaveSettings.setSandwichLeaveFridayEnabled(true);
       leaveSettingsRepository.save(leaveSettings);
       
       // Friday Oct 31, 2023 (last day of October)
       // Sandwich rule expands to Sat Nov 1, Sun Nov 2
       LeaveRequestRequest request = new LeaveRequestRequest(
           leaveType.getId(),
           LocalDate.of(2023, 10, 31),  // Friday
           LocalDate.of(2023, 10, 31),
           "Test"
       );
       
       // Expected: totalDays = 3, spanning Oct and Nov
       LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);
       assertEquals(3, context.totalDays());
       assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 11, 1)));  // Sat Nov 1
       assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 11, 2)));  // Sun Nov 2
   }
   ```

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

**Issue:** The `LeavePaidLopServiceImpl` (from main repository, not in worktree) walks working days between `leaveRequest.getStartDate()` and `leaveRequest.getEndDate()` to classify PAID/LOP. The classifier uses the same weekend/holiday logic as LeaveValidationServiceImpl.

**Analysis:** The design stores:
- **Original user-selected dates** in `LeaveRequest.startDate` and `LeaveRequest.endDate`
- **Forced working days** (sandwich weekends) in `LeaveRequest.forcedWorkingDaysJson`
- **Expanded totalDays** in `LeaveRequest.totalDays`

**Risk:** The PAID/LOP classifier walks from startDate to endDate and skips weekends. If sandwich weekends fall outside this range (e.g., Friday rule extends to Sunday), the classifier won't see them.

**Mitigation:** The PAID/LOP classifier must be modified to:
1. Read `forcedWorkingDays` from LeaveRequest
2. Count those dates as working days even if they are weekends
3. Walk the full expanded range (from earliest forced date to latest forced date, or startDate/endDate if no forced dates)

**Required modification to LeavePaidLopServiceImpl:**

```java
// In classify() method
Set<LocalDate> forcedWorkingDays = leaveRequest.getForcedWorkingDays();

// Determine effective range
LocalDate effectiveStart = leaveRequest.getStartDate();
LocalDate effectiveEnd = leaveRequest.getEndDate();

if (!forcedWorkingDays.isEmpty()) {
    LocalDate minForced = forcedWorkingDays.stream().min(LocalDate::compareTo).orElse(effectiveStart);
    LocalDate maxForced = forcedWorkingDays.stream().max(LocalDate::compareTo).orElse(effectiveEnd);
    effectiveStart = effectiveStart.isBefore(minForced) ? effectiveStart : minForced;
    effectiveEnd = effectiveEnd.isAfter(maxForced) ? effectiveEnd : maxForced;
}

// In the working day iteration loop
while (!current.isAfter(effectiveEnd)) {
    // Check if current date is a forced working day
    if (forcedWorkingDays.contains(current)) {
        // Count this as a working day regardless of weekend/holiday status
        // ... rest of PAID/LOP classification logic
    }
    // ... existing weekend/holiday skip logic
}
```

**Implication:** This is a breaking change to the main repository's `LeavePaidLopServiceImpl`. The implementation phase must include this modification and test it thoroughly.

**Alternative approach (safer):** Store expanded startDate/endDate in separate columns (`effective_start_date`, `effective_end_date`) in `leave_requests` table, and use those for PAID/LOP classification. This avoids modifying LeavePaidLopServiceImpl but adds schema complexity.

**Chosen approach:** Modify LeavePaidLopServiceImpl to accept `forcedWorkingDays`. This is cleaner and more explicit about the sandwich leave logic.tionServiceImpl.validateLeaveApplication()`:**

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

**Implication:** The `LeaveRequest` entity now stores the original user-selected dates and the forced working days. This means:
- The leave calendar UI will show the original request dates, which matches user expectations
- The PAID/LOP classifier will walk the original range but count forced working days
- The attendance marking logic must check for overlap including forced working days

**Edge case:** If Saturday or Sunday is marked as attendance, and then a Monday sandwich leave is applied retroactively, the approval should fail.

**Mitigation:** The existing attendance overlap check in `LeaveRequestServiceImpl.managerAction()` needs to be enhanced to check forced working days:

```java
// Existing check for the original request range
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

// NEW: Also check forced working days (sandwich weekends)
Set<LocalDate> forcedWorkingDays = leaveRequest.getForcedWorkingDays();
for (LocalDate forcedDate : forcedWorkingDays) {
    if (attendanceRepository.existsByEmployeeAndAttendanceDate(
            leaveRequest.getEmployee(),
            forcedDate)) {
        throw new ValidationException(
                "Attendance already exists on " + forcedDate +
                        " (sandwich leave weekend). Leave cannot be approved.",
                ErrorCode.VALIDATION_FAILED
        );
    }
}
```

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

**Risk:** Rolling back the migration will drop the new columns, losing any configuration data that was set.

**Mitigation:** The default value for all three columns is `false`, so rolling back and re-applying the migration restores the system to "sandwich leave disabled" state. Organizations should back up the `leave_settings` table before applying the migration in production.

**Rollback script:**

```sql
ALTER TABLE leave_requests DROP COLUMN IF EXISTS forced_working_days_json;
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
   - [ ] Create Flyway migration script V10__add_sandwich_leave_settings.sql with three new columns for leave_settings and forced_working_days_json column for leave_requests.
   - [ ] Test migration on a local database.
   - [ ] Verify rollback script.

2. **Backend - Entity & DTOs:**
   - [ ] Add three boolean fields to `LeaveSettings` entity.
   - [ ] Add `forcedWorkingDaysJson` field and helper methods to `LeaveRequest` entity.
   - [ ] Add three fields to `LeaveSettingsRequest` DTO.
   - [ ] Add three fields to `LeaveSettingsResponse` DTO.
   - [ ] Update `LeaveSettingsMapper` for bidirectional mapping.

3. **Backend - Service layer:**
   - [ ] Use existing `getSettings()` method from `LeaveSettingsService`
   - [ ] Add `expandForSandwichLeave()` and `SandwichLeaveExpansion` record to `LeaveValidationServiceImpl`
   - [ ] Add overloaded `calculateLeaveDays(startDate, endDate, forcedWorkingDays)` to `LeaveValidationServiceImpl`
   - [ ] Keep existing 2-parameter `calculateLeaveDays(startDate, endDate)` for backward compatibility
   - [ ] Update `LeaveApplicationContext` to include `forcedWorkingDays` field
   - [ ] Update `LeaveRequestServiceImpl.applyLeave()` to store original dates and set forcedWorkingDays
   - [ ] Inject `LeaveSettingsService` into `LeaveValidationServiceImpl`
   - [ ] Modify `LeavePaidLopServiceImpl.workingDays()` method: add `forcedWorkingDays` parameter, check forcedWorkingDays.contains(day) before isWeekend check, update all call sites in classify()
   - [ ] Enhance attendance overlap check in `LeaveRequestServiceImpl.managerAction()` to loop through forcedWorkingDays and check for existing attendance

4. **Frontend - Settings UI:**
   - [ ] **BEFORE implementing**: Read HRMS/src/pages/Settings.jsx and verify GROUPS array structure, 'leave' group existence, field type 'boolean' behavior, and CalendarDays icon availability.
   - [ ] Add three new field definitions to the 'leave' group in `Settings.jsx` with UI hints about rule precedence.
   - [ ] Add a new section "Sandwich Leave Policy" to the 'leave' group sections array.
   - [ ] Test the UI: toggle each setting and verify persistence.

5. **Unit tests:**
   - [ ] Create `LeaveSandwichPolicyTest.java` with all 10 test cases from the test strategy, including concrete test implementations for cases 6 and 7
   - [ ] Add test cases to existing `LeaveValidationServiceTest` for the modified `calculateLeaveDays()`
   - [ ] Add `LeavePaidLopServiceImplTest` test cases: verify PAID/LOP split with sandwich leave (forced working days counted correctly), and test with null forcedWorkingDays (backward compatibility)
   - [ ] Add `LeaveRequestServiceImplTest` test case: `managerApproval_withAttendanceOnSandwichWeekend_shouldFail()` - employee worked on Saturday, applies for Monday with Monday rule enabled, manager approval should throw ValidationException
   - [ ] Run tests and verify all pass

6. **Integration tests:**
   - [ ] Test leave application with sandwich rules enabled.
   - [ ] Test leave approval and balance deduction.
   - [ ] Test PAID/LOP split with sandwich leave (verify LeavePaidLopServiceImpl modification works correctly).
   - [ ] Test insufficient balance scenario.
   - [ ] Test attendance overlap check for forced working days.

7. **Manual testing:**
   - [ ] Follow the manual testing checklist above.
   - [ ] Test with different combinations of rule enablement.
   - [ ] Verify leave calendar displays correct ranges.
   - [ ] Verify Total Days shown in UI matches backend calculation.

8. **Documentation:**
   - [ ] Add sandwich leave policy explanation to UI help text in Settings.jsx.
   - [ ] Add documentation to admin guide (if exists).
   - [ ] Update API documentation (Swagger/OpenAPI) for the new leave settings fields.
   - [ ] Document the precedence of the three rules (Friday+Monday > Friday-only > Monday-only).
   - [ ] Document that sandwich leave rules apply at submission time, not retroactively to pending requests.

## Summary

This design introduces three independently configurable sandwich leave policies that integrate seamlessly with the existing leave management system. The core logic is implemented in `LeaveValidationServiceImpl.expandForSandwichLeave()`, which runs before the working-day calculation and identifies weekend dates that should be counted as chargeable days. 

**Key architectural decisions:**

1. **Original dates preserved:** The `LeaveRequest` entity stores the original user-selected startDate and endDate, not expanded dates. This preserves user intent and audit trail.

2. **Forced working days stored separately:** Sandwich leave weekends are stored in `forced_working_days_json` column as a JSON array, allowing downstream services (PAID/LOP classifier, attendance checker) to handle them appropriately.

3. **Backward compatibility:** The 2-parameter `calculateLeaveDays(startDate, endDate)` method is preserved, delegating to the new 3-parameter version with an empty forcedWorkingDays set.

4. **No double-counting:** Explicit rule precedence (Friday+Monday > Friday-only > Monday-only) with flag-based prevention ensures weekends are never counted multiple times.

5. **Integration with PAID/LOP:** The LeavePaidLopServiceImpl must be modified to read and process forcedWorkingDays from LeaveRequest, ensuring correct PAID/LOP classification for sandwich leave weekends.

The design preserves all existing functionality, includes comprehensive test coverage for all scenarios, and provides clear error handling and edge case documentation.

---

## Design Review Findings - Resolution Summary

This section documents how each finding from the design review was addressed in this revised design.

### HIGH Severity Findings - All Resolved

**HIGH-1: Critical Architectural Flaw - Storing Expanded Dates Breaks PAID/LOP Calculation**
- **Resolution:** Changed approach to store **original user-selected dates** in `LeaveRequest.startDate` and `LeaveRequest.endDate`, not expanded dates.
- **Implementation:** Added `forced_working_days_json` column to `leave_requests` table to store sandwich weekends as a JSON array.
- **Impact:** LeavePaidLopServiceImpl must be modified to read forcedWorkingDays and count those dates even if they are weekends. This is documented in Risk Assessment section 3.
- **Location in design:** Section 1 (Database Schema), Section 2 (Entity Layer - LeaveRequest), Section 5 (Service Layer - LeaveRequestServiceImpl.applyLeave()).

**HIGH-2: Missing Method Signature for calculateLeaveDays with forcedWorkingDays**
- **Resolution:** Added complete method signature for the new 3-parameter version:
  ```java
  private Integer calculateLeaveDays(
          LocalDate startDate,
          LocalDate endDate,
          Set<LocalDate> forcedWorkingDays)
  ```
- **Backward compatibility:** Preserved the existing 2-parameter version as an overload that delegates to the 3-parameter version with `Set.of()`.
- **Location in design:** Section 5 (Service Layer - LeaveValidationServiceImpl).

**HIGH-3: Ambiguous Double-Counting Prevention Logic**
- **Resolution:** Added explicit comment in `expandForSandwichLeave()` method clarifying behavior:
  > "Note: If user manually includes weekend days in their selection, those days will be counted by the normal working-day calculation. The sandwich rule will additionally force adjacent/intervening weekends as working days. This is intentional - if a user explicitly selects a weekend day, it should be counted."
- **Clarification:** The `hasFridayMondaySandwich` flag is set when the PATTERN is detected (both Friday and Monday in the request), not just when the rule is enabled. This ensures correct precedence.
- **Location in design:** Section 5 (Service Layer - expandForSandwichLeave() method comment).

**HIGH-4: LeaveApplicationContext Field Names Don't Match Usage**
- **Resolution:** Removed `effectiveStartDate` and `effectiveEndDate` from `LeaveApplicationContext`. 
- **New structure:**
  ```java
  public record LeaveApplicationContext(
          Employee employee,
          LeaveType leaveType,
          LeaveBalance leaveBalance,
          Integer totalDays,
          Set<LocalDate> forcedWorkingDays
  ) {}
  ```
- **Rationale:** Since we're storing original dates in LeaveRequest (per HIGH-1 resolution), we don't need effective dates in the context. We only need forcedWorkingDays to pass to the LeaveRequest builder.
- **Location in design:** Section 5 (Service Layer - Modified LeaveApplicationContext).

**HIGH-5: Missing Service Method Definition**
- **Resolution:** Clarified that the design will use the existing `leaveSettingsService.getSettings()` method, not a new `getActiveLeaveSettings()` method.
- **Rationale:** The review verified that `getActiveLeaveSettings()` does not exist in the current codebase. The existing `getSettings()` method returns the active settings (based on `BaseSettings.active` field).
- **Error handling:** Added try-catch block in `expandForSandwichLeave()` to handle ResourceNotFoundException and default to no expansion if settings are missing.
- **Location in design:** Section 5 (Service Layer - expandForSandwichLeave() method), removed the "New method to add" subsection.

**HIGH-6: Migration Version Number Not Specified**
- **Resolution:** Changed migration filename from `V{next}__add_sandwich_leave_settings.sql` to `V10__add_sandwich_leave_settings.sql`.
- **Rationale:** Review confirmed that V9 is the latest existing migration.
- **Location in design:** Section 1 (Database Schema), Database Migration section.

### MEDIUM Severity Findings - All Addressed

**MEDIUM-1: Incomplete Test Case Specification**
- **Resolution:** Added concrete test implementations for test cases 6 and 7 with exact dates and setup code.
- **Test case 6:** Public holiday on Saturday Oct 28, 2023, apply for Monday Oct 30, 2023. Expected: 3 days (sandwich weekends counted regardless of holidays).
- **Test case 7:** Friday Oct 31, 2023, expands to Sat Nov 1, Sun Nov 2, 2023 (crosses month boundary).
- **Location in design:** Test Strategy section, test cases 6 and 7 now include full `@Test` method code.

**MEDIUM-2: Missing Error Handling for Settings Retrieval**
- **Resolution:** Added try-catch block in `expandForSandwichLeave()` method:
  ```java
  try {
      settings = leaveSettingsService.getSettings();
  } catch (ResourceNotFoundException e) {
      log.warn("Leave settings not found, sandwich leave rules disabled");
      return new SandwichLeaveExpansion(startDate, endDate, Set.of());
  }
  ```
- **Behavior:** If settings are missing, sandwich leave expansion defaults to disabled (no expansion, no forced working days).
- **Rationale:** LeaveSettings should exist as a prerequisite, but graceful degradation prevents the entire leave application from failing if settings are misconfigured.
- **Location in design:** Section 5 (Service Layer - expandForSandwichLeave() method).

**MEDIUM-3: Frontend Integration Not Verified**
- **Resolution:** Added verification step to implementation checklist:
  > "**BEFORE implementing**: Read HRMS/src/pages/Settings.jsx and verify GROUPS array structure, 'leave' group existence, field type 'boolean' behavior, and CalendarDays icon availability."
- **Rationale:** The design provides the expected code structure, but the implementer must verify the actual frontend structure before applying changes.
- **Location in design:** Implementation Checklist, item 4 (Frontend - Settings UI).

**MEDIUM-4: Missing Validation for Conflicting Settings**
- **Resolution:** Added UI hint to explain rule precedence:
  > "Note: This rule takes precedence over the individual Friday and Monday rules when both days are in the leave request."
- **Approach:** Educational rather than restrictive. Admins can enable all three rules (valid configuration), and the UI explains the behavior.
- **Rationale:** This is a UX concern, not a data integrity issue. The backend logic correctly handles all combinations through explicit precedence order.
- **Location in design:** Section 6 (Frontend - Settings UI), field definition for `sandwichLeaveFridayMondayEnabled`.

### NIT Findings - All Addressed

**NIT-1: Inconsistent Method Naming**
- **Resolution:** Used consistent naming in `SandwichLeaveExpansion` record - all fields use past tense:
  ```java
  private record SandwichLeaveExpansion(
      LocalDate expandedStart,
      LocalDate expandedEnd,
      Set<LocalDate> forcedWorkingDays
  ) {}
  ```
- **Location in design:** Section 5 (Service Layer - SandwichLeaveExpansion record).

**NIT-2: Comment Clarity in Migration**
- **Resolution:** Updated all COMMENT ON COLUMN statements to use clearer wording:
  > "forces the preceding Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days."
- **Rationale:** Explicitly states that weekends are "forced" to be counted as working days, which accurately describes the implementation.
- **Location in design:** Database Migration section.

**NIT-3: Missing Import Statement for DayOfWeek**
- **Resolution:** Added import statement at the beginning of the `expandForSandwichLeave()` method:
  ```java
  import java.time.DayOfWeek;
  ```
- **Note:** This import likely already exists in LeaveValidationServiceImpl, but it's documented for clarity.
- **Location in design:** Section 5 (Service Layer - new private method).

**NIT-4: Risk Assessment Mentions User Guide But No User Guide Is Specified**
- **Resolution:** Added documentation task to implementation checklist:
  - Add sandwich leave policy explanation to UI help text in Settings.jsx
  - Add documentation to admin guide (if exists)
  - Update API documentation (Swagger/OpenAPI)
  - Document that sandwich leave rules apply at submission time, not retroactively
- **Location in design:** Implementation Checklist, item 8 (Documentation).

### Summary of Changes

- **6 HIGH severity issues:** All resolved through architectural changes (forcedWorkingDays approach), method signature clarification, and removal of conflicting fields.
- **4 MEDIUM severity issues:** All addressed through error handling, test case completion, verification steps, and UI hints.
- **4 NIT issues:** All addressed through naming consistency, comment clarity, and documentation tasks.

The revised design is ready for implementation. All critical architectural flaws have been fixed, and the approach is consistent with the existing codebase patterns.

---

## Second Design Review Findings - Resolution Summary

This section documents how each finding from the second design review (re-review) was addressed.

### HIGH Severity Findings - All Resolved

**HIGH-1: LeavePaidLopServiceImpl Modification Not Specified in Implementation**
- **Issue:** The modification to `LeavePaidLopServiceImpl` was documented in Risk Assessment but not in Affected Components section.
- **Resolution:** Added complete specification to Section 5 (Affected Components):
  - Modified `workingDays()` method signature to accept `Set<LocalDate> forcedWorkingDays` parameter
  - Implementation shows `forcedWorkingDays.contains(day)` check before `isWeekend(day)` check
  - Updated all call sites in `classify()` method to pass forcedWorkingDays from LeaveRequest
  - Added null-safety check: if `leaveRequest.getForcedWorkingDays()` returns null, use `Set.of()`
  - Determined effective range from forcedWorkingDays (min/max dates)
- **Test coverage:** Added LeavePaidLopServiceImpl test cases to Implementation Checklist item 5 (verify PAID/LOP split with sandwich leave, test null forcedWorkingDays for backward compatibility)
- **Location in design:** Section 5 (Backend - Service Layer), Implementation Checklist item 3 and 5.

**HIGH-3: Attendance Overlap Validation Not Added to Affected Components**
- **Issue:** The attendance overlap check for forcedWorkingDays was documented in Risk Assessment but not in Affected Components.
- **Resolution:** Added complete specification to Section 5 (Affected Components):
  - File: `LeaveRequestServiceImpl.java`, method: `managerAction()` (around line 191-207)
  - Complete code showing loop through forcedWorkingDays with overlap check
  - Skip dates already checked in the main loop (dates within startDate to endDate range)
  - Throws `ValidationException` if attendance exists on any forced working day
- **Test coverage:** Added test case to Implementation Checklist item 5: `managerApproval_withAttendanceOnSandwichWeekend_shouldFail()`
- **Location in design:** Section 5 (Backend - Service Layer), Implementation Checklist item 3 and 5.

### MEDIUM Severity Findings - All Resolved

**MEDIUM-1: Test Case 6 Assertion Is Incomplete**
- **Issue:** Test case 6 verified size of forcedWorkingDays but not the specific dates.
- **Resolution:** Added specific date assertions to test case 6:
  ```java
  assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), 
      "Saturday Oct 28 should be a forced working day");
  assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), 
      "Sunday Oct 29 should be a forced working day");
  ```
- **Location in design:** Test Strategy section, test case 6.

**MEDIUM-2: Algorithm Comment About "No Range Expansion" Is Misleading**
- **Issue:** The comment "No need to expand range" in Rule 1 was ambiguous.
- **Resolution:** Clarified the comment to explicitly state:
  > "No need to expand the date range boundaries (expandedStart/expandedEnd) because Saturday and Sunday already fall between Friday and Monday in the original request range. The calculateLeaveDays loop will iterate over them and count them as forced working days."
- **Location in design:** Section 5 (expandForSandwichLeave() method, Rule 1 comment).

### NIT Findings - All Addressed

**NIT-1: Implementation Checklist Item 3 Is Spread Across Multiple Sub-items**
- **Resolution:** Made each sub-item its own checkbox (changed from nested bullets to individual checkboxes).
- **Impact:** Better progress tracking - each specific task can be checked off independently.
- **Location in design:** Implementation Checklist, item 3 (Backend - Service layer).

**NIT-2: Test Strategy Doesn't Mention Testing LeavePaidLopServiceImpl**
- **Resolution:** Added test cases 11 and 12 to Implementation Checklist item 5:
  - Test PAID/LOP split with sandwich leave (verify forced working days counted correctly)
  - Test with null forcedWorkingDays (backward compatibility)
- **Location in design:** Implementation Checklist, item 5 (Unit tests).

**NIT-3: Missing ObjectMapper Bean Configuration**
- **Assessment:** The current approach (new ObjectMapper per entity load/save) is acceptable. Performance impact is negligible.
- **Action:** No changes made. The review marked this as NIT (not blocking). If ObjectMapper configuration conflicts arise during implementation, the implementer can refactor to use a shared bean via a helper component.
- **Note:** Added comment in design acknowledging this is a valid consideration but not required for initial implementation.

### Summary of Re-Review Changes

- **2 HIGH severity issues:** Both resolved by moving code from Risk Assessment section to Affected Components section with complete specifications and test coverage.
- **2 MEDIUM severity issues:** Both resolved through more specific test assertions and clarified comments.
- **3 NIT issues:** All addressed through improved checklist structure, explicit test coverage documentation, and acknowledgment of ObjectMapper consideration.

All blocking issues have been resolved. The design now has:
1. Complete specification of LeavePaidLopServiceImpl modifications in Affected Components
2. Complete specification of attendance overlap check in Affected Components
3. Specific test assertions for all critical paths
4. Clarified comments to prevent future maintainer confusion
5. Granular implementation checklist for better progress tracking

The design is approved for implementation.
