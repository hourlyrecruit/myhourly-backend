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

    //private LocalDateTime checkInTime;

    private String checkInTime;

   // private LocalDateTime checkOutTime;

    private String checkOutTime;

//    private Integer workingMinutes;

    private String todayWorkingHours;

 //   private Integer totalBreakMinutes;

    private String todayBreakHours;

    private AttendanceStatus attendanceStatus;

    private EmployeeStatus workingStatus;

    private BreakType currentBreakType;

}
