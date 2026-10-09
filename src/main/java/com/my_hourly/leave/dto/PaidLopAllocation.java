package com.my_hourly.leave.dto;

import java.time.YearMonth;
import java.util.List;

/**
 * The PAID / LOP split of a leave request, computed by
 * {@code LeavePaidLopService} before anything is persisted.
 *
 * <p>A request is walked one working day at a time, in date order. Each day is
 * PAID while both hold:</p>
 * <ul>
 *   <li>the calendar month still has monthly-guideline allowance left, and</li>
 *   <li>the employee's annual balance for that day's year still has days left.</li>
 * </ul>
 * <p>Every other day is LOP. LOP days are never deducted from the annual
 * balance, and the annual balance can never go negative because a day is only
 * PAID when the balance can cover it.</p>
 *
 * <p>Detached value object: safe to log, return or assert on after the
 * transaction that produced it has closed.</p>
 *
 * @param paidDays days to deduct from the annual balance
 * @param lopDays  days that are not paid (over the monthly allowance / balance)
 * @param months   the per-calendar-month breakdown the split was derived from
 */
public record PaidLopAllocation(
        int paidDays,
        int lopDays,
        List<MonthAllocation> months
) {

    /**
     * How one calendar month within the request was classified.
     *
     * @param month               the calendar month
     * @param workingDays         working days of the request that fall in this month
     * @param allowanceBeforeThis how much of the monthly guideline was still
     *                            available when this month was processed
     * @param paidDays            days of this month classified as PAID
     * @param lopDays             days of this month classified as LOP
     */
    public record MonthAllocation(
            YearMonth month,
            int workingDays,
            int allowanceBeforeThis,
            int paidDays,
            int lopDays
    ) {
    }

    public int totalDays() {
        return paidDays + lopDays;
    }

    public static PaidLopAllocation of(int paidDays, int lopDays) {
        return new PaidLopAllocation(paidDays, lopDays, List.of());
    }
}
