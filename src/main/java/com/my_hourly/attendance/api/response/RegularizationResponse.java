package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.RegularizationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularizationResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private RegularizationStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime rejectedAt;
    private Long rejectedById;
    private String rejectedByName;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private List<RegularizationDetailResponse> details;
}
