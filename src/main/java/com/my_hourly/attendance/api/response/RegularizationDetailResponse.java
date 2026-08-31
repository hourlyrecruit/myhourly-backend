package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.RegularizationDetailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegularizationDetailResponse {

    private Long id;
    private Long regularizationId;
    private Long attendanceId;
    private LocalDate attendanceDate;

    /* Original snapshot */
    private AttendanceStatus originalStatus;
    private String originalCheckIn;
    private String originalCheckOut;

    /* Requested */
    private AttendanceStatus requestedStatus;
    private String requestedCheckIn;
    private String requestedCheckOut;

    /* Approved */
    private AttendanceStatus approvedStatus;
    private String approvedCheckIn;
    private String approvedCheckOut;

    /* Status */
    private RegularizationDetailStatus status;
    private String remarks;
}
