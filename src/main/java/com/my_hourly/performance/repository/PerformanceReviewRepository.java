package com.my_hourly.performance.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.performance.entity.PerformanceReview;
import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceReviewRepository
        extends JpaRepository<PerformanceReview, Long>,
        JpaSpecificationExecutor<PerformanceReview> {

    List<PerformanceReview> findByEmployee(Employee employee);

    List<PerformanceReview> findByReviewer(Employee reviewer);

    List<PerformanceReview> findByReviewType(ReviewType reviewType);

    List<PerformanceReview> findByReviewYear(Integer reviewYear);

    List<PerformanceReview> findByReviewTypeAndReviewYear(
            ReviewType reviewType,
            Integer reviewYear
    );

    List<PerformanceReview> findByReviewTypeAndReviewMonthAndReviewYear(
            ReviewType reviewType,
            Integer reviewMonth,
            Integer reviewYear
    );

    List<PerformanceReview> findByStatus(ReviewStatus status);

    List<PerformanceReview> findByRating(PerformanceRating rating);

    Optional<PerformanceReview> findByEmployeeAndReviewTypeAndReviewMonthAndReviewYear(
            Employee employee,
            ReviewType reviewType,
            Integer reviewMonth,
            Integer reviewYear
    );

    Optional<PerformanceReview> findByEmployeeAndReviewTypeAndReviewYear(
            Employee employee,
            ReviewType reviewType,
            Integer reviewYear
    );

    boolean existsByEmployeeAndReviewTypeAndReviewMonthAndReviewYear(
            Employee employee,
            ReviewType reviewType,
            Integer reviewMonth,
            Integer reviewYear
    );

    boolean existsByEmployeeAndReviewTypeAndReviewYear(
            Employee employee,
            ReviewType reviewType,
            Integer reviewYear
    );

}