package com.my_hourly.attendance.api.request;

import com.my_hourly.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRegularizationDetailRequest {

    @NotNull(message = "Attendance ID is required.")
    private Long attendanceId;

    @NotNull(message = "Requested status is required.")
    private AttendanceStatus requestedStatus;
}
