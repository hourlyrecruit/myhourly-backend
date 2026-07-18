package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.attendance.entity.BreakType;
import com.my_hourly.attendance.entity.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDashboardResponse {

    private LocalDate attendanceDate;

    private boolean checkedIn;

    private boolean checkedOut;

    private AttendanceStatus attendanceStatus;

    private EmployeeStatus employeeStatus;

    private BreakType currentBreakType;

    private String checkInTime;

    private String checkOutTime;

    private Integer workingMinutes;

    private String workingHours;

    private Integer breakMinutes;

    private String breakHours;

}
