package com.my_hourly.report.dto.request;

import com.my_hourly.attendance.entity.AttendanceStatus;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request DTO for Attendance Report with filters and pagination
 */
@Getter
@Setter
public class AttendanceReportRequest {

    // Employee Filters
    @Parameter(description = "Filter by Employee ID", example = "1")
    private Long employeeId;

    @Parameter(description = "Filter by Employee Name (partial match)", example = "John")
    @Size(max = 100, message = "Employee name cannot exceed 100 characters")
    private String employeeName;

    @Parameter(description = "Filter by Department ID", example = "5")
    private Long departmentId;

    // Attendance Filters
    @Parameter(description = "Filter by Attendance Status", example = "PRESENT")
    private AttendanceStatus attendanceStatus;

    // Date Range Filters
    @Parameter(description = "Start Date (YYYY-MM-DD)", example = "2026-07-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Parameter(description = "End Date (YYYY-MM-DD)", example = "2026-07-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    // Month/Year Filters (alternative to date range)
    @Parameter(description = "Month (1-12)", example = "7")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @Parameter(description = "Year", example = "2026")
    @Min(value = 2000, message = "Year must be 2000 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    // Pagination
    @Parameter(description = "Page number (0-indexed)", example = "0")
    @Min(value = 0, message = "Page must be non-negative")
    private Integer page = 0;

    @Parameter(description = "Page size", example = "20")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;

    // Sorting
    @Parameter(description = "Sort field", example = "attendanceDate")
    @Pattern(
        regexp = "attendanceDate|checkInTime|checkOutTime|employeeName|departmentName",
        message = "Invalid sort field"
    )
    private String sortBy = "attendanceDate";

    @Parameter(description = "Sort direction", example = "DESC")
    @Pattern(regexp = "ASC|DESC", message = "Sort direction must be ASC or DESC")
    private String sortDir = "DESC";
}