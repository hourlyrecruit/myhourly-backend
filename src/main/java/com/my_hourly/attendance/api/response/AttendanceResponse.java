package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.BreakType;
import com.my_hourly.attendance.entity.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;

    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private String formattedCheckInTime;

    private LocalDateTime checkOutTime;

    private String formattedCheckOutTime;

    private Integer workingMinutes;

    private String workingHours;

    private Integer totalBreakMinutes;

    private String breakHours;

    private AttendanceStatus attendanceStatus;

    private EmployeeStatus employeeStatus;

    private BreakType currentBreakType;

}
