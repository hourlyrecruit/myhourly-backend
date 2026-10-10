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

    /**
     * The current employee's leave requests, optionally filtered to a calendar
     * month ({@code month} + {@code year}) or a whole year ({@code year} only).
     * A request is returned when its date range overlaps the selected period.
     */
    List<LeaveRequestResponse> getMyLeaveRequests(
            Integer month,
            Integer year);

    List<LeaveRequestResponse> getTeamLeaveRequests();

    /**
     * Every leave request, optionally filtered to a calendar month
     * ({@code month} + {@code year}) or a whole year ({@code year} only).
     * A request is returned when its date range overlaps the selected period.
     */
    List<LeaveRequestResponse> getAllLeaveRequests(
            Integer month,
            Integer year);

    LeaveRequestResponse cancelLeave(
            Long leaveRequestId);

    LeaveRequestResponse managerAction(
            Long leaveRequestId,
            LeaveActionRequest request);

//    LeaveRequestResponse hrAction(
//            Long leaveRequestId,
//            LeaveActionRequest request);

}
