package com.my_hourly.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Summary statistics for attendance report
 */
@Getter
@Builder
@Schema(description = "Attendance report summary statistics")
public class AttendanceSummaryResponse {

    @Schema(description = "Total number of attendance records", example = "150")
    private long totalRecords;

    @Schema(description = "Number of present days", example = "120")
    private long presentCount;

    @Schema(description = "Number of absent days", example = "5")
    private long absentCount;

    @Schema(description = "Number of late arrivals", example = "10")
    private long lateCount;

    @Schema(description = "Number of half days", example = "3")
    private long halfDayCount;

    @Schema(description = "Number of leave days", example = "8")
    private long leaveCount;

    @Schema(description = "Number of holidays", example = "2")
    private long holidayCount;

    @Schema(description = "Number of weekend days", example = "2")
    private long weekendCount;

    @Schema(description = "Overall attendance percentage", example = "95.5")
    private double attendancePercentage;
}
