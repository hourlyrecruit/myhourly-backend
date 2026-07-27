package com.my_hourly.report.dto;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.report.entity.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class AttendanceReportFilter {

    private ReportType reportType;
    private Long employeeId;
    private Long departmentId;
    private String employeeName;
    private AttendanceStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer month;
    private Integer year;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "firstName";
    private String sortDirection = "ASC";
}
