package com.my_hourly.leave.service;

import java.util.List;

import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveBalanceResponse;
import com.my_hourly.leave.dto.response.LeaveResponse;

public interface LeaveService {

    LeaveResponse applyLeave(LeaveRequest request);

    List<LeaveResponse> getEmployeeLeaves(String employeeCode);

    List<LeaveResponse> getLeaveStatus(String employeeCode);

    List<LeaveResponse> getPendingLeaves();

    LeaveResponse approveLeave(Long leaveId);

    LeaveResponse rejectLeave(Long leaveId);

    List<LeaveResponse> getAllLeaves();

    List<LeaveResponse> getApprovedLeaves();

    List<LeaveResponse> getRejectedLeaves();

}