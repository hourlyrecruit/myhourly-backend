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
                .allocationType(request.getAllocationType())
                .carryForwardAllowed(request.isCarryForwardAllowed())
                .maxCarryForwardDays(request.getMaxCarryForwardDays())
                .expireUnusedLeaves(request.isExpireUnusedLeaves())
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
                .allocationType(entity.getAllocationType())
                .carryForwardAllowed(entity.isCarryForwardAllowed())
                .maxCarryForwardDays(entity.getMaxCarryForwardDays())
                .expireUnusedLeaves(entity.isExpireUnusedLeaves())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(
            LeaveTypeRequest request,
            LeaveType entity) {

        entity.setName(request.getName().trim());
        entity.setDescription(request.getDescription());
        entity.setPaid(request.getPaid());
        entity.setAllocatedDays(request.getAllocatedDays());
        entity.setAllocationType(request.getAllocationType());
        entity.setCarryForwardAllowed(request.isCarryForwardAllowed());
        entity.setMaxCarryForwardDays(request.getMaxCarryForwardDays());
        entity.setExpireUnusedLeaves(request.isExpireUnusedLeaves());
    }
}
