package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;

import java.util.List;

public interface LeaveAuthorizationService {

    void validateManagerApproval(
            Employee manager,
            LeaveRequest leaveRequest);

    void validateHrApproval(
            Employee employee);

    List<LeaveRequestResponse> getTeamLeaveRequests();

}