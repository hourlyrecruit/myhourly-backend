package com.my_hourly.attendance.api.request;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.RegularizationDetailStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularizationDetailActionRequest {

    @NotNull(message = "Status is required.")
    private RegularizationDetailStatus status;

    private AttendanceStatus approvedStatus;

    private String remarks;
}
