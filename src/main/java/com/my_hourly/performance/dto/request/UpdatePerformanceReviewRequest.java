package com.my_hourly.performance.dto.request;

import com.my_hourly.performance.enums.PerformanceRating;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdatePerformanceReviewRequest {

    @NotNull(message = "Performance rating is required")
    private PerformanceRating rating;

    /**
     * Overall score out of 100
     */
    @NotNull(message = "Score is required")
    @DecimalMin(value = "0.0", message = "Score cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Score cannot be greater than 100")
    private Double score;

    @Size(max = 1000, message = "Strengths cannot exceed 1000 characters")
    private String strengths;

    @Size(max = 1000, message = "Improvements cannot exceed 1000 characters")
    private String improvements;

    @Size(max = 2000, message = "Manager feedback cannot exceed 2000 characters")
    private String managerFeedback;

    @Size(max = 2000, message = "Employee comment cannot exceed 2000 characters")
    private String employeeComment;

}