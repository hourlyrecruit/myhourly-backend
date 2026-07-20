package com.my_hourly.leave.mapper;

import com.my_hourly.leave.api.request.LeaveTypeRequest;
import com.my_hourly.leave.api.response.LeaveTypeResponse;
import com.my_hourly.leave.entity.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {

    public LeaveType toEntity(LeaveTypeRequest request) {

        if (request == null) {
            return null;
        }

        return LeaveType.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .paid(request.getPaid())
                .allocatedDays(request.getAllocatedDays())
                .monthlyGuideline(
                        request.getMonthlyGuideline() != null
                                ? request.getMonthlyGuideline()
                                : 2)
                .carryForwardAllowed(request.isCarryForwardAllowed())
                .active(true)
                .build();
    }

    public LeaveTypeResponse toResponse(LeaveType entity) {

        if (entity == null) {
            return null;
        }

        return LeaveTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .paid(entity.getPaid())
                .allocatedDays(entity.getAllocatedDays())
                .monthlyGuideline(entity.getMonthlyGuideline())
                .carryForwardAllowed(entity.isCarryForwardAllowed())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(LeaveTypeRequest request, LeaveType entity) {

        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setPaid(request.getPaid());
        entity.setAllocatedDays(request.getAllocatedDays());
        entity.setMonthlyGuideline(
                request.getMonthlyGuideline() != null
                        ? request.getMonthlyGuideline()
                        : entity.getMonthlyGuideline());
        entity.setCarryForwardAllowed(request.isCarryForwardAllowed());
    }
}
