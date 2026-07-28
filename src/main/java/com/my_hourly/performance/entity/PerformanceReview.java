package com.my_hourly.performance.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "performance_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_review_period",
                        columnNames = {
                                "employee_id",
                                "review_type",
                                "review_month",
                                "review_year"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Employee reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false, length = 20)
    private ReviewType reviewType;

    /**
     * Required only for MONTHLY reviews.
     * Null for YEARLY reviews.
     */
    @Column(name = "review_month")
    private Integer reviewMonth;

    @Column(name = "review_year", nullable = false)
    private Integer reviewYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PerformanceRating rating;

    /**
     * Overall score out of 100.
     */
    @Column(nullable = false)
    private Double score;

    @Column(length = 1000)
    private String strengths;

    @Column(length = 1000)
    private String improvements;

    @Column(length = 2000)
    private String managerFeedback;

    @Column(length = 2000)
    private String employeeComment;

    @Column(nullable = false)
    private LocalDateTime reviewDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewStatus status;

}
