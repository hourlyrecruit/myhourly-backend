package com.my_hourly.leave.mapper;

import com.my_hourly.leave.api.response.LeaveTransactionResponse;
import com.my_hourly.leave.entity.LeaveTransaction;
import org.springframework.stereotype.Component;

@Component
public class LeaveTransactionMapper {

    public LeaveTransactionResponse toResponse(
            LeaveTransaction entity) {

        if (entity == null) {
            return null;
        }

        return LeaveTransactionResponse.builder()
                .id(entity.getId())
                .leaveType(entity.getLeaveType().getName())
                .transactionType(entity.getTransactionType())
                .days(entity.getDays())
                .balanceBefore(entity.getBalanceBefore())
                .balanceAfter(entity.getBalanceAfter())
                .remarks(entity.getRemarks())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}