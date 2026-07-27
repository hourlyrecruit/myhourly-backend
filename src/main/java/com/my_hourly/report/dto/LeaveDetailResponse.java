package com.my_hourly.report.dto;

import com.my_hourly.leave.enums.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveDetailResponse {

    private String leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer totalDays;

    private LeaveStatus status;

    private String reason;

}