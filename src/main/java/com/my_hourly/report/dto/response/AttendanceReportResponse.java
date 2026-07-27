package com.my_hourly.report.dto.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Individual attendance record in the report
 */
@Getter
@Builder
@Schema(description = "Attendance report record")
public class AttendanceReportResponse {

    @Schema(description = "Employee ID", example = "1")
    private Long employeeId;

    @Schema(description = "Employee code/number", example = "EMP001")
    private String employeeCode;

    @Schema(description = "Full name of the employee", example = "John Doe")
    private String employeeName;

    @Schema(description = "Department name", example = "Engineering")
    private String departmentName;

    @Schema(description = "Attendance date", example = "2026-07-27")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    @Schema(description = "Check-in time", example = "2026-07-27T09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkInTime;

    @Schema(description = "Check-out time", example = "2026-07-27T18:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkOutTime;

    @Schema(description = "Total working minutes", example = "480")
    private Integer workingMinutes;

    @Schema(description = "Total break minutes", example = "60")
    private Integer breakMinutes;

    @Schema(description = "Attendance status", example = "PRESENT")
    private AttendanceStatus attendanceStatus;

    @Schema(description = "Working hours (formatted)", example = "8.0")
    private Double workingHours;
}
