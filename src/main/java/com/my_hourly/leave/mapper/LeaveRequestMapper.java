package com.my_hourly.leave.mapper;

import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.stereotype.Component;

@Component
public class LeaveRequestMapper {

    public LeaveRequestResponse toResponse(
            LeaveRequest entity) {

        if (entity == null) {
            return null;
        }

        return LeaveRequestResponse.builder()
                .id(entity.getId())

                .employeeId(entity.getEmployee().getId())
                .employeeCode(entity.getEmployee().getEmployeeCode())
                .employeeName(
                        entity.getEmployee().getFirstName()
                                + " "
                                + entity.getEmployee().getLastName())

                .leaveTypeId(entity.getLeaveType().getId())
                .leaveType(entity.getLeaveType().getName())

                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())

                .totalDays(entity.getTotalDays())

                .reason(entity.getReason())

                .status(entity.getStatus())

//                .rejectionReason(entity.getRejectionReason())

                .build();
    }
}