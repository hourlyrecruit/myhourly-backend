package com.my_hourly.leave.api.response;

import com.my_hourly.leave.enums.LeaveTransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTransactionResponse {

    private Long id;

    private String leaveType;

    private LeaveTransactionType transactionType;

    private Integer days;

    private Integer balanceBefore;

    private Integer balanceAfter;

    private String remarks;

    private LocalDateTime createdAt;

}
