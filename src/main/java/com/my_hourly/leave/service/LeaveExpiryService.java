package com.my_hourly.leave.service;

import com.my_hourly.leave.dto.LeaveExpiryPlan;

/**
 * Handles month-end leave expiry based on the monthly guideline.
 *
 * <p>Algorithm (per employee, per leave type where carryForwardAllowed = false):</p>
 * <pre>
 *   usedThisMonth    = sum of APPROVED leave days in the current month
 *   unusedGuideline  = max(0, monthlyGuideline - usedThisMonth)
 *   if unusedGuideline > 0:
 *     expiredLeaves  += unusedGuideline
 *     remainingLeaves -= unusedGuideline
 * </pre>
 *
 * <p>If carryForwardAllowed = true, this service does nothing — unused guideline
 * rolls over into the remaining annual balance.</p>
 */
public interface LeaveExpiryService {

    /**
     * Runs the month-end expiry calculation for all active employees.
     * Typically invoked by the scheduler on the last day of every month.
     *
     * <p>The full plan is logged before anything is modified. Setting
     * {@code leave.expiry.dry-run=true} makes this method log the plan and stop,
     * leaving every balance untouched.</p>
     */
    void expireMonthlyUnused();

    /**
     * Computes exactly what {@link #expireMonthlyUnused()} would do, without
     * modifying a single balance.
     *
     * <p>Use this to audit a run in a real environment — in particular the
     * {@code approvedLeaveDaysInMonth} of each entry, which is what decides
     * whether days expire. If it is unexpectedly 0 for employees who did take
     * approved leave, the usage query is not matching the rows it should.</p>
     *
     * @return the plan; {@link LeaveExpiryPlan#isSkipped()} is true when the run
     *         would not do anything (e.g. carry-forward is enabled globally)
     */
    LeaveExpiryPlan previewMonthlyUnused();
}
