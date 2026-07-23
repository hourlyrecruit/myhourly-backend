package com.my_hourly.leave.mapper;

import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.entity.LeaveBalance;
import org.springframework.stereotype.Component;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse toResponse(LeaveBalance entity) {

        if (entity == null) {
            return null;
        }

        return LeaveBalanceResponse.builder()
                .id(entity.getId())

                .employeeId(entity.getEmployee().getId())
                .employeeCode(entity.getEmployee().getEmployeeCode())
                .employeeName(
                        entity.getEmployee().getFirstName()
                                + " "
                                + entity.getEmployee().getLastName())

                .leaveTypeId(entity.getLeaveType().getId())
                .leaveType(entity.getLeaveType().getName())

                .year(entity.getYear())

                .allocatedLeaves(entity.getAllocatedLeaves())
                .usedLeaves(entity.getUsedLeaves())
                .expiredLeaves(entity.getExpiredLeaves())
                .remainingLeaves(entity.getRemainingLeaves())

                .build();
    }
}