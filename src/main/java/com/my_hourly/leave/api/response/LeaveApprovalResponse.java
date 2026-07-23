package com.my_hourly.leave.api.response;

import com.my_hourly.leave.enums.ApprovalLevel;
import com.my_hourly.leave.enums.LeaveAction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApprovalResponse {

    private Long id;

    private Long approvedById;

    private String approvedByName;

    private ApprovalLevel approvalLevel;

    private LeaveAction action;

    private String remarks;

    private LocalDateTime createdAt;

}