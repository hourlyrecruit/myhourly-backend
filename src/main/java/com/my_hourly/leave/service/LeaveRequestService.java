package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.enums.LeaveStatus;

import java.util.List;

public interface LeaveRequestService {

    LeaveRequest getLeaveRequestEntity(Long leaveRequestId);

    LeaveRequestResponse applyLeave(
            LeaveRequestRequest request);

    LeaveRequestResponse getLeaveRequest(
            Long leaveRequestId);

    List<LeaveRequestResponse> getMyLeaveRequests();

    List<LeaveRequestResponse> getTeamLeaveRequests();

    List<LeaveRequestResponse> getAllLeaveRequests();

    LeaveRequestResponse cancelLeave(
            Long leaveRequestId);

    LeaveRequestResponse managerAction(
            Long leaveRequestId,
            LeaveActionRequest request);

//    LeaveRequestResponse hrAction(
//            Long leaveRequestId,
//            LeaveActionRequest request);

}
