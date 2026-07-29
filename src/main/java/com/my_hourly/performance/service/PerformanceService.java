package com.my_hourly.performance.service;

import com.my_hourly.performance.dto.request.CreatePerformanceReviewRequest;
import com.my_hourly.performance.dto.request.PerformanceReviewFilterRequest;
import com.my_hourly.performance.dto.request.UpdatePerformanceReviewRequest;
import com.my_hourly.performance.dto.response.PerformanceReviewResponse;
import com.my_hourly.performance.dto.response.PerformanceSummaryResponse;
import org.springframework.data.domain.Page;

public interface PerformanceService {

    /**
     * Create Monthly/Yearly Performance Review
     */
    PerformanceReviewResponse createReview(
            CreatePerformanceReviewRequest request);

    /**
     * Update Performance Review
     */
    PerformanceReviewResponse updateReview(
            Long reviewId,
            UpdatePerformanceReviewRequest request);

    /**
     * Complete Performance Review
     */
    PerformanceReviewResponse completeReview(
            Long reviewId);

    /**
     * Get Review By Id
     */
    PerformanceReviewResponse getReviewById(
            Long reviewId);

    /**
     * Get All Reviews With Filters
     */
    Page<PerformanceReviewResponse> getReviews(
            PerformanceReviewFilterRequest request);

    /**
     * Dashboard Summary
     */
    PerformanceSummaryResponse getSummary(
            PerformanceReviewFilterRequest request);

    /**
     * Delete Review
     */
    void deleteReview(
            Long reviewId);

}
