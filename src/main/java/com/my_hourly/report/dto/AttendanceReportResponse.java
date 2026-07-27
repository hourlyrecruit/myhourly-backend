package com.my_hourly.report.dto;

import com.my_hourly.attendance.entity.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceReportResponse {


    private Long presentDays;

    private Long absentDays;

    private Long halfDays;

    private Long lateDays;

    private Integer totalWorkingMinutes;

    private Integer totalBreakMinutes;

    private Integer totalLateMinutes;

    private Integer totalEarlyExitMinutes;

    private Integer totalOvertimeMinutes;

    private Double attendancePercentage;

    private AttendanceStatus status;
}