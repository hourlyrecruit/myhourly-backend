package com.my_hourly.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Paginated response for Attendance Report
 */
@Getter
@Builder
@Schema(description = "Paginated Attendance Report Response with summary")
public class AttendanceReportPageResponse {

    @Schema(description = "List of attendance records for the current page")
    private List<AttendanceReportResponse> content;

    @Schema(description = "Summary statistics for the filtered attendance data")
    private AttendanceSummaryResponse summary;

    // Pagination metadata
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int page;

    @Schema(description = "Number of items per page", example = "20")
    private int size;

    @Schema(description = "Total number of items across all pages", example = "150")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "8")
    private int totalPages;

    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;

    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;

    @Schema(description = "Whether there is a next page", example = "true")
    private boolean hasNext;

    @Schema(description = "Whether there is a previous page", example = "false")
    private boolean hasPrevious;
}