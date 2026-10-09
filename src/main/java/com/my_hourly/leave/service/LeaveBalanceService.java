package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;

import java.time.LocalDate;
import java.util.List;

public interface LeaveBalanceService {

    /**
     * Returns the annual leave balance for an employee and leave type in the year
     * of the given date.
     */
    LeaveBalance getLeaveBalanceEntity(
            Employee employee,
            LeaveType leaveType,
            LocalDate date);

    /**
     * Same lookup as {@link #getLeaveBalanceEntity} but takes a
     * {@code SELECT ... FOR UPDATE} row lock.
     *
     * <p>Leave approval reads the balance, the month's already-approved PAID
     * days and then writes the balance. Locking the balance row first serialises
     * concurrent approvals for the same employee / leave type / year, so the
     * monthly allowance and the annual balance cannot be over-allocated.</p>
     */
    LeaveBalance getLeaveBalanceEntityForUpdate(
            Employee employee,
            LeaveType leaveType,
            LocalDate date);

    LeaveBalanceResponse getLeaveBalance(
            Long leaveBalanceId);

    List<LeaveBalanceResponse> getMyLeaveBalances();

    List<LeaveBalanceResponse> getEmployeeLeaveBalances(
            Long employeeId);

    List<LeaveBalanceResponse> getAllLeaveBalances();

    void deductLeaveBalance(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest);

    /**
     * Deducts only the approved PAID days of a request from the annual balance.
     *
     * <p>LOP days are never passed here, which is what keeps them out of the
     * annual balance. When {@code days} is zero there is nothing to deduct, so
     * no balance is written and no transaction is recorded. The balance is
     * clamped so it can never go negative.</p>
     */
    void deductPaidLeaveDays(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest,
            int days);

    void restoreLeaveBalance(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest);

}