package com.my_hourly.performance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceSummaryResponse {

    /**
     * Total Reviews
     */
    private Long totalReviews;

    /**
     * Monthly Reviews
     */
    private Long monthlyReviews;

    /**
     * Yearly Reviews
     */
    private Long yearlyReviews;

    /**
     * Completed Reviews
     */
    private Long completedReviews;

    /**
     * Draft Reviews
     */
    private Long draftReviews;

    /**
     * Average Performance Score
     */
    private Double averageScore;

    /**
     * Rating Counts
     */
    private Long excellentCount;

    private Long veryGoodCount;

    private Long goodCount;

    private Long averageCount;

    private Long needsImprovementCount;

}