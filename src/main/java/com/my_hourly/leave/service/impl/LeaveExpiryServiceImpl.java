package com.my_hourly.leave.service.impl;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.LeaveExpiryPlan;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveExpiryServiceImpl implements LeaveExpiryService {

    private static final int DEFAULT_MONTHLY_GUIDELINE = 2;

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveTransactionService leaveTransactionService;
    private final LeaveSettingsService leaveSettingsService;

    /**
     * When true the month-end run only logs what it would expire and never
     * touches a balance. Defaults to false; flip it on in an environment to
     * audit a run before letting it deduct leave.
     */
    @Value("${leave.expiry.dry-run:false}")
    private boolean dryRun;

    /**
     * Month-end expiry algorithm:
     *
     * <pre>
     * For each active employee:
     *   For each active LeaveType where carryForwardAllowed = false:
     *     usedThisMonth   = APPROVED leave days in the expiring month
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

        ComputedExpiry computed = computePlan();
        LeaveExpiryPlan plan = computed.plan();

        // The plan is always logged before a single balance is touched, so a
        // suspicious run is visible in the log even when it then applies.
        logPlan(plan, dryRun);

        if (plan.isSkipped()) {
            return;
        }

        if (dryRun) {
            log.warn("Leave expiry DRY RUN for {} - no balance was changed. "
                            + "Set leave.expiry.dry-run=false to apply this plan.",
                    plan.month());
            return;
        }

        int expiredDays = 0;
        for (PlannedExpiry planned : computed.toApply()) {
            applyExpiry(planned);
            expiredDays += planned.entry().daysToExpire();
        }

        log.info("Leave expiry completed for month: {} | {} balance(s) updated, {} day(s) expired",
                plan.month(), computed.toApply().size(), expiredDays);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveExpiryPlan previewMonthlyUnused() {

        return computePlan().plan();
    }

    // -----------------------------------------------------------------------
    // Plan computation (no writes)
    // -----------------------------------------------------------------------

    /** A balance plus the entry describing the change it would receive. */
    private record PlannedExpiry(LeaveBalance balance, LeaveExpiryPlan.ExpiryEntry entry) {
    }

    /** The public plan and the writable pairs it was derived from. */
    private record ComputedExpiry(LeaveExpiryPlan plan, List<PlannedExpiry> toApply) {
    }

    private ComputedExpiry computePlan() {

        // The scheduler fires at 23:30 on the last day, so LocalDate.now()
        // is still the month we want to expire.
        LocalDate today = LocalDate.now();
        YearMonth expiringMonth = YearMonth.of(today.getYear(), today.getMonth());

        LocalDate monthStart = expiringMonth.atDay(1);
        LocalDate monthEnd = expiringMonth.atEndOfMonth();

        LeaveSettings settings;
        try {
            settings = leaveSettingsService.getSettings();
        } catch (Exception e) {
            log.error("Could not retrieve LeaveSettings. Aborting leave expiry process.", e);
            return skipped(expiringMonth, DEFAULT_MONTHLY_GUIDELINE, "LeaveSettings could not be loaded");
        }

        int monthlyGuideline = settings.getMonthlyGuideline() != null
                ? settings.getMonthlyGuideline()
                : DEFAULT_MONTHLY_GUIDELINE;

        // Carry Forward = ON (carryForwardAllowed = true) -> No expiry
        if (Boolean.TRUE.equals(settings.getCarryForwardAllowed())) {
            return skipped(expiringMonth, monthlyGuideline, "Global carry-forward is enabled");
        }

        List<Employee> employees = employeeRepository.findByActiveTrue();
        List<LeaveType> leaveTypes = leaveTypeRepository.findByActiveTrue();

        // Load all balances for the year and all approved days used in the month
        // in two queries, replacing the per-employee / per-leave-type N+1 reads.
        Map<Long, Map<Long, LeaveBalance>> balancesByEmployeeAndType = new HashMap<>();
        for (LeaveBalance balance : leaveBalanceRepository.findByYear(expiringMonth.getYear())) {
            balancesByEmployeeAndType
                    .computeIfAbsent(balance.getEmployee().getId(), k -> new HashMap<>())
                    .put(balance.getLeaveType().getId(), balance);
        }

        Map<Long, Map<Long, Integer>> usedDaysByEmployeeAndType = new HashMap<>();
        for (LeaveRequestRepository.UsedDaysProjection used :
                leaveRequestRepository.sumApprovedLeaveDaysInMonthGrouped(monthStart, monthEnd)) {
            usedDaysByEmployeeAndType
                    .computeIfAbsent(used.getEmployeeId(), k -> new HashMap<>())
                    .put(used.getLeaveTypeId(), used.getTotalDays().intValue());
        }

        List<LeaveExpiryPlan.ExpiryEntry> entries = new ArrayList<>();
        List<PlannedExpiry> toApply = new ArrayList<>();
        int consideredBalances = 0;

        for (Employee employee : employees) {
            for (LeaveType leaveType : leaveTypes) {

                // Apply expiry logic to paid leave types
                if (!Boolean.TRUE.equals(leaveType.getPaid())) {
                    continue;
                }

                Map<Long, LeaveBalance> employeeBalances =
                        balancesByEmployeeAndType.get(employee.getId());
                if (employeeBalances == null) {
                    continue;
                }

                LeaveBalance balance = employeeBalances.get(leaveType.getId());
                if (balance == null) {
                    continue;
                }

                consideredBalances++;

                int usedThisMonth = usedDaysByEmployeeAndType
                        .getOrDefault(employee.getId(), Map.of())
                        .getOrDefault(leaveType.getId(), 0);

                int daysToExpire = daysToExpire(balance, usedThisMonth, monthlyGuideline);

                if (daysToExpire <= 0) {
                    log.debug("No expiry for employee {} leaveType {} month {}: used {} of {} guideline "
                                    + "day(s), {} day(s) remaining",
                            employee.getId(), leaveType.getName(), expiringMonth.getMonth(),
                            usedThisMonth, monthlyGuideline, balance.getRemainingLeaves());
                    continue;
                }

                LeaveExpiryPlan.ExpiryEntry entry = new LeaveExpiryPlan.ExpiryEntry(
                        employee.getId(),
                        employee.getEmployeeCode(),
                        employeeName(employee),
                        leaveType.getId(),
                        leaveType.getName(),
                        usedThisMonth,
                        balance.getRemainingLeaves(),
                        daysToExpire
                );

                entries.add(entry);
                toApply.add(new PlannedExpiry(balance, entry));
            }
        }

        return new ComputedExpiry(
                new LeaveExpiryPlan(
                        expiringMonth,
                        monthlyGuideline,
                        consideredBalances,
                        List.copyOf(entries),
                        null
                ),
                List.copyOf(toApply)
        );
    }

    /**
     * Unused guideline days for a balance, clamped to what is actually
     * remaining. Zero means "nothing to expire".
     */
    private int daysToExpire(LeaveBalance balance, int usedThisMonth, int monthlyGuideline) {

        if (balance.getRemainingLeaves() <= 0) {
            // Nothing left to expire
            return 0;
        }

        // Unused = guideline - used, clamped to 0 (cannot be negative)
        int unusedGuideline = Math.max(0, monthlyGuideline - usedThisMonth);

        // Expire cannot exceed what is actually remaining
        return Math.min(unusedGuideline, balance.getRemainingLeaves());
    }

    // -----------------------------------------------------------------------
    // Apply (writes)
    // -----------------------------------------------------------------------

    private void applyExpiry(PlannedExpiry planned) {

        LeaveBalance balance = planned.balance();
        LeaveExpiryPlan.ExpiryEntry entry = planned.entry();

        int balanceBefore = balance.getRemainingLeaves();

        balance.setExpiredLeaves(balance.getExpiredLeaves() + entry.daysToExpire());
        balance.setRemainingLeaves(balance.getRemainingLeaves() - entry.daysToExpire());

        leaveBalanceRepository.save(balance);

        leaveTransactionService.createExpiryTransaction(balance, entry.daysToExpire());

        log.info("Expired {} day(s) for employee {} leaveType {} | balance: {} -> {}",
                entry.daysToExpire(), entry.employeeId(), entry.leaveTypeName(),
                balanceBefore, balance.getRemainingLeaves());
    }

    // -----------------------------------------------------------------------
    // Reporting helpers
    // -----------------------------------------------------------------------

    /**
     * Logs the plan that is about to be applied.
     *
     * @param verbose true to log every affected balance, not just the summary
     *                (used for dry runs, where the detail is the whole point)
     */
    private void logPlan(LeaveExpiryPlan plan, boolean verbose) {

        if (plan.isSkipped()) {
            log.info("Leave expiry skipped for month: {} ({})", plan.month(), plan.skippedReason());
            return;
        }

        log.info("Leave expiry plan for month: {} | guideline {} day(s)/month | "
                        + "{} of {} eligible balance(s) affected, {} day(s) would expire",
                plan.month(), plan.monthlyGuideline(),
                plan.affectedBalances(), plan.consideredBalances(), plan.totalDaysToExpire());

        for (LeaveExpiryPlan.ExpiryEntry entry : plan.entries()) {
            logEntry(entry, plan.monthlyGuideline(), verbose);
        }
    }

    private void logEntry(LeaveExpiryPlan.ExpiryEntry entry, int monthlyGuideline, boolean verbose) {

        String detail = "Leave expiry plan: employee {} {} leaveType {} | approved this month: {} of "
                + "{} guideline day(s) | remaining: {} -> {}";

        if (verbose) {
            log.info(detail,
                    entry.employeeId(), entry.employeeName(), entry.leaveTypeName(),
                    entry.approvedLeaveDaysInMonth(), monthlyGuideline,
                    entry.remainingLeavesBefore(), entry.remainingLeavesAfter());
        } else {
            log.debug(detail,
                    entry.employeeId(), entry.employeeName(), entry.leaveTypeName(),
                    entry.approvedLeaveDaysInMonth(), monthlyGuideline,
                    entry.remainingLeavesBefore(), entry.remainingLeavesAfter());
        }
    }

    private static ComputedExpiry skipped(YearMonth month, int monthlyGuideline, String reason) {

        return new ComputedExpiry(
                LeaveExpiryPlan.skipped(month, monthlyGuideline, reason),
                List.of()
        );
    }

    private static String employeeName(Employee employee) {

        String lastName = employee.getLastName();

        return lastName == null || lastName.isBlank()
                ? employee.getFirstName()
                : employee.getFirstName() + " " + lastName;
    }
}
