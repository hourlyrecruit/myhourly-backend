package com.my_hourly.leave.service;

/**
 * Handles month-end leave expiry based on the monthly guideline.
 *
 * <p>Algorithm (per employee, per leave type where carryForwardAllowed = false):</p>
 * <pre>
 *   usedThisMonth    = sum of HR_APPROVED leave days in the current month
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
     */
    void expireMonthlyUnused();
}
