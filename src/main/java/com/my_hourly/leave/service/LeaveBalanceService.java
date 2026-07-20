package com.my_hourly.leave.service;



import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;

import java.time.LocalDate;
import java.util.List;

public interface LeaveBalanceService {

    LeaveBalance getLeaveBalanceEntity(
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

    void restoreLeaveBalance(
            LeaveBalance leaveBalance,
            LeaveRequest leaveRequest);


    void initializeEmployeeLeaveBalance(Employee employee);

}