package com.my_hourly.performance.dto.response;

import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewResponse {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private Long reviewerId;

    private String reviewerName;

    private ReviewType reviewType;

    /**
     * Null for YEARLY reviews
     */
    private Integer reviewMonth;

    private Integer reviewYear;

    private PerformanceRating rating;

    /**
     * Score out of 100
     */
    private Double score;

    private String strengths;

    private String improvements;

    private String managerFeedback;

    private String employeeComment;

    private LocalDateTime reviewDate;

    private ReviewStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
