package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCalendarResponse {

    private LocalDate attendanceDate;

    private AttendanceStatus attendanceStatus;

}