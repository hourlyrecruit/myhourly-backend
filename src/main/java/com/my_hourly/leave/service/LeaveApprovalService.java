package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.ApprovalLevel;
import com.my_hourly.leave.enums.LeaveAction;

import java.util.List;

public interface LeaveApprovalService {

    void createApproval(
            LeaveRequest leaveRequest,
            Employee approvedBy,
            ApprovalLevel approvalLevel,
            LeaveAction action,
            String remarks);

    List<LeaveApprovalResponse> getApprovalHistory(
            Long leaveRequestId);

}