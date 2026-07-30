package com.my_hourly.performance.controller;

import com.my_hourly.performance.dto.request.CreatePerformanceReviewRequest;
import com.my_hourly.performance.dto.request.PerformanceReviewFilterRequest;
import com.my_hourly.performance.dto.request.UpdatePerformanceReviewRequest;
import com.my_hourly.performance.dto.response.PerformanceReviewResponse;
import com.my_hourly.performance.dto.response.PerformanceSummaryResponse;
import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import com.my_hourly.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "Search Performance Reviews, 'EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN'")
    public ResponseEntity<Page<PerformanceReviewResponse>> searchReviews(
            @Parameter(description = "Employee ID - 1,2,3...")
            @RequestParam(required = false) Long employeeId,
            @Parameter(description = "Reviewer ID - Manager who is reviewing the employee")
            @RequestParam(required = false) Long reviewerId,
            @Parameter(description = "Department ID - 1, 2, 3...")
            @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Review Type -   MONTHLY or YEARLY")
            @RequestParam(required = false) ReviewType reviewType,
            @Parameter(description = "Review Month - Valid values: 1-12")
            @RequestParam(required = false) Integer reviewMonth,
            @Parameter(description = "Review Year - Valid values: 20__ - 20__")
            @RequestParam(required = false) Integer reviewYear,
            @Parameter(description = "Rating | Valid values: EXCELLENT, VERY_GOOD, GOOD, AVERAGE, NEEDS_IMPROVEMENT")
            @RequestParam(required = false) PerformanceRating rating,
            @Parameter(description = "Review Status - Valid values: DRAFT, COMPLETED")
            @RequestParam(required = false) ReviewStatus status,
            @Parameter(description = "Rating | Valid score " +
                    "EXCELLENT -> 90–100 " +
                    "VERY_GOOD -> 80–89.99 " +
                    "GOOD -> 70–79.99 " +
                    "AVERAGE -> 60–69.99 " +
                    "NEEDS_IMPROVEMENT below 60 ")
            @RequestParam(required = false) Double minScore,
            @Parameter(description = "Rating | Valid score " +
                    "EXCELLENT -> 90–100 " +
                    "VERY_GOOD -> 80–89.99 " +
                    "GOOD -> 70–79.99 " +
                    "AVERAGE -> 60–69.99 " +
                    "NEEDS_IMPROVEMENT below 60 ")
            @RequestParam(required = false) Double maxScore,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        PerformanceReviewFilterRequest request = buildFilterRequest(
                employeeId, reviewerId, departmentId, reviewType, reviewMonth,
                reviewYear, rating, status, minScore, maxScore, page, size,
                sortBy, sortDir);

        return ResponseEntity.ok(
                performanceService.getReviews(request));
    }

    @PostMapping("/get-summary")
    @Operation(summary = "Performance Dashboard Summary, Fill the filter, 'EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PerformanceSummaryResponse> getSummary(
            @Parameter(description = "Employee ID - 1, 2, 3...")
            @RequestParam(required = false) Long employeeId,
            @Parameter(description = "Reviewer ID - Manager who is reviewing the employee")
            @RequestParam(required = false) Long reviewerId,
            @Parameter
(description = "Department ID - 1, 2, 3...")
            @RequestParam(required = false) Long departmentId,
            @Parameter(description = "Review Type -   MONTHLY or YEARLY")
            @RequestParam(required = false) ReviewType reviewType,
            @Parameter(description = "Review Month - Valid values: 1-12")
            @RequestParam(required = false) Integer reviewMonth,
            @Parameter(description = "Review Year - Valid values: 20__ - 20__")
            @RequestParam(required = false) Integer reviewYear,
            @Parameter
(description = "Rating | Valid values: EXCELLENT, VERY_GOOD, GOOD, AVERAGE, NEEDS_IMPROVEMENT")
            @RequestParam(required = false) PerformanceRating rating,
            @Parameter(description = "Review Status - Valid values: DRAFT, COMPLETED")
            @RequestParam(required = false) ReviewStatus status,
            @Parameter(description = "Rating | Valid score " +
                    "EXCELLENT -> 90–100 " +
                    "VERY_GOOD -> 80–89.99 " +
                    "GOOD -> 70–79.99 " +
                    "AVERAGE -> 60–69.99 " +
                    "NEEDS_IMPROVEMENT below 60 ")
            @RequestParam(required = false) Double minScore,
            @Parameter(description = "Rating | Valid score " +
                    "EXCELLENT -> 90–100 " +
                    "VERY_GOOD -> 80–89.99 " +
                    "GOOD -> 70–79.99 " +
                    "AVERAGE -> 60–69.99 " +
                    "NEEDS_IMPROVEMENT below 60 ")
            @RequestParam(required = false) Double maxScore) {

        PerformanceReviewFilterRequest request = buildFilterRequest(
                employeeId, reviewerId, departmentId, reviewType, reviewMonth,
                reviewYear, rating, status, minScore, maxScore, 0, 10,
                "reviewDate", "DESC");

        return ResponseEntity.ok(
                performanceService.getSummary(request));
    }

    private PerformanceReviewFilterRequest buildFilterRequest(
            Long employeeId,
            Long reviewerId,
            Long departmentId,
            ReviewType reviewType,
            Integer reviewMonth,
            Integer reviewYear,
            PerformanceRating rating,
            ReviewStatus status,
            Double minScore,
            Double maxScore,
            Integer page,
            Integer size,
            String sortBy,
            String sortDir) {

        return PerformanceReviewFilterRequest.builder()
                .employeeId(employeeId)
                .reviewerId(reviewerId)
                .departmentId(departmentId)
                .reviewType(reviewType)
                .reviewMonth(reviewMonth)
                .reviewYear(reviewYear)
                .rating(rating)
                .status(status)
                .minScore(minScore)
                .maxScore(maxScore)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete Performance Review, 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId) {

        performanceService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }
}
