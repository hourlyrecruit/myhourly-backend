package com.my_hourly.leave.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponse {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long leaveTypeId;

    private String leaveType;

    private Integer year;

    private Integer allocatedLeaves;

    private Integer usedLeaves;

    private Integer expiredLeaves;

    private Integer remainingLeaves;

}