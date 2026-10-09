package com.my_hourly.leave.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * Read-only report of the month-end leave allowance review.
 *
 * <p>Produced by {@code LeaveExpiryService#previewMonthlyUnused()} and logged
 * by the scheduled run. Unused monthly-guideline days simply expire when the
 * calendar month turns - the next month's allowance is recomputed from
 * LeaveSettings - so this plan NEVER modifies an annual leave balance. It
 * exists purely as an audit trail: what went unused, and how much PAID leave
 * was attributed to the month by actual date.</p>
 *
 * <p>All values are primitives/copies detached from the persistence context, so
 * a plan can be safely logged, returned or asserted on after the transaction
 * that produced it has ended.</p>
 *
 * @param month               the year-month being reported
 * @param monthlyGuideline    guideline days per employee per month used for the calculation
 * @param consideredEmployees employees evaluated (active, with a leave balance this year)
 * @param entries             one entry per employee with unused guideline days
 * @param skippedReason       non-null when the run was skipped (e.g. global carry-forward enabled)
 */
public record LeaveExpiryPlan(
        YearMonth month,
        int monthlyGuideline,
        int consideredEmployees,
        List<ExpiryEntry> entries,
        String skippedReason
) {

    /**
     * One employee's unused monthly allowance for the month.
     *
     * @param paidLeaveDaysInMonth  APPROVED PAID days attributed to this month
     *                              by their actual dates, across all paid leave types
     * @param unusedGuidelineDays   {@code max(0, guideline - paidLeaveDaysInMonth)} -
     *                              days of the allowance that went unused and
     *                              will NOT carry into the next month
     */
    public record ExpiryEntry(
            Long employeeId,
            String employeeCode,
            String employeeName,
            int paidLeaveDaysInMonth,
            int unusedGuidelineDays
    ) {
    }

    /**
     * A run that was abandoned before any calculation (missing settings,
     * global carry-forward, ...). Carries the reason so the log explains
     * itself.
     */
    public static LeaveExpiryPlan skipped(YearMonth month, int monthlyGuideline, String reason) {
        return new LeaveExpiryPlan(month, monthlyGuideline, 0, List.of(), reason);
    }

    public boolean isSkipped() {
        return skippedReason != null;
    }

    public int affectedEmployees() {
        return entries.size();
    }

    public int totalUnusedDays() {
        return entries.stream().mapToInt(ExpiryEntry::unusedGuidelineDays).sum();
    }
}
