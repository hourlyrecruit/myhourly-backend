package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.LeaveExpiryPlan;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveTransactionService;
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
import org.springframework.test.util.ReflectionTestUtils;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pins the month-end expiry rule from {@code LeaveExpiryServiceImpl}:
 *
 * <pre>
 *   unusedGuideline = max(0, monthlyGuideline - approvedLeaveDaysInMonth)
 * </pre>
 *
 * <p>Guards the regression where the usage query filtered on a status the
 * schema does not allow and therefore always returned "no leave taken",
 * causing guideline days to be expired for employees who had already used
 * (and had deducted) their approved leave.</p>
 *
 * <p>Note: these tests mock {@link LeaveRequestRepository}, so they pin the
 * expiry <em>algorithm</em>. The status value the query actually filters on is
 * pinned separately by
 * {@link com.my_hourly.leave.repository.LeaveRequestQueryStatusTest}, which
 * needs no database.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Month-end leave expiry")
class LeaveExpiryServiceTest {

    private static final int MONTHLY_GUIDELINE = 2;
    private static final long EMPLOYEE_ID = 1L;
    private static final long LEAVE_TYPE_ID = 10L;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveTransactionService leaveTransactionService;

    @Mock
    private LeaveSettingsService leaveSettingsService;

    @InjectMocks
    private LeaveExpiryServiceImpl leaveExpiryService;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveBalance balance;

    @BeforeEach
    void setUp() {

        employee = Employee.builder()
                .firstName("John")
                .lastName("Test")
                .build();
        employee.setId(EMPLOYEE_ID);

        leaveType = LeaveType.builder()
                .name("Annual Leave")
                .paid(true)
                .allocatedDays(24)
                .carryForwardAllowed(false)
                .active(true)
                .build();
        leaveType.setId(LEAVE_TYPE_ID);

        balance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(LocalDate.now().getYear())
                .allocatedLeaves(24)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(10)
                .build();
    }

    /**
     * Stubs one active employee, one paid leave type and one balance, i.e. a
     * single eligible balance on which expiry can apply.
     */
    private void givenOneActiveEmployeeWithOnePaidBalance() {

        LeaveSettings settings = LeaveSettings.builder()
                .carryForwardAllowed(false)
                .monthlyGuideline(MONTHLY_GUIDELINE)
                .annualPaidLeave(24)
                .build();

        when(leaveSettingsService.getSettings()).thenReturn(settings);
        when(employeeRepository.findByActiveTrue()).thenReturn(List.of(employee));
        when(leaveTypeRepository.findByActiveTrue()).thenReturn(List.of(leaveType));
        when(leaveBalanceRepository.findByYear(anyInt())).thenReturn(List.of(balance));
    }

    /**
     * Stubs the approved-days-in-month aggregate with the given usage.
     */
    private void givenApprovedLeaveDaysInMonth(long totalDays) {

        LeaveRequestRepository.UsedDaysProjection used =
                mock(LeaveRequestRepository.UsedDaysProjection.class);

        when(used.getEmployeeId()).thenReturn(EMPLOYEE_ID);
        when(used.getLeaveTypeId()).thenReturn(LEAVE_TYPE_ID);
        when(used.getTotalDays()).thenReturn(totalDays);

        when(leaveRequestRepository
                .sumApprovedLeaveDaysInMonthGrouped(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(used));
    }

    /**
     * Stubs the approved-days-in-month aggregate with no rows at all - the
     * exact shape the stale-status bug produced.
     */
    private void givenNoApprovedLeaveDaysInMonth() {

        when(leaveRequestRepository
                .sumApprovedLeaveDaysInMonthGrouped(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
    }

    private void givenDryRunEnabled() {

        ReflectionTestUtils.setField(leaveExpiryService, "dryRun", true);
    }

    // -----------------------------------------------------------------------
    // Expiry
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Approved leave up to the monthly guideline expires nothing")
    void approvedLeaveMatchingGuidelineDoesNotExpire() {

        givenOneActiveEmployeeWithOnePaidBalance();
        // Employee used (and had deducted) 2 days of approved leave this month.
        givenApprovedLeaveDaysInMonth(MONTHLY_GUIDELINE);

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves(),
                "No guideline days are unused, so the balance must be untouched");
        assertEquals(0, balance.getExpiredLeaves(),
                "Nothing may be marked expired when the guideline was fully used");

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    @Test
    @DisplayName("Approved leave beyond the monthly guideline expires nothing")
    void approvedLeaveExceedingGuidelineDoesNotExpire() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenApprovedLeaveDaysInMonth(MONTHLY_GUIDELINE + 3);

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves());
        assertEquals(0, balance.getExpiredLeaves());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    @Test
    @DisplayName("No approved leave at all expires the unused monthly guideline")
    void noApprovedLeaveExpiresTheUnusedGuideline() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenNoApprovedLeaveDaysInMonth();

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10 - MONTHLY_GUIDELINE, balance.getRemainingLeaves(),
                "The whole monthly guideline was unused and must be expired");
        assertEquals(MONTHLY_GUIDELINE, balance.getExpiredLeaves());

        verify(leaveBalanceRepository).save(balance);
        verify(leaveTransactionService).createExpiryTransaction(balance, MONTHLY_GUIDELINE);
    }

    @Test
    @DisplayName("Usage is measured over the whole expiring month")
    void usageIsMeasuredOverTheExpiringMonth() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenApprovedLeaveDaysInMonth(MONTHLY_GUIDELINE);

        leaveExpiryService.expireMonthlyUnused();

        ArgumentCaptor<LocalDate> monthStart = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> monthEnd = ArgumentCaptor.forClass(LocalDate.class);

        verify(leaveRequestRepository).sumApprovedLeaveDaysInMonthGrouped(
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
    // Dry run
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Dry run leaves every balance untouched")
    void dryRunDoesNotTouchBalances() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenNoApprovedLeaveDaysInMonth();
        givenDryRunEnabled();

        leaveExpiryService.expireMonthlyUnused();

        assertEquals(10, balance.getRemainingLeaves(),
                "A dry run must not deduct days, even when days would expire");
        assertEquals(0, balance.getExpiredLeaves(),
                "A dry run must not record expired days");

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    // -----------------------------------------------------------------------
    // Preview
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Preview reports the days that would expire without changing anything")
    void previewReportsTheDaysThatWouldExpire() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenNoApprovedLeaveDaysInMonth();

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertFalse(plan.isSkipped());
        assertEquals(1, plan.consideredBalances());
        assertEquals(1, plan.affectedBalances());
        assertEquals(MONTHLY_GUIDELINE, plan.totalDaysToExpire());
        assertEquals(MONTHLY_GUIDELINE, plan.monthlyGuideline());

        // The window the plan was computed over is the whole expiring month.
        ArgumentCaptor<LocalDate> monthStart = ArgumentCaptor.forClass(LocalDate.class);
        verify(leaveRequestRepository).sumApprovedLeaveDaysInMonthGrouped(
                monthStart.capture(), any(LocalDate.class));
        assertEquals(YearMonth.from(monthStart.getValue()), plan.month());

        LeaveExpiryPlan.ExpiryEntry entry = plan.entries().get(0);
        assertEquals(EMPLOYEE_ID, entry.employeeId());
        assertEquals("John Test", entry.employeeName());
        assertEquals(LEAVE_TYPE_ID, entry.leaveTypeId());
        assertEquals("Annual Leave", entry.leaveTypeName());
        assertEquals(0, entry.approvedLeaveDaysInMonth(),
                "The preview must expose the approved-days figure that drives expiry - "
                        + "0 here is the signature of the stale-status bug");
        assertEquals(10, entry.remainingLeavesBefore());
        assertEquals(MONTHLY_GUIDELINE, entry.daysToExpire());
        assertEquals(8, entry.remainingLeavesAfter());

        // A preview is read-only.
        assertEquals(10, balance.getRemainingLeaves());
        assertEquals(0, balance.getExpiredLeaves());
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    @Test
    @DisplayName("Preview reports nothing to expire when the guideline was used")
    void previewReportsNothingWhenGuidelineWasUsed() {

        givenOneActiveEmployeeWithOnePaidBalance();
        givenApprovedLeaveDaysInMonth(MONTHLY_GUIDELINE);

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertFalse(plan.isSkipped());
        assertEquals(1, plan.consideredBalances(),
                "The balance is still considered, it simply needs no expiry");
        assertEquals(0, plan.affectedBalances());
        assertEquals(0, plan.totalDaysToExpire());
        assertTrue(plan.entries().isEmpty());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    @Test
    @DisplayName("Preview explains why a run would be skipped")
    void previewIsSkippedWhenCarryForwardIsEnabled() {

        LeaveSettings carryForwardOn = LeaveSettings.builder()
                .carryForwardAllowed(true)
                .monthlyGuideline(MONTHLY_GUIDELINE)
                .build();

        when(leaveSettingsService.getSettings()).thenReturn(carryForwardOn);

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertTrue(plan.isSkipped(),
                "With global carry-forward enabled nothing expires, and the plan must say so");
        assertNotNull(plan.skippedReason());
        assertTrue(plan.entries().isEmpty());
        assertEquals(0, plan.totalDaysToExpire());

        verifyNoInteractions(leaveBalanceRepository, leaveTransactionService);
    }

    @Test
    @DisplayName("Preview explains a settings failure instead of throwing")
    void previewIsSkippedWhenSettingsCannotBeLoaded() {

        when(leaveSettingsService.getSettings())
                .thenThrow(new IllegalStateException("no settings row"));

        LeaveExpiryPlan plan = leaveExpiryService.previewMonthlyUnused();

        assertTrue(plan.isSkipped());
        assertNotNull(plan.skippedReason());

        verifyNoInteractions(leaveBalanceRepository, leaveTransactionService);
    }
}
