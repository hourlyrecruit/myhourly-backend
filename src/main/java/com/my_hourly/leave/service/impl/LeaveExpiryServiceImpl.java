package com.my_hourly.leave.service.impl;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveExpiryService;
import com.my_hourly.leave.service.LeaveTransactionService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveExpiryServiceImpl implements LeaveExpiryService {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTransactionService leaveTransactionService;
    private final LeaveSettingsService leaveSettingsService;

    /**
     * Month-end expiry algorithm:
     *
     * <pre>
     * For each active employee:
     *   For each active LeaveType where carryForwardAllowed = false:
     *     usedThisMonth   = HR_APPROVED leave days in the expiring month
     *     unusedGuideline = max(0, monthlyGuideline - usedThisMonth)
     *     if unusedGuideline > 0:
     *       expiredLeaves   += unusedGuideline
     *       remainingLeaves -= unusedGuideline
     * </pre>
     *
     * Runs on the last day of the month so the "current month" is still the
     * month being expired.
     */
    @Override
    @Transactional
    public void expireMonthlyUnused() {

        // The scheduler fires at 23:30 on the last day, so LocalDate.now()
        // is still the month we want to expire.
        LocalDate today = LocalDate.now();
        YearMonth expiringMonth = YearMonth.of(today.getYear(), today.getMonth());

        LocalDate monthStart = expiringMonth.atDay(1);
        LocalDate monthEnd   = expiringMonth.atEndOfMonth();

        log.info("Leave expiry started for month: {}", expiringMonth);

        LeaveSettings settings;
        try {
            settings = leaveSettingsService.getSettings();
        } catch (Exception e) {
            log.error("Could not retrieve LeaveSettings. Aborting leave expiry process.", e);
            return;
        }

        // Carry Forward = ON (carryForwardAllowed = true) -> No expiry
        if (Boolean.TRUE.equals(settings.getCarryForwardAllowed())) {
            log.info("Carry Forward is enabled globally. No leaves will be expired.");
            return;
        }

        int monthlyGuideline = settings.getMonthlyGuideline() != null ? settings.getMonthlyGuideline() : 2;

        List<Employee> employees = employeeRepository.findByActiveTrue();
        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        for (Employee employee : employees) {
            for (LeaveType leaveType : leaveTypes) {
                // Apply expiry logic to paid leave types
                if (Boolean.TRUE.equals(leaveType.getPaid())) {
                    processExpiry(employee, leaveType, monthStart, monthEnd, expiringMonth, monthlyGuideline);
                }
            }
        }

        log.info("Leave expiry completed for month: {}", expiringMonth);
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private void processExpiry(Employee employee,
                                LeaveType leaveType,
                                LocalDate monthStart,
                                LocalDate monthEnd,
                                YearMonth expiringMonth,
                                int monthlyGuideline) {

        // Fetch the annual balance for this year
        leaveBalanceRepository
                .findByEmployeeAndLeaveTypeAndYear(
                        employee,
                        leaveType,
                        expiringMonth.getYear())
                .ifPresent(balance -> expireUnused(
                        balance,
                        leaveType,
                        employee,
                        monthStart,
                        monthEnd,
                        monthlyGuideline));
    }

    private void expireUnused(LeaveBalance balance,
                               LeaveType leaveType,
                               Employee employee,
                               LocalDate monthStart,
                               LocalDate monthEnd,
                               int monthlyGuideline) {

        if (balance.getRemainingLeaves() <= 0) {
            // Nothing left to expire
            return;
        }

        // How many approved leave days did this employee use in the expiring month?
        Integer usedThisMonth = leaveRequestRepository.sumApprovedLeaveDaysInMonth(
                employee,
                leaveType,
                monthStart,
                monthEnd);

        if (usedThisMonth == null) {
            usedThisMonth = 0;
        }

        // Unused = guideline - used, clamped to 0 (cannot be negative)
        int unusedGuideline = Math.max(0, monthlyGuideline - usedThisMonth);

        if (unusedGuideline <= 0) {
            log.debug("No expiry for employee {} leaveType {} month {}: used {} >= guideline {}",
                    employee.getId(), leaveType.getName(), monthStart.getMonth(),
                    usedThisMonth, monthlyGuideline);
            return;
        }

        // Expire cannot exceed what is actually remaining
        int daysToExpire = Math.min(unusedGuideline, balance.getRemainingLeaves());

        int balanceBefore = balance.getRemainingLeaves();

        balance.setExpiredLeaves(balance.getExpiredLeaves() + daysToExpire);
        balance.setRemainingLeaves(balance.getRemainingLeaves() - daysToExpire);

        leaveBalanceRepository.save(balance);

        leaveTransactionService.createExpiryTransaction(balance, daysToExpire);

        log.info("Expired {} day(s) for employee {} leaveType {} | balance: {} → {}",
                daysToExpire, employee.getId(), leaveType.getName(),
                balanceBefore, balance.getRemainingLeaves());
    }
}
