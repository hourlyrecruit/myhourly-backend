package com.my_hourly.report.dto.response;

import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Individual leave record in the report
 */
@Getter
@Builder
@Schema(description = "Leave report record")
public class LeaveReportResponse {

    // Employee Details
    @Schema(description = "Employee ID", example = "1")
    private Long employeeId;

    @Schema(description = "Employee code/number", example = "EMP001")
    private String employeeCode;

    @Schema(description = "Full name of the employee", example = "John Doe")
    private String employeeName;

    @Schema(description = "Department name", example = "Engineering")
    private String departmentName;

    // Leave Details
    @Schema(description = "Leave request ID", example = "10")
    private Long leaveId;

    @Schema(description = "Type of leave")
    private String leaveType;

    @Schema(description = "Current status of leave", example = "APPROVED")
    private LeaveStatus leaveStatus;

    @Schema(description = "Leave start date", example = "2026-07-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "Leave end date", example = "2026-07-30")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "Total number of leave days", example = "3")
    private Integer totalDays;

    @Schema(description = "Reason for leave")
    private String reason;

    // Timestamps
    @Schema(description = "Date when leave request was created", example = "2026-07-20T10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Date when leave request was last updated", example = "2026-07-21T14:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
