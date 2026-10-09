package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.LeaveExpiryPlan;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.impl.LeaveExpiryServiceImpl;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pins the month-end monthly-allowance review from {@code LeaveExpiryServiceImpl}:
 *
 * <pre>
 *   paidThisMonth   = APPROVED PAID days attributed to the month by their dates
 *   unusedGuideline = max(0, monthlyGuideline - paidThisMonth)
 * </pre>
 *
 * <p>The review is REPORT-ONLY: unused guideline days lapse with the calendar
 * month and are never deducted from the annual balance, never added to
 * expiredLeaves and never written to the ledger. Running it repeatedly changes
 * nothing - which is exactly what "expiry processing does not duplicate
 * balance adjustments" requires.</p>
 *
 * <p>These tests mock {@link LeaveRequestRepository} and
 * {@link LeaveBalanceRepository}, so they pin the review <em>behaviour</em>.
 * The date attribution of the underlying queries is pinned separately by the
 * integration test against a real PostgreSQL database.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Month-end leave allowance review")
class LeaveExpiryServiceTest {

    private static final int MONTHLY_GUIDELINE = 2;
    private static final long EMPLOYEE_ID = 1L;
    private static final long OTHER_EMPLOYEE_ID = 2L;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveSettingsService leaveSettingsService;

    @InjectMocks
    private LeaveExpiryServiceImpl leaveExpiryService;

    private Employee employee;
    private Employee otherEmployee;
    private LeaveBalance balance;

    @BeforeEach
    void setUp() {

        employee = Employee.builder()
                .firstName("John")
                .lastName("Test")
                .build();
        employee.setId(EMPLOYEE_ID);

        otherEmployee = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();
        otherEmployee.setId(OTHER_EMPLOYEE_ID);

        balance = LeaveBalance.builder()
                .employee(employee)
                .year(LocalDate.now().getYear())
                .allocatedLeaves(24)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(10)
                .build();
    }

    private void givenSettings(int guideline, boolean carryForwardAllowed) {

        LeaveSettings settings = LeaveSettings.builder()
                .carryForwardAllowed(carryForwardAllowed)
                .monthlyGuideline(guideline)
                .annualPaidLeave(24)
                .build();

        when(leaveSettingsService.getSettings()).thenReturn(settings);
    }

