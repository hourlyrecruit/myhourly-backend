package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.BreakType;
import com.my_hourly.attendance.entity.EmployeeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CheckInResponse {
    private Long id;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private String formattedCheckInTime;

    private AttendanceStatus attendanceStatus;

}
