package com.my_hourly.leave.service;

import java.util.List;

import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveResponse;

public interface LeaveService {

    // =====================================================
    // Employee - Apply Leave
    // =====================================================

    LeaveResponse applyLeave(LeaveRequest request);

    // =====================================================
    // Employee - View Own Leaves
    // =====================================================

    List<LeaveResponse> getEmployeeLeaves(String employeeCode);

    // =====================================================
    // Employee - View Leave Status
    // =====================================================

    List<LeaveResponse> getLeaveStatus(String employeeCode);

    // =====================================================
    // Employee - View Leave Balance
    // =====================================================

    Integer getLeaveBalance(String employeeCode);

    // =====================================================
    // Manager / HR - View All Leaves
    // =====================================================

    List<LeaveResponse> getAllLeaves();

    // =====================================================
    // Manager - View Pending Leaves
    // =====================================================

    List<LeaveResponse> getPendingLeaves();

    // =====================================================
    // Manager - Approve Leave
    // =====================================================

    LeaveResponse approveLeave(Long leaveId, String managerRemarks);

    // =====================================================
    // Manager - Reject Leave
    // =====================================================

    LeaveResponse rejectLeave(Long leaveId, String managerRemarks);

}