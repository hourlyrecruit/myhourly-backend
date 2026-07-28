package com.my_hourly.performance.dto.request;

import com.my_hourly.performance.enums.PerformanceRating;
import com.my_hourly.performance.enums.ReviewStatus;
import com.my_hourly.performance.enums.ReviewType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReviewFilterRequest {

    private Long employeeId;

    private Long reviewerId;

    private Long departmentId;

    private ReviewType reviewType;

    private Integer reviewMonth;

    private Integer reviewYear;

    private PerformanceRating rating;

    private ReviewStatus status;

    private Double minScore;

    private Double maxScore;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 10;

    @Builder.Default
    private String sortBy = "reviewDate";

    @Builder.Default
    private String sortDir = "DESC";

}