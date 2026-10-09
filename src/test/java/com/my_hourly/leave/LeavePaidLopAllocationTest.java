package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.dto.PaidLopAllocation;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.impl.LeavePaidLopServiceImpl;
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
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pins the monthly PAID / LOP rule from {@code LeavePaidLopServiceImpl}:
 *
 * <pre>
 *   per calendar month, in date order:
 *     allowanceLeft = max(0, monthlyGuideline - alreadyPaidDaysInMonth)
 *     a day is PAID while allowanceLeft &gt; 0 AND balance.remaining &gt; 0
 *     otherwise it is LOP
 * </pre>
 *
 * <p>These assertions are about the classification only — the allocator never
 * writes, so "LOP does not reduce the balance" is verified both by the numbers
 * and by asserting the balance repository is never saved.</p>
 *
 * <p>Dates are anchored on real weekdays in October/November 2026 (Oct 5 2026 is
 * a Monday; Oct 31 2026 is a Saturday) so weekends fall where the test expects
 * without a holiday table.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Monthly PAID / LOP classification")
class LeavePaidLopAllocationTest {

    private static final int DEFAULT_GUIDELINE = 2;
    private static final int DEFAULT_REMAINING = 12;

    /** Monday. Mon..Thu is four working days. */
    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 5);

    /** Thu Oct 29 .. Tue Nov 3 2026 - two working days in each month, weekend in between. */
    private static final LocalDate CROSS_MONTH_START = LocalDate.of(2026, 10, 29);
    private static final LocalDate CROSS_MONTH_END = LocalDate.of(2026, 11, 3);

    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final YearMonth NOVEMBER = YearMonth.of(2026, 11);

    @Mock
    private LeaveSettingsService leaveSettingsService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private HolidayRepository holidayRepository;

    @InjectMocks
    private LeavePaidLopServiceImpl leavePaidLopService;

    private Employee employee;
    private LeaveType leaveType;

    /** Inputs every test varies; the stubs below read them at invocation time. */
    private int guideline;
    private int remaining;
    private final Map<YearMonth, Integer> paidDaysByMonth = new HashMap<>();

    @BeforeEach
    void setUp() {

        guideline = DEFAULT_GUIDELINE;
        remaining = DEFAULT_REMAINING;
        paidDaysByMonth.clear();

        employee = Employee.builder().firstName("John").lastName("Test").build();
        employee.setId(1L);

        leaveType = LeaveType.builder()
                .name("Annual Leave")
                .paid(true)
                .allocatedDays(24)
                .monthlyGuideline(DEFAULT_GUIDELINE)
                .active(true)
                .build();
        leaveType.setId(10L);

        when(leaveSettingsService.getSettings()).thenAnswer(invocation ->
                LeaveSettings.builder()
                        .carryForwardAllowed(false)
                        .monthlyGuideline(guideline)
                        .annualPaidLeave(24)
                        .build());

        when(holidayRepository.findByHolidayDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        when(leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(any(), any(), anyInt()))
                .thenAnswer(invocation -> Optional.of(balance()));

        when(leaveRequestRepository.sumPaidLeaveDaysInMonth(
                any(), any(), any(LocalDate.class), any(LocalDate.class)))
                .thenAnswer(invocation -> paidDaysByMonth.getOrDefault(
                        YearMonth.from(invocation.getArgument(2, LocalDate.class)), 0));
    }

    private LeaveBalance balance() {

        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(2026)
                .allocatedLeaves(24)
                .usedLeaves(24 - remaining)
                .expiredLeaves(0)
                .remainingLeaves(remaining)
                .build();
    }

    private PaidLopAllocation classify(LocalDate start, LocalDate end) {

        return leavePaidLopService.classify(employee, leaveType, start, end);
    }

    // -----------------------------------------------------------------------
    // The guideline caps PAID days for the month
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Days beyond the monthly guideline are LOP")
    void daysBeyondTheGuidelineAreLop() {

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3)); // Mon..Thu

        assertEquals(DEFAULT_GUIDELINE, allocation.paidDays(),
                "Only the month's guideline days may be PAID");
        assertEquals(2, allocation.lopDays(),
                "The remaining days are LOP");
        assertEquals(4, allocation.totalDays());
    }

    @Test
    @DisplayName("A request shorter than the guideline is entirely PAID")
    void requestShorterThanGuidelineIsEntirelyPaid() {

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(1)); // Mon..Tue

        assertEquals(2, allocation.paidDays());
        assertEquals(0, allocation.lopDays());
    }

    @Test
    @DisplayName("Raising the monthly guideline raises the PAID days")
    void changingTheGuidelineChangesTheAllocation() {

        guideline = 3;

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(3, allocation.paidDays());
        assertEquals(1, allocation.lopDays());
    }

    @Test
    @DisplayName("Lowering the monthly guideline lowers the PAID days")
    void loweringTheGuidelineLowersThePaidDays() {

        guideline = 1;

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(1, allocation.paidDays());
        assertEquals(3, allocation.lopDays());
    }

    @Test
    @DisplayName("PAID days already approved this month reduce the remaining allowance")
    void alreadyApprovedPaidDaysReduceTheAllowance() {

        paidDaysByMonth.put(OCTOBER, 1);

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(1, allocation.paidDays(),
                "One of the two guideline days was already spent this month");
        assertEquals(3, allocation.lopDays());
    }

    @Test
    @DisplayName("A fully spent month makes every new day LOP")
    void fullySpentMonthMakesEveryDayLop() {

        paidDaysByMonth.put(OCTOBER, DEFAULT_GUIDELINE);

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(0, allocation.paidDays());
        assertEquals(4, allocation.lopDays());
    }

    // -----------------------------------------------------------------------
    // Month boundaries reset the allowance
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("A request crossing a month boundary uses each month's own allowance")
    void crossMonthRequestUsesEachMonthsAllowance() {

        // October's allowance is already exhausted, November's is untouched.
        paidDaysByMonth.put(OCTOBER, DEFAULT_GUIDELINE);

        PaidLopAllocation allocation = classify(CROSS_MONTH_START, CROSS_MONTH_END);

        assertEquals(2, allocation.paidDays(),
                "Only November's untouched allowance can be spent");
        assertEquals(2, allocation.lopDays(),
                "October's days are LOP because October's allowance is spent");

        List<PaidLopAllocation.MonthAllocation> months = allocation.months();
        assertEquals(2, months.size());

        PaidLopAllocation.MonthAllocation october = months.get(0);
        assertEquals(OCTOBER, october.month());
        assertEquals(2, october.workingDays(), "Weekend days are not counted");
        assertEquals(0, october.paidDays());
        assertEquals(2, october.lopDays());

        PaidLopAllocation.MonthAllocation november = months.get(1);
        assertEquals(NOVEMBER, november.month());
        assertEquals(2, november.workingDays());
        assertEquals(DEFAULT_GUIDELINE, november.paidDays(),
                "November starts with a fresh guideline allowance");
        assertEquals(0, november.lopDays());
    }

    @Test
    @DisplayName("A fully unused cross-month request is PAID in both months")
    void unusedCrossMonthRequestIsPaidInBothMonths() {

        PaidLopAllocation allocation = classify(CROSS_MONTH_START, CROSS_MONTH_END);

        assertEquals(4, allocation.paidDays());
        assertEquals(0, allocation.lopDays());
        assertEquals(2, allocation.months().size());
    }

    // -----------------------------------------------------------------------
    // The annual balance caps PAID days and can never go negative
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("An insufficient annual balance turns the excess into LOP")
    void insufficientBalanceTurnsTheExcessIntoLop() {

        remaining = 1;

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(1, allocation.paidDays(),
                "The balance can only cover one day");
        assertEquals(3, allocation.lopDays());
        assertTrue(allocation.paidDays() <= remaining,
                "PAID days may never exceed the remaining annual balance");
    }

    @Test
    @DisplayName("An exhausted annual balance makes every day LOP")
    void exhaustedBalanceMakesEveryDayLop() {

        remaining = 0;

        PaidLopAllocation allocation = classify(MONDAY, MONDAY.plusDays(3));

        assertEquals(0, allocation.paidDays());
        assertEquals(4, allocation.lopDays());
    }

    @Test
    @DisplayName("The annual balance is shared across the months of a request")
    void annualBalanceIsSharedAcrossMonths() {

        // Two months want up to 2 PAID days each (4 total); only 3 are available.
        remaining = 3;

        PaidLopAllocation allocation = classify(CROSS_MONTH_START, CROSS_MONTH_END);

        assertEquals(3, allocation.paidDays(),
                "October takes 2, leaving only 1 for November");
        assertEquals(1, allocation.lopDays());
    }

    // -----------------------------------------------------------------------
    // Purity
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Classification never writes a balance")
    void classificationNeverWritesABalance() {

        classify(MONDAY, MONDAY.plusDays(3));

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }
}
