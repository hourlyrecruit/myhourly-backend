package com.my_hourly.report.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveReportResponse {

    private Integer allocatedLeaves;

    private Integer usedLeaves;

    private Integer remainingLeaves;

    private Integer expiredLeaves;

    private Long pendingLeaves;

    private Long approvedLeaves;

    private Long rejectedLeaves;

    private Long cancelledLeaves;
}