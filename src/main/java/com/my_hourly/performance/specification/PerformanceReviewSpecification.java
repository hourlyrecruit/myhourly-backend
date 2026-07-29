package com.my_hourly.performance.specification;

import com.my_hourly.performance.dto.request.PerformanceReviewFilterRequest;
import com.my_hourly.performance.entity.PerformanceReview;
import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import org.springframework.data.jpa.domain.Specification;

public class PerformanceReviewSpecification {

    public static Specification<PerformanceReview> filter(
            PerformanceReviewFilterRequest request) {

        return Specification
                .where(hasEmployee(request.getEmployeeId()))
                .and(hasReviewer(request.getReviewerId()))
                .and(hasDepartment(request.getDepartmentId()))
                .and(hasReviewType(request.getReviewType()))
                .and(hasReviewMonth(request.getReviewMonth()))
                .and(hasReviewYear(request.getReviewYear()))
                .and(hasRating(request.getRating()))
                .and(hasStatus(request.getStatus()))
                .and(hasMinScore(request.getMinScore()))
                .and(hasMaxScore(request.getMaxScore()));
    }

    public static Specification<PerformanceReview> hasEmployee(Long employeeId) {
        return (root, query, cb) ->
                employeeId == null
                        ? null
                        : cb.equal(root.get("employee").get("id"), employeeId);
    }

    public static Specification<PerformanceReview> hasReviewer(Long reviewerId) {
        return (root, query, cb) ->
                reviewerId == null
                        ? null
                        : cb.equal(root.get("reviewer").get("id"), reviewerId);
    }

    public static Specification<PerformanceReview> hasDepartment(Long departmentId) {
        return (root, query, cb) ->
                departmentId == null
                        ? null
                        : cb.equal(root.get("employee").get("department").get("id"), departmentId);
    }

    public static Specification<PerformanceReview> hasReviewType(ReviewType reviewType) {
        return (root, query, cb) ->
                reviewType == null
                        ? null
                        : cb.equal(root.get("reviewType"), reviewType);
    }

    public static Specification<PerformanceReview> hasReviewMonth(Integer reviewMonth) {
        return (root, query, cb) ->
                reviewMonth == null
                        ? null
                        : cb.equal(root.get("reviewMonth"), reviewMonth);
    }

    public static Specification<PerformanceReview> hasReviewYear(Integer reviewYear) {
        return (root, query, cb) ->
                reviewYear == null
                        ? null
                        : cb.equal(root.get("reviewYear"), reviewYear);
    }

    public static Specification<PerformanceReview> hasRating(PerformanceRating rating) {
        return (root, query, cb) ->
                rating == null
                        ? null
                        : cb.equal(root.get("rating"), rating);
    }

    public static Specification<PerformanceReview> hasStatus(ReviewStatus status) {
        return (root, query, cb) ->
                status == null
                        ? null
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<PerformanceReview> hasMinScore(Double minScore) {
        return (root, query, cb) ->
                minScore == null
                        ? null
                        : cb.greaterThanOrEqualTo(root.get("score"), minScore);
    }

    public static Specification<PerformanceReview> hasMaxScore(Double maxScore) {
        return (root, query, cb) ->
                maxScore == null
                        ? null
                        : cb.lessThanOrEqualTo(root.get("score"), maxScore);
    }

}