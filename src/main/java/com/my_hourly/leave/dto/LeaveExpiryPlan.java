package com.my_hourly.leave.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * Read-only description of what the month-end leave expiry run would do.
 *
 * <p>Produced by {@code LeaveExpiryService#previewMonthlyUnused()} and logged
 * by the scheduled run <em>before</em> any balance is modified, so a run can be
 * audited (or a regression spotted) without any leave being deducted.</p>
 *
 * <p>All values are primitives/copies detached from the persistence context, so
 * a plan can be safely logged, returned or asserted on after the transaction
 * that produced it has ended.</p>
 *
 * @param month             the year-month being expired
 * @param monthlyGuideline  guideline days per month used for the calculation
 * @param consideredBalances employee x paid-leave-type combinations actually evaluated
 * @param entries           one entry per balance that would have days expired
 * @param skippedReason     non-null when the run was skipped (e.g. global carry-forward enabled)
 */
public record LeaveExpiryPlan(
        YearMonth month,
        int monthlyGuideline,
        int consideredBalances,
        List<ExpiryEntry> entries,
        String skippedReason
) {

    /**
     * A single balance that would have unused guideline days expired.
     *
     * @param approvedLeaveDaysInMonth days of APPROVED leave counted for this employee,
     *                                 leave type and month - the value that decides expiry
     * @param daysToExpire             guideline days that would be deducted
     */
    public record ExpiryEntry(
            Long employeeId,
            String employeeCode,
            String employeeName,
            Long leaveTypeId,
            String leaveTypeName,
            int approvedLeaveDaysInMonth,
            int remainingLeavesBefore,
            int daysToExpire
    ) {

        public int remainingLeavesAfter() {
            return remainingLeavesBefore - daysToExpire;
        }
    }

    /**
     * A run that was abandoned before any calculation (missing settings, global
     * carry-forward, ...). Carries the reason so the log explains itself.
     */
    public static LeaveExpiryPlan skipped(YearMonth month, int monthlyGuideline, String reason) {

        return new LeaveExpiryPlan(month, monthlyGuideline, 0, List.of(), reason);
    }

    public boolean isSkipped() {
        return skippedReason != null;
    }

    public int affectedBalances() {
        return entries.size();
    }

    public int totalDaysToExpire() {
        return entries.stream().mapToInt(ExpiryEntry::daysToExpire).sum();
    }
}
