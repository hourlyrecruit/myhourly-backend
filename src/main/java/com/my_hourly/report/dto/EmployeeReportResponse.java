package com.my_hourly.report.dto;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeReportResponse {

    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;

    private LocalDate fromDate;
    private LocalDate toDate;

    // Attendance
    private Long presentDays;
    private Long absentDays;
    private Long halfDays;
    private Long leaveDays;
    private Long lateDays;

    private Integer totalWorkingMinutes;
    private Integer totalBreakMinutes;
    private Integer totalLateMinutes;
    private Integer totalEarlyExitMinutes;
    private Integer totalOvertimeMinutes;

    private Double attendancePercentage;

    // Leave
    private Integer allocatedLeaves;
    private Integer usedLeaves;
    private Integer remainingLeaves;
    private Integer expiredLeaves;

    private Long pendingLeaves;
    private Long approvedLeaves;
    private Long rejectedLeaves;
    private Long cancelledLeaves;

}