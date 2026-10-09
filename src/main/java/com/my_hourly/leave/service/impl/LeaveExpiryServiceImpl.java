package com.my_hourly.leave.service.impl;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.LeaveExpiryPlan;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveExpiryService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Month-end monthly-allowance review.
 *
 * <p>The monthly paid-leave guideline is an allowance per employee per
 * calendar month, recomputed from LeaveSettings as
 * {@code guideline - PAID days attributed to that month}. When the calendar
 * month turns, unused allowance simply lapses: the next month starts fresh.
 * It is never carried into the annual balance and never deducted from it - so
 * this service performs NO balance writes at all, and a run repeated any
 * number of times cannot duplicate anything.</p>
 *
 * <p>This run exists purely for auditing:</p>
 * <pre>
 *   paidThisMonth = APPROVED PAID days attributed to the expiring month by
 *                   their actual dates (a request spanning months contributes
 *                   only the days that fall in this month)
 *   unused        = max(0, monthlyGuideline - paidThisMonth)
 * </pre>
 *
 * <p>PAID days still leave the annual balance only when leave is approved;
 * expiry does not undo those deductions and LOP days never touched the
 * balance in the first place.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveExpiryServiceImpl implements LeaveExpiryService {

    private static final int DEFAULT_MONTHLY_GUIDELINE = 2;

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveSettingsService leaveSettingsService;

    @Override
    @Transactional
    public void expireMonthlyUnused() {

        LeaveExpiryPlan plan = computePlan();

        logPlan(plan);

        if (plan.isSkipped()) {
            return;
        }

        // Report only. The unused allowance lapses with the calendar month by
        // construction (the next month recomputes it from LeaveSettings), so
        // there is deliberately no balance deduction, no expiredLeaves increment
        // and no ledger transaction - which also makes a repeated run a no-op.
        log.info("Leave allowance review for month: {} | {} of {} employee(s) left {} guideline day(s) "
                        + "unused - the allowance resets with the new month; no annual balance was modified.",
                plan.month(), plan.affectedEmployees(), plan.consideredEmployees(),
                plan.totalUnusedDays());
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveExpiryPlan previewMonthlyUnused() {
        return computePlan();
    }

    // -----------------------------------------------------------------------
    // Plan computation (no writes)
    // -----------------------------------------------------------------------

    private LeaveExpiryPlan computePlan() {

        // The scheduler fires at 23:30 on the last day, so LocalDate.now()
        // is still the month being reported.
        LocalDate today = LocalDate.now();
        YearMonth expiringMonth = YearMonth.of(today.getYear(), today.getMonth());

        LocalDate monthStart = expiringMonth.atDay(1);
        LocalDate monthEnd = expiringMonth.atEndOfMonth();

        LeaveSettings settings;
        try {
            settings = leaveSettingsService.getSettings();
        } catch (Exception e) {
            log.error("Could not retrieve LeaveSettings. Aborting leave allowance review.", e);
            return skipped(expiringMonth, DEFAULT_MONTHLY_GUIDELINE, "LeaveSettings could not be loaded");
        }

        int monthlyGuideline = settings.getMonthlyGuideline() != null
                ? settings.getMonthlyGuideline()
                : DEFAULT_MONTHLY_GUIDELINE;

        // Carry Forward = ON (carryForwardAllowed = true) -> nothing is
        // reported as lapsing; the unused allowance is retained by policy.
        if (Boolean.TRUE.equals(settings.getCarryForwardAllowed())) {
            return skipped(expiringMonth, monthlyGuideline, "Global carry-forward is enabled");
        }

        List<Employee> employees = employeeRepository.findByActiveTrue();

        // Only employees with a leave balance this year are considered - an
        // employee outside the leave scheme has no allowance to lapse.
        Map<Long, Boolean> hasBalanceByEmployee = new HashMap<>();
        for (LeaveBalance balance : leaveBalanceRepository.findByYear(expiringMonth.getYear())) {
            hasBalanceByEmployee.put(balance.getEmployee().getId(), Boolean.TRUE);
        }

        // PAID days per employee, attributed to the expiring month by their
        // actual dates (allocation rows + legacy fallback, merged by the
        // repository's default method).
        Map<Long, Integer> paidByEmployee = new HashMap<>();
        for (LeaveRequestRepository.PaidDaysProjection paid
                : leaveRequestRepository.sumPaidLeaveDaysInMonthGrouped(monthStart, monthEnd)) {
            if (paid.getEmployeeId() != null && paid.getPaidDays() != null) {
                paidByEmployee.merge(paid.getEmployeeId(), paid.getPaidDays(), Integer::sum);
            }
        }

        List<LeaveExpiryPlan.ExpiryEntry> entries = new ArrayList<>();
        int considered = 0;

        for (Employee employee : employees) {

            if (!Boolean.TRUE.equals(hasBalanceByEmployee.get(employee.getId()))) {
                continue;
            }

            considered++;

            int paidThisMonth = paidByEmployee.getOrDefault(employee.getId(), 0);
            int unused = Math.max(0, monthlyGuideline - paidThisMonth);

            if (unused <= 0) {
                log.debug("No unused allowance for employee {} month {}: {} of {} guideline day(s) used",
                        employee.getId(), expiringMonth.getMonth(),
                        paidThisMonth, monthlyGuideline);
                continue;
            }

            entries.add(new LeaveExpiryPlan.ExpiryEntry(
                    employee.getId(),
                    employee.getEmployeeCode(),
                    employeeName(employee),
                    paidThisMonth,
                    unused
            ));
        }

        return new LeaveExpiryPlan(
                expiringMonth,
                monthlyGuideline,
                considered,
                List.copyOf(entries),
                null
        );
    }

    // -----------------------------------------------------------------------
    // Reporting helpers
    // -----------------------------------------------------------------------

    /**
     * Logs the plan. Purely informational - no balance is ever modified.
     */
    private void logPlan(LeaveExpiryPlan plan) {

        if (plan.isSkipped()) {
            log.info("Leave allowance review skipped for month: {} ({})",
                    plan.month(), plan.skippedReason());
            return;
        }

        log.info("Leave allowance report for month: {} | guideline {} day(s)/employee | "
                        + "{} of {} employee(s) with unused allowance, {} day(s) lapsed with the month",
                plan.month(), plan.monthlyGuideline(),
                plan.affectedEmployees(), plan.consideredEmployees(), plan.totalUnusedDays());

        for (LeaveExpiryPlan.ExpiryEntry entry : plan.entries()) {
            log.debug("Leave allowance report: employee {} {} | PAID this month: {} of {} "
                            + "guideline day(s) | unused: {} day(s) (lapsing, not deducted from any balance)",
                    entry.employeeId(), entry.employeeName(),
                    entry.paidLeaveDaysInMonth(), plan.monthlyGuideline(),
                    entry.unusedGuidelineDays());
        }
    }

    private static LeaveExpiryPlan skipped(YearMonth month, int monthlyGuideline, String reason) {

        return LeaveExpiryPlan.skipped(month, monthlyGuideline, reason);
    }

    private static String employeeName(Employee employee) {

        String lastName = employee.getLastName();
        return lastName == null || lastName.isBlank()
                ? employee.getFirstName()
                : employee.getFirstName() + " " + lastName;
    }
}
