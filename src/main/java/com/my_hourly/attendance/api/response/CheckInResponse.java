package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInResponse {
    private Long id;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private String formattedCheckInTime;

    private AttendanceStatus attendanceStatus;

    private EmployeeStatus employeeStatus;

    private Integer lateMinutes;



}
