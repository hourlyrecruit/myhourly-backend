package com.my_hourly.performance.controller;

import com.my_hourly.performance.dto.request.CreatePerformanceReviewRequest;
import com.my_hourly.performance.dto.request.PerformanceReviewFilterRequest;
import com.my_hourly.performance.dto.request.UpdatePerformanceReviewRequest;
import com.my_hourly.performance.dto.response.PerformanceReviewResponse;
import com.my_hourly.performance.dto.response.PerformanceSummaryResponse;
import com.my_hourly.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/performance")
@RequiredArgsConstructor
@Tag(name = "16-Performance Management")
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Create Performance Review, Access: MANAGER, HR_ADMIN, SUPER_ADMIN")
    public ResponseEntity<PerformanceReviewResponse> createReview(
            @Valid @RequestBody CreatePerformanceReviewRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(performanceService.createReview(request));
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('MANAGER','HR_ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Update Performance Review, Access: MANAGER, HR_ADMIN, SUPER_ADMIN")
    public ResponseEntity<PerformanceReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdatePerformanceReviewRequest request) {

        return ResponseEntity.ok(
                performanceService.updateReview(reviewId, request));
    }

    @PutMapping("/{reviewId}/complete")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Complete Performance Review, Access: MANAGER, HR_ADMIN, SUPER_ADMIN")
    public ResponseEntity<PerformanceReviewResponse> completeReview(
            @PathVariable Long reviewId) {

        return ResponseEntity.ok(
                performanceService.completeReview(reviewId));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get Performance Review By Id, Access: 'EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PerformanceReviewResponse> getReviewById(
            @PathVariable Long reviewId) {

        return ResponseEntity.ok(
                performanceService.getReviewById(reviewId));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Search Performance Reviews")
    public ResponseEntity<Page<PerformanceReviewResponse>> searchReviews(
            @RequestBody PerformanceReviewFilterRequest request) {

        return ResponseEntity.ok(
                performanceService.getReviews(request));
    }

    @PostMapping("/summary")
    @Operation(summary = "Performance Dashboard Summary")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PerformanceSummaryResponse> getSummary(
            @RequestBody PerformanceReviewFilterRequest request) {

        return ResponseEntity.ok(
                performanceService.getSummary(request));
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete Performance Review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId) {

        performanceService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }
}