    /** One active employee with one balance, carry-forward disabled. */
    private void givenOneEmployeeWithABalance() {

        givenSettings(MONTHLY_GUIDELINE, false);
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(employee));
        when(leaveBalanceRepository.findByYear(anyInt())).thenReturn(List.of(balance));
    }

    /** Stubs the PAID-days-in-month aggregate for the given employee. */
    private void givenPaidLeaveDaysInMonth(long employeeId, int paidDays) {

        List<LeaveRequestRepository.PaidDaysProjection> rows =
                List.of(paidRow(employeeId, paidDays));

        when(leaveRequestRepository
                .sumPaidLeaveDaysInMonthGrouped(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rows);
    }

    /** Stubs the aggregate with no rows at all - nobody took PAID leave. */
    private void givenNoPaidLeaveDaysInMonth() {

        when(leaveRequestRepository
                .sumPaidLeaveDaysInMonthGrouped(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
    }

    private static LeaveRequestRepository.PaidDaysProjection paidRow(long employeeId, int paidDays) {

        LeaveRequestRepository.PaidDaysProjection paid =
                mock(LeaveRequestRepository.PaidDaysProjection.class);

        when(paid.getEmployeeId()).thenReturn(employeeId);
        when(paid.getPaidDays()).thenReturn(paidDays);

        return paid;
    }

    // -----------------------------------------------------------------------
    // Report-only: no balance is ever modified
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Unused guideline is reported but never deducted from the balance")
    void unusedGuidelineIsNeverDeductedFromTheBalance() {

        givenOneEmployeeWithABalance();
        givenNoPaidLeaveDaysInMonth();

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves(),
                "The annual balance must be untouched - unused monthly allowance lapses, "
                        + "it is never deducted from the balance");
        assertEquals(0, balance.getExpiredLeaves(),
                "No expiredLeaves counter may be incremented by the review");

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    @DisplayName("A repeated run still changes nothing (no duplicate adjustments)")
    void repeatedRunsChangeNothing() {

        givenOneEmployeeWithABalance();
        givenNoPaidLeaveDaysInMonth();

        leaveExpiryService.expireMonthlyUnused();
        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves());
        assertEquals(0, balance.getExpiredLeaves());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verify(leaveBalanceRepository, times(2)).findByYear(anyInt());
    }

    @Test
    @DisplayName("PAID days beyond the guideline also leave the balance untouched")
    void usedGuidelineLeavesTheBalanceUntouched() {

        givenOneEmployeeWithABalance();
        givenPaidLeaveDaysInMonth(EMPLOYEE_ID, MONTHLY_GUIDELINE + 3);

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves());
        assertEquals(0, balance.getExpiredLeaves());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    // -----------------------------------------------------------------------
    // Attribution window
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Usage is measured over the whole expiring month")
    void usageIsMeasuredOverTheExpiringMonth() {

        givenOneEmployeeWithABalance();
        givenNoPaidLeaveDaysInMonth();

        leaveExpiryService.expireMonthlyUnused();

        ArgumentCaptor<LocalDate> monthStart = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> monthEnd = ArgumentCaptor.forClass(LocalDate.class);

        verify(leaveRequestRepository).sumPaidLeaveDaysInMonthGrouped(
                monthStart.capture(),
                monthEnd.capture()
        );

        assertEquals(1, monthStart.getValue().getDayOfMonth(),
                "The window must start on the 1st of the expiring month");
        assertEquals(monthStart.getValue().getMonth(), monthEnd.getValue().getMonth());
        assertEquals(monthStart.getValue().getYear(), monthEnd.getValue().getYear());
        assertEquals(
                monthStart.getValue().with(TemporalAdjusters.lastDayOfMonth()),
                monthEnd.getValue(),
                "The window must end on the last day of the same month"
        );
    }

    // -----------------------------------------------------------------------
    // The plan itself
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("The plan reports the unused allowance per employee")
    void planReportsUnusedAllowancePerEmployee() {

        givenOneEmployeeWithABalance();
        givenPaidLeaveDaysInMonth(EMPLOYEE_ID, 1);

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertFalse(plan.isSkipped());
        assertEquals(1, plan.consideredEmployees());
        assertEquals(1, plan.affectedEmployees());
        assertEquals(MONTHLY_GUIDELINE, plan.monthlyGuideline());

        LeaveExpiryPlan.ExpiryEntry entry = plan.entries().get(0);
        assertEquals(EMPLOYEE_ID, entry.employeeId());
        assertEquals("John Test", entry.employeeName());
        assertEquals(1, entry.paidLeaveDaysInMonth(),
                "The entry must expose the PAID-days figure that drives the report");
        assertEquals(MONTHLY_GUIDELINE - 1, entry.unusedGuidelineDays());
        assertEquals(MONTHLY_GUIDELINE - 1, plan.totalUnusedDays());

        // Preview is read-only.
        assertEquals(10, balance.getRemainingLeaves());
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    @DisplayName("Employees without unused allowance are not reported")
    void employeesWithFullUsageAreNotReported() {

        givenOneEmployeeWithABalance();
        givenPaidLeaveDaysInMonth(EMPLOYEE_ID, MONTHLY_GUIDELINE);

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertFalse(plan.isSkipped());
        assertEquals(1, plan.consideredEmployees(), "The balance is still considered");
        assertEquals(0, plan.affectedEmployees());
        assertEquals(0, plan.totalUnusedDays());
        assertTrue(plan.entries().isEmpty());
    }

    @Test
    @DisplayName("The allowance is one per employee - usage is per employee, not per leave type")
    void usageIsReportedPerEmployee() {

        LeaveBalance otherBalance = LeaveBalance.builder()
                .employee(otherEmployee)
                .year(LocalDate.now().getYear())
                .allocatedLeaves(24)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(8)
                .build();

        givenSettings(MONTHLY_GUIDELINE, false);
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(employee, otherEmployee));
        when(leaveBalanceRepository.findByYear(anyInt()))
                .thenReturn(List.of(balance, otherBalance));
        List<LeaveRequestRepository.PaidDaysProjection> rows = List.of(
                paidRow(EMPLOYEE_ID, MONTHLY_GUIDELINE),
                paidRow(OTHER_EMPLOYEE_ID, 0)
        );
        when(leaveRequestRepository
                .sumPaidLeaveDaysInMonthGrouped(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(rows);

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertEquals(2, plan.consideredEmployees());
        assertEquals(1, plan.affectedEmployees(),
                "Only the employee who did not use the guideline has unused days");
        assertEquals(OTHER_EMPLOYEE_ID, plan.entries().get(0).employeeId(),
                "The employee with 0 PAID days is the one with unused allowance");
        assertEquals(0, plan.entries().get(0).paidLeaveDaysInMonth());
        assertEquals(MONTHLY_GUIDELINE, plan.entries().get(0).unusedGuidelineDays());
    }

    @Test
    @DisplayName("Employees without a leave balance are not considered")
    void employeesWithoutBalanceAreNotConsidered() {

        givenSettings(MONTHLY_GUIDELINE, false);
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(employee));
        when(leaveBalanceRepository.findByYear(anyInt())).thenReturn(List.of());
        givenNoPaidLeaveDaysInMonth();

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertFalse(plan.isSkipped());
        assertEquals(0, plan.consideredEmployees());
        assertEquals(0, plan.affectedEmployees());
    }

    // -----------------------------------------------------------------------
    // Skipped runs
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Carry-forward enabled skips the run and retains the allowance")
    void previewIsSkippedWhenCarryForwardIsEnabled() {

        when(leaveSettingsService.getSettings()).thenReturn(
                LeaveSettings.builder()
                        .carryForwardAllowed(true)
                        .monthlyGuideline(MONTHLY_GUIDELINE)
                        .build()
        );

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertTrue(plan.isSkipped(),
                "With global carry-forward enabled nothing lapses, and the plan must say so");
        assertNotNull(plan.skippedReason());
        assertTrue(plan.entries().isEmpty());
        assertEquals(0, plan.totalUnusedDays());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    @DisplayName("A settings failure is reported instead of thrown")
    void previewIsSkippedWhenSettingsCannotBeLoaded() {

        when(leaveSettingsService.getSettings())
                .thenThrow(new IllegalStateException("no settings row"));

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertTrue(plan.isSkipped());
        assertNotNull(plan.skippedReason());
    }
}
