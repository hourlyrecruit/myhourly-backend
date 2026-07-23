package com.my_hourly.leave.mapper;

import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.entity.LeaveApproval;
import org.springframework.stereotype.Component;

@Component
public class LeaveApprovalMapper {

    public LeaveApprovalResponse toResponse(
            LeaveApproval entity) {

        if (entity == null) {
            return null;
        }

        return LeaveApprovalResponse.builder()
                .id(entity.getId())

                .approvedById(entity.getApprovedBy().getId())

                .approvedByName(
                        entity.getApprovedBy().getFirstName()
                                + " "
                                + entity.getApprovedBy().getLastName())

                .approvalLevel(entity.getApprovalLevel())

                .action(entity.getAction())

                .remarks(entity.getRemarks())

                .createdAt(entity.getCreatedAt())

                .build();
    }

}