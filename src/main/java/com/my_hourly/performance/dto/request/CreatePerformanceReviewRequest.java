package com.my_hourly.performance.dto.request;

import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewType;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePerformanceReviewRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    /**
     * Reviewer (Manager)
     */
    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    @NotNull(message = "Review type is required")
    private ReviewType reviewType;

    /**
     * Required only for MONTHLY review.
     * Valid values: 1-12
     */
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer reviewMonth;

    @NotNull(message = "Review year is required")
    @Min(value = 2024, message = "Invalid review year")
    private Integer reviewYear;

    @NotNull(message = "Performance rating is required")
    private PerformanceRating rating;

    /**
     * Overall score out of 100
     */
    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score cannot be negative")
    @DecimalMax(value = "100.0", message = "Score cannot exceed 100")
    private Double score;

    @Size(max = 1000)
    private String strengths;

    @Size(max = 1000)
    private String improvements;

    @Size(max = 2000)
    private String managerFeedback;

    @Size(max = 2000)
    private String employeeComment;

}