package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.dto.PaidLopAllocation;
import com.my_hourly.leave.entity.LeaveType;

import java.time.LocalDate;
<<<<<<< HEAD
=======
import java.util.Set;
>>>>>>> feature/sandwich-leave

/**
 * Splits a leave request's days into PAID and LOP before any balance is touched.
 *
 * <p>The monthly paid-leave guideline lives in LeaveSettings ({@code
 * monthly_guideline}); it is the maximum number of eligible PAID days an
 * employee can take per calendar month. It is an allowance for the month, NOT a
 * credit into the annual balance.</p>
 */
public interface LeavePaidLopService {

    /**
     * Classifies every working day of {@code [startDate, endDate]}.
     *
     * <p>Days are walked in date order and per calendar month, so a request that
     * crosses a month boundary uses each month's own remaining allowance. A day
     * is PAID only while the month still has guideline allowance AND the annual
     * balance can cover it; everything else is LOP.</p>
     *
     * <p>This method performs reads only - it never writes a balance. The caller
     * applies the {@code paidDays} it returns.</p>
     *
     * @throws com.my_hourly.common.exception.ResourceNotFoundException if no
     *         annual balance exists for the employee / leave type / year
     */
    PaidLopAllocation classify(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate);
<<<<<<< HEAD
=======

    /**
     * Classifies every working day of {@code [startDate, endDate]}, including
     * forced working days (sandwich leave weekends).
     *
     * <p>Forced working days are counted as working days even if they fall on
     * weekends. They are typically used for sandwich leave policies where weekend
     * days adjacent to leave days are automatically charged.</p>
     *
     * <p>Days are walked in date order and per calendar month, so a request that
     * crosses a month boundary uses each month's own remaining allowance. A day
     * is PAID only while the month still has guideline allowance AND the annual
     * balance can cover it; everything else is LOP.</p>
     *
     * <p>This method performs reads only - it never writes a balance. The caller
     * applies the {@code paidDays} it returns.</p>
     *
     * @param forcedWorkingDays dates that must be counted as working days even if
     *                         they fall on weekends (e.g., sandwich leave weekends)
     * @throws com.my_hourly.common.exception.ResourceNotFoundException if no
     *         annual balance exists for the employee / leave type / year
     */
    PaidLopAllocation classify(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            Set<LocalDate> forcedWorkingDays);
>>>>>>> feature/sandwich-leave
}
