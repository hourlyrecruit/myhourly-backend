package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.context.LeaveApplicationContext;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.leave.service.LeaveTypeService;
import com.my_hourly.leave.service.impl.LeaveValidationServiceImpl;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Pins the configurable sandwich leave expansion of
 * {@code LeaveValidationServiceImpl}:
 *
 * <ul>
 *   <li><b>Monday rule</b> — leave on a Monday also charges the preceding
 *       Saturday and Sunday (3 days).</li>
 *   <li><b>Friday rule</b> — leave on a Friday also charges the following
 *       Saturday and Sunday (3 days).</li>
 *   <li><b>Friday + Monday rule</b> — leave spanning Friday to Monday charges
 *       the intervening weekend once (4 days).</li>
 * </ul>
 *
 * <p>The rules are independent. When several are enabled the Friday+Monday rule
 * takes precedence, and the weekend days are stored in a {@code Set} so they can
 * never be counted twice.</p>
 *
 * <p>Dates are anchored on real weekdays in November 2026 (Nov 27 2026 is a
 * Friday, Nov 30 2026 is a Monday) and are in the future because leave cannot be
 * applied for a past date.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Configurable sandwich leave policy")
class LeaveSandwichPolicyTest {

    private static final LocalDate THURSDAY = LocalDate.of(2026, 11, 26);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 11, 27);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 11, 28);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 11, 29);
    private static final LocalDate MONDAY = LocalDate.of(2026, 11, 30);
    private static final LocalDate TUESDAY = LocalDate.of(2026, 12, 1);

    @Mock
    private LeaveTypeService leaveTypeService;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private LeaveSettingsService leaveSettingsService;

    @InjectMocks
    private LeaveValidationServiceImpl leaveValidationService;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveSettings settings;

    @BeforeEach
    void setUp() {

        employee = Employee.builder().firstName("John").lastName("Test").build();
        employee.setId(1L);

        leaveType = LeaveType.builder()
                .name("Annual Leave")
                .paid(true)
                .allocatedDays(24)
                .monthlyGuideline(2)
                .active(true)
                .build();
        leaveType.setId(10L);

        settings = LeaveSettings.builder()
                .carryForwardAllowed(false)
                .monthlyGuideline(2)
                .annualPaidLeave(24)
                .build();

        when(leaveTypeService.getLeaveTypeEntity(leaveType.getId())).thenReturn(leaveType);
        when(leaveSettingsService.getSettings()).thenReturn(settings);
        when(leaveBalanceService.getLeaveBalanceEntity(any(), any(), any()))
                .thenReturn(LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(leaveType)
                        .year(LocalDate.now().getYear())
                        .allocatedLeaves(24)
                        .usedLeaves(0)
                        .expiredLeaves(0)
                        .remainingLeaves(24)
                        .build());
        when(leaveRequestRepository
                .existsByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        any(), anyList(), any(), any()))
                .thenReturn(false);
        when(holidayRepository.findByHolidayDateBetween(any(), any()))
                .thenReturn(List.of());
    }

    private LeaveApplicationContext validate(LocalDate start, LocalDate end) {

        return leaveValidationService.validateLeaveApplication(
                employee,
                new LeaveRequestRequest(leaveType.getId(), start, end, "Sandwich leave test"));
    }

    // -----------------------------------------------------------------------
    // No rule enabled: plain weekend skipping
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("No rules: Friday-only leave charges 1 day")
    void noRulesFridayOnlyChargesOneDay() {

        LeaveApplicationContext context = validate(FRIDAY, FRIDAY);

        assertEquals(1, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    @Test
    @DisplayName("No rules: Monday-only leave charges 1 day")
    void noRulesMondayOnlyChargesOneDay() {

        LeaveApplicationContext context = validate(MONDAY, MONDAY);

        assertEquals(1, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    @Test
    @DisplayName("No rules: Friday to Monday charges only the two working days")
    void noRulesFridayToMondayChargesTwoDays() {

        LeaveApplicationContext context = validate(FRIDAY, MONDAY);

        assertEquals(2, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    // -----------------------------------------------------------------------
    // Monday rule
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Monday rule: Monday leave charges the preceding weekend (3 days)")
    void mondayRuleChargesPrecedingWeekend() {

        settings.setSandwichLeaveMondayEnabled(true);

        LeaveApplicationContext context = validate(MONDAY, MONDAY);

        assertEquals(3, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
        assertTrue(context.forcedWorkingDays().contains(SATURDAY));
        assertTrue(context.forcedWorkingDays().contains(SUNDAY));
    }

    @Test
    @DisplayName("Monday rule: leaves not on Monday are unaffected")
    void mondayRuleIgnoresNonMonday() {

        settings.setSandwichLeaveMondayEnabled(true);

        LeaveApplicationContext context = validate(TUESDAY, TUESDAY);

        assertEquals(1, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    // -----------------------------------------------------------------------
    // Friday rule
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Friday rule: Friday leave charges the following weekend (3 days)")
    void fridayRuleChargesFollowingWeekend() {

        settings.setSandwichLeaveFridayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, FRIDAY);

        assertEquals(3, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
        assertTrue(context.forcedWorkingDays().contains(SATURDAY));
        assertTrue(context.forcedWorkingDays().contains(SUNDAY));
    }

    @Test
    @DisplayName("Friday rule: leaves not on Friday are unaffected")
    void fridayRuleIgnoresNonFriday() {

        settings.setSandwichLeaveFridayEnabled(true);

        LeaveApplicationContext context = validate(THURSDAY, THURSDAY);

        assertEquals(1, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    // -----------------------------------------------------------------------
    // Friday + Monday rule
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Friday+Monday rule: Friday to Monday charges the weekend (4 days)")
    void fridayMondayRuleChargesInterveningWeekend() {

        settings.setSandwichLeaveFridayMondayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, MONDAY);

        assertEquals(4, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
        assertTrue(context.forcedWorkingDays().contains(SATURDAY));
        assertTrue(context.forcedWorkingDays().contains(SUNDAY));
    }

    @Test
    @DisplayName("Friday+Monday rule: Friday alone does not fire")
    void fridayMondayRuleNeedsMonday() {

        settings.setSandwichLeaveFridayMondayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, FRIDAY);

        assertEquals(1, context.totalDays());
        assertTrue(context.forcedWorkingDays().isEmpty());
    }

    // -----------------------------------------------------------------------
    // Independence and precedence (no double counting)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("All rules: Friday to Monday still charges 4 days exactly once")
    void allRulesFridayToMondayNoDoubleCounting() {

        settings.setSandwichLeaveMondayEnabled(true);
        settings.setSandwichLeaveFridayEnabled(true);
        settings.setSandwichLeaveFridayMondayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, MONDAY);

        assertEquals(4, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
    }

    @Test
    @DisplayName("All rules: Friday alone uses the Friday rule")
    void allRulesFridayUsesFridayRule() {

        settings.setSandwichLeaveMondayEnabled(true);
        settings.setSandwichLeaveFridayEnabled(true);
        settings.setSandwichLeaveFridayMondayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, FRIDAY);

        assertEquals(3, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
    }

    @Test
    @DisplayName("All rules: Monday alone uses the Monday rule")
    void allRulesMondayUsesMondayRule() {

        settings.setSandwichLeaveMondayEnabled(true);
        settings.setSandwichLeaveFridayEnabled(true);
        settings.setSandwichLeaveFridayMondayEnabled(true);

        LeaveApplicationContext context = validate(MONDAY, MONDAY);

        assertEquals(3, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
    }

    @Test
    @DisplayName("Friday and Monday rules without the combined rule: weekend counted once")
    void overlappingSingleRulesCountWeekendOnce() {

        settings.setSandwichLeaveFridayEnabled(true);
        settings.setSandwichLeaveMondayEnabled(true);

        LeaveApplicationContext context = validate(FRIDAY, MONDAY);

        assertEquals(4, context.totalDays());
        assertEquals(2, context.forcedWorkingDays().size());
        assertTrue(context.forcedWorkingDays().contains(SATURDAY));
        assertTrue(context.forcedWorkingDays().contains(SUNDAY));
    }

    // -----------------------------------------------------------------------
    // Sandwich weekends override holidays and month boundaries
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Monday rule: a public holiday on the forced Saturday still charges 3 days")
    void forcedWeekendOverridesHoliday() {

        when(holidayRepository.findByHolidayDateBetween(any(), any()))
                .thenReturn(List.of(Holiday.builder()
                        .holidayDate(SATURDAY)
                        .holidayName("Test Holiday")
                        .active(true)
                        .build()));

        settings.setSandwichLeaveMondayEnabled(true);

        LeaveApplicationContext context = validate(MONDAY, MONDAY);

        assertEquals(3, context.totalDays());
        assertTrue(context.forcedWorkingDays().contains(SATURDAY));
        assertTrue(context.forcedWorkingDays().contains(SUNDAY));
    }
}
