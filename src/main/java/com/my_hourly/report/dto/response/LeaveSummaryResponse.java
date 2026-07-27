package com.my_hourly.report.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * Summary statistics for leave report
 */
@Getter
@Builder
@Schema(description = "Leave report summary statistics")
public class LeaveSummaryResponse {

    @Schema(description = "Total number of leave applications", example = "75")
    private long totalLeaves;

    // Leave Status Counts
    @Schema(description = "Number of approved leaves", example = "60")
    private long approvedLeaves;

    @Schema(description = "Number of pending leaves", example = "10")
    private long pendingLeaves;

    @Schema(description = "Number of rejected leaves", example = "3")
    private long rejectedLeaves;

    @Schema(description = "Number of cancelled leaves", example = "2")
    private long cancelledLeaves;

    // Leave Days Summary
    @Schema(description = "Total leave days across all applications", example = "225")
    private long totalLeaveDays;

    @Schema(description = "Average leave days per application", example = "3.0")
    private double averageLeaveDays;

    // Employee Summary
    @Schema(description = "Number of unique employees who took leave", example = "45")
    private long uniqueEmployees;
}
