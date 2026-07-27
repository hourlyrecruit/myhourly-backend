package com.my_hourly.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;
import lombok.Getter;

/**
 * Paginated response for Leave Report
 */
@Getter
@Builder
@Schema(description = "Paginated Leave Report Response with summary")
public class LeaveReportPageResponse {

    @Schema(description = "List of leave records for the current page")
    private List<LeaveReportResponse> content;

    @Schema(description = "Summary statistics for the filtered leave data")
    private LeaveSummaryResponse summary;

    // Pagination metadata
    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int page;

    @Schema(description = "Number of items per page", example = "20")
    private int size;

    @Schema(description = "Total number of items across all pages", example = "75")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "4")
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
