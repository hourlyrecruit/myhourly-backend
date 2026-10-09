package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.enums.LeaveTransactionType;
import com.my_hourly.leave.mapper.LeaveBalanceMapper;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.service.LeaveTransactionService;
import com.my_hourly.leave.service.impl.LeaveBalanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Pins the annual-balance side of leave approval: only PAID days are deducted,
 * LOP days never touch the balance, and the balance can never go negative.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PAID-day deduction from the annual balance")
class LeaveBalanceServicePaidDeductionTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveBalanceMapper leaveBalanceMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private LeaveTransactionService leaveTransactionService;

    @InjectMocks
    private LeaveBalanceServiceImpl leaveBalanceService;

    private Employee employee;
    private LeaveBalance balance;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {

        employee = Employee.builder().firstName("John").lastName("Test").build();
        employee.setId(1L);

        LeaveType leaveType = LeaveType.builder()
                .name("Annual Leave")
                .paid(true)
                .allocatedDays(24)
                .active(true)
                .build();
        leaveType.setId(10L);

        balance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(2026)
                .allocatedLeaves(24)
                .usedLeaves(12)
                .expiredLeaves(0)
                .remainingLeaves(12)
                .build();

        leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(LocalDate.of(2026, 10, 5))
                .endDate(LocalDate.of(2026, 10, 8))
                .totalDays(4)
                .reason("Personal")
                .status(LeaveStatus.APPROVED)
                .build();
        leaveRequest.setId(100L);
    }

    @Test
    @DisplayName("Only the PAID days are deducted and recorded")
    void onlyPaidDaysAreDeducted() {

        // 4-day request: 2 PAID, 2 LOP. Only 2 may hit the balance.
        leaveBalanceService.deductPaidLeaveDays(balance, leaveRequest, 2);

        assertEquals(10, balance.getRemainingLeaves(),
                "The balance must drop by the PAID days only, not the whole request");
        assertEquals(14, balance.getUsedLeaves());

        verify(leaveBalanceRepository).save(balance);
        verify(leaveTransactionService).createTransaction(
                eq(balance),
                eq(leaveRequest),
                eq(LeaveTransactionType.LEAVE_APPROVED),
                eq(2),
                eq(12),
                eq(10),
                any(String.class));
    }

    @Test
    @DisplayName("An entirely LOP request does not touch the balance at all")
    void entirelyLopRequestDoesNotTouchTheBalance() {

        leaveBalanceService.deductPaidLeaveDays(balance, leaveRequest, 0);

        assertEquals(12, balance.getRemainingLeaves(),
                "LOP days are never deducted from the annual balance");
        assertEquals(12, balance.getUsedLeaves());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }

    @Test
    @DisplayName("A deduction larger than the balance is clamped so it cannot go negative")
    void deductionIsClampedToTheAvailableBalance() {

        leaveBalanceService.deductPaidLeaveDays(balance, leaveRequest, 20);

        assertEquals(0, balance.getRemainingLeaves(),
                "The annual balance must never go negative");
        assertEquals(24, balance.getUsedLeaves());

        verify(leaveTransactionService).createTransaction(
                eq(balance),
                eq(leaveRequest),
                eq(LeaveTransactionType.LEAVE_APPROVED),
                eq(12),
                eq(12),
                eq(0),
                any(String.class));
    }

    // -----------------------------------------------------------------------
    // Legacy full-deduction path (unpaid leave types)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("The legacy full deduction deducts every day when the balance covers it")
    void legacyFullDeductionDeductsEveryDay() {

        // 4-day request, 12 days remaining: legacy behaviour, unchanged.
        leaveBalanceService.deductLeaveBalance(balance, leaveRequest);

        assertEquals(8, balance.getRemainingLeaves());
        assertEquals(16, balance.getUsedLeaves());

        verify(leaveBalanceRepository).save(balance);
        verify(leaveTransactionService).createTransaction(
                eq(balance),
                eq(leaveRequest),
                eq(LeaveTransactionType.LEAVE_APPROVED),
                eq(4),
                eq(12),
                eq(8),
                any(String.class));
    }

    @Test
    @DisplayName("The legacy full deduction is clamped so an over-balance request cannot go negative")
    void legacyFullDeductionIsClampedToTheBalance() {

        // Submission no longer blocks over-balance requests, so an unpaid
        // leave type can reach approval with less balance than totalDays.
        balance.setUsedLeaves(22);
        balance.setRemainingLeaves(2);

        leaveBalanceService.deductLeaveBalance(balance, leaveRequest); // totalDays = 4

        assertEquals(0, balance.getRemainingLeaves(),
                "The annual balance must never go negative for any leave type");
        assertEquals(24, balance.getUsedLeaves());

        verify(leaveTransactionService).createTransaction(
                eq(balance),
                eq(leaveRequest),
                eq(LeaveTransactionType.LEAVE_APPROVED),
                eq(2),
                eq(2),
                eq(0),
                any(String.class));
    }

    @Test
    @DisplayName("An exhausted balance deducts nothing and writes nothing")
    void exhaustedBalanceDeductionWritesNothing() {

        balance.setUsedLeaves(24);
        balance.setRemainingLeaves(0);

        leaveBalanceService.deductLeaveBalance(balance, leaveRequest);

        assertEquals(0, balance.getRemainingLeaves());
        assertEquals(24, balance.getUsedLeaves());

        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
        verifyNoInteractions(leaveTransactionService);
    }
}
