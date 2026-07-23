package com.my_hourly.leave.service;

import com.my_hourly.leave.api.request.LeaveTypeRequest;
import com.my_hourly.leave.api.response.LeaveTypeResponse;
import com.my_hourly.leave.entity.LeaveType;

import java.util.List;

public interface LeaveTypeService {

    LeaveType getLeaveTypeEntity(Long leaveTypeId);

    LeaveTypeResponse createLeaveType(LeaveTypeRequest request);

    LeaveTypeResponse updateLeaveType(Long leaveTypeId,
                                      LeaveTypeRequest request);

    LeaveTypeResponse getLeaveType(Long leaveTypeId);

    List<LeaveTypeResponse> getAllLeaveTypes();

    List<LeaveTypeResponse> getActiveLeaveTypes();

    LeaveTypeResponse activateLeaveType(Long leaveTypeId);

    LeaveTypeResponse deactivateLeaveType(Long leaveTypeId);

}
