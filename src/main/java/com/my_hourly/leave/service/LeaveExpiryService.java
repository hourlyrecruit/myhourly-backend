package com.my_hourly.leave.service;

import com.my_hourly.leave.dto.LeaveExpiryPlan;

/**
 * Month-end review of the monthly paid-leave allowance.
 *
 * <p>Algorithm (per employee, when carryForwardAllowed = false):</p>
 * <pre>
 *   paidThisMonth   = APPROVED PAID days attributed to the expiring month by
 *                     their actual dates (all paid leave types together)
 *   unusedGuideline = max(0, monthlyGuideline - paidThisMonth)
 * </pre>
 *
 * <p>The unused allowance lapses with the calendar month by construction -
 * the next month recomputes it from LeaveSettings - so this service performs
 * NO balance writes: nothing is deducted from the annual balance, no
 * expiredLeaves counter is incremented, no ledger transaction is written, and
 * a repeated run cannot duplicate anything. The plan is report-only.</p>
 *
 * <p>If carryForwardAllowed = true, the run is skipped entirely and the
 * unused allowance is retained by the carry-forward policy.</p>
 */
public interface LeaveExpiryService {

    /**
     * Runs the month-end allowance review for all active employees.
     * Typically invoked by the scheduler on the last day of every month.
     *
     * <p>Logs the full report. Never modifies a balance - running it once,
     * twice or a hundred times has exactly the same (null) effect on data.</p>
     */
    void expireMonthlyUnused();

    /**
     * Computes exactly what {@link #expireMonthlyUnused()} reports, without
     * touching any balance.
     *
     * <p>Use this to audit a month in a real environment — in particular the
     * {@code paidLeaveDaysInMonth} of each entry, which is what decides how
     * much allowance went unused. If it is unexpectedly 0 for employees who
     * did take approved PAID leave, the usage query is not matching the rows
     * it should.</p>
     *
     * @return the plan; {@link LeaveExpiryPlan#isSkipped()} is true when the run
     *         would report nothing (e.g. carry-forward is enabled globally)
     */
    LeaveExpiryPlan previewMonthlyUnused();
}
