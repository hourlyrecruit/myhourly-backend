package com.my_hourly.performance.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.performance.dto.request.CreatePerformanceReviewRequest;
import com.my_hourly.performance.dto.request.PerformanceReviewFilterRequest;
import com.my_hourly.performance.dto.request.UpdatePerformanceReviewRequest;
import com.my_hourly.performance.dto.response.PerformanceReviewResponse;
import com.my_hourly.performance.dto.response.PerformanceSummaryResponse;
import com.my_hourly.performance.entity.PerformanceReview;
import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import com.my_hourly.performance.repository.PerformanceReviewRepository;
import com.my_hourly.performance.service.PerformanceService;
import com.my_hourly.performance.specification.PerformanceReviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public PerformanceReviewResponse createReview(
            CreatePerformanceReviewRequest request) {

        validateCreateRequest(request);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        Employee reviewer = employeeRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reviewer not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        validateDuplicateReview(employee, request);
        validateScoreAndRating(
                request.getScore(),
                request.getRating());

        PerformanceReview review = PerformanceReview.builder()
                .employee(employee)
                .reviewer(reviewer)
                .reviewType(request.getReviewType())
                .reviewMonth(request.getReviewMonth())
                .reviewYear(request.getReviewYear())
                .rating(request.getRating())
                .score(request.getScore())
                .strengths(request.getStrengths())
                .improvements(request.getImprovements())
                .managerFeedback(request.getManagerFeedback())
                .employeeComment(request.getEmployeeComment())
                .reviewDate(LocalDateTime.now())
                .status(ReviewStatus.DRAFT)
                .build();

        review = performanceReviewRepository.save(review);

        return mapToResponse(review);
    }

    private void validateCreateRequest(
            CreatePerformanceReviewRequest request) {

        if (request.getReviewType() == ReviewType.MONTHLY) {

            if (request.getReviewMonth() == null) {
                throw new BadRequestException(
                        "Review month is required for monthly review.",
                        ErrorCode.BAD_REQUEST);
            }

        } else {

            request.setReviewMonth(null);
        }
    }

    private void validateDuplicateReview(
            Employee employee,
            CreatePerformanceReviewRequest request) {

        boolean exists;

        if (request.getReviewType() == ReviewType.MONTHLY) {

            exists = performanceReviewRepository
                    .existsByEmployeeAndReviewTypeAndReviewMonthAndReviewYear(
                            employee,
                            request.getReviewType(),
                            request.getReviewMonth(),
                            request.getReviewYear());

        } else {

            exists = performanceReviewRepository
                    .existsByEmployeeAndReviewTypeAndReviewYear(
                            employee,
                            request.getReviewType(),
                            request.getReviewYear());
        }

        if (exists) {
            throw new BadRequestException(
                    "Performance review already exists.",
                    ErrorCode.PERFORMANCE_REVIEW_ALREADY_EXIST);
        }
    }

    private void validateScoreAndRating(
            Double score,
            PerformanceRating rating) {

        switch (rating) {

            case EXCELLENT -> {
                if (score < 90) {
                    throw new BadRequestException(
                            "Excellent rating requires score >= 90",
                            ErrorCode.BAD_REQUEST);
                }
            }

            case VERY_GOOD -> {
                if (score < 80 || score >= 90) {
                    throw new BadRequestException(
                            "Very Good rating requires score between 80 and 89",
                            ErrorCode.BAD_REQUEST);
                }
            }

            case GOOD -> {
                if (score < 70 || score >= 80) {
                    throw new BadRequestException(
                            "Good rating requires score between 70 and 79",
                            ErrorCode.BAD_REQUEST);
                }
            }

            case AVERAGE -> {
                if (score < 60 || score >= 70) {
                    throw new BadRequestException(
                            "Average rating requires score between 60 and 69",
                            ErrorCode.BAD_REQUEST);
                }
            }

            case NEEDS_IMPROVEMENT -> {
                if (score >= 60) {
                    throw new BadRequestException(
                            "Needs Improvement requires score below 60",
                            ErrorCode.BAD_REQUEST);
                }
            }
        }
    }

    @Override
    public PerformanceReviewResponse updateReview(
            Long reviewId,
            UpdatePerformanceReviewRequest request) {

        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.COMPLETED) {
            throw new BadRequestException(
                    "Completed performance review cannot be updated.",
                    ErrorCode.NOT_ALLOWED);
        }

        validateScoreAndRating(
                request.getScore(),
                request.getRating());

        review.setRating(request.getRating());
        review.setScore(request.getScore());
        review.setStrengths(request.getStrengths());
        review.setImprovements(request.getImprovements());
        review.setManagerFeedback(request.getManagerFeedback());
        review.setEmployeeComment(request.getEmployeeComment());

        review = performanceReviewRepository.save(review);

        return mapToResponse(review);
    }

    @Override
    public PerformanceReviewResponse completeReview(
            Long reviewId) {

        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.COMPLETED) {
            throw new BadRequestException(
                    "Performance review is already completed.",
                    ErrorCode.NOT_ALLOWED);
        }

        review.setStatus(ReviewStatus.COMPLETED);
        review.setReviewDate(LocalDateTime.now());

        review = performanceReviewRepository.save(review);

        return mapToResponse(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceReviewResponse getReviewById(
            Long reviewId) {

        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        return mapToResponse(review);
    }

    @Override
    public void deleteReview(Long reviewId) {

        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found",
                        ErrorCode.RESOURCE_NOT_FOUND));

        if (review.getStatus() == ReviewStatus.COMPLETED) {
            throw new BadRequestException(
                    "Completed performance review cannot be deleted.",
                    ErrorCode.NOT_ALLOWED);
        }

        performanceReviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PerformanceReviewResponse> getReviews(
            PerformanceReviewFilterRequest request) {

        Sort sort = Sort.by(
                Sort.Direction.fromString(request.getSortDir()),
                request.getSortBy());

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort);

        Specification<PerformanceReview> specification =
                PerformanceReviewSpecification.filter(request);

        Page<PerformanceReview> reviews =
                performanceReviewRepository.findAll(
                        specification,
                        pageable);

        return reviews.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PerformanceSummaryResponse getSummary(
            PerformanceReviewFilterRequest request) {

        Specification<PerformanceReview> specification =
                PerformanceReviewSpecification.filter(request);

        List<PerformanceReview> reviews =
                performanceReviewRepository.findAll(specification);

        long totalReviews = reviews.size();

        long monthlyReviews = reviews.stream()
                .filter(r -> r.getReviewType() == ReviewType.MONTHLY)
                .count();

        long yearlyReviews = reviews.stream()
                .filter(r -> r.getReviewType() == ReviewType.YEARLY)
                .count();

        long completedReviews = reviews.stream()
                .filter(r -> r.getStatus() == ReviewStatus.COMPLETED)
                .count();

        long draftReviews = reviews.stream()
                .filter(r -> r.getStatus() == ReviewStatus.DRAFT)
                .count();

        double averageScore = reviews.stream()
                .mapToDouble(PerformanceReview::getScore)
                .average()
                .orElse(0.0);

        long excellentCount = reviews.stream()
                .filter(r -> r.getRating() == PerformanceRating.EXCELLENT)
                .count();

        long veryGoodCount = reviews.stream()
                .filter(r -> r.getRating() == PerformanceRating.VERY_GOOD)
                .count();

        long goodCount = reviews.stream()
                .filter(r -> r.getRating() == PerformanceRating.GOOD)
                .count();

        long averageCount = reviews.stream()
                .filter(r -> r.getRating() == PerformanceRating.AVERAGE)
                .count();

        long needsImprovementCount = reviews.stream()
                .filter(r -> r.getRating() == PerformanceRating.NEEDS_IMPROVEMENT)
                .count();

        return PerformanceSummaryResponse.builder()
                .totalReviews(totalReviews)
                .monthlyReviews(monthlyReviews)
                .yearlyReviews(yearlyReviews)
                .completedReviews(completedReviews)
                .draftReviews(draftReviews)
                .averageScore(averageScore)
                .excellentCount(excellentCount)
                .veryGoodCount(veryGoodCount)
                .goodCount(goodCount)
                .averageCount(averageCount)
                .needsImprovementCount(needsImprovementCount)
                .build();
    }

    private PerformanceReviewResponse mapToResponse(
            PerformanceReview review) {

        return PerformanceReviewResponse.builder()
                .id(review.getId())

                .employeeId(review.getEmployee().getId())
                .employeeCode(review.getEmployee().getEmployeeCode())
                .employeeName(
                        review.getEmployee().getFirstName() + " "
                                + review.getEmployee().getLastName())

                .reviewerId(
                        review.getReviewer() != null
                                ? review.getReviewer().getId()
                                : null)

                .reviewerName(
                        review.getReviewer() != null
                                ? review.getReviewer().getFirstName() + " "
                                  + review.getReviewer().getLastName()
                                : null)

                .reviewType(review.getReviewType())
                .reviewMonth(review.getReviewMonth())
                .reviewYear(review.getReviewYear())

                .rating(review.getRating())
                .score(review.getScore())

                .strengths(review.getStrengths())
                .improvements(review.getImprovements())

                .managerFeedback(review.getManagerFeedback())
                .employeeComment(review.getEmployeeComment())

                .reviewDate(review.getReviewDate())
                .status(review.getStatus())

                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())

                .build();
    }
}