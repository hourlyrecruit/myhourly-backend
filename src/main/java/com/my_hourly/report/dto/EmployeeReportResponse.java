package com.my_hourly.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeReportResponse {

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private String department;

    private String designation;

    // Summary
    private AttendanceReportResponse attendanceSummary;
    private LeaveReportResponse leaveSummary;
    // Detailed
    private List<LeaveDetailResponse> leaveDetails;
    private List<AttendanceDetailResponse> attendanceDetails;

}