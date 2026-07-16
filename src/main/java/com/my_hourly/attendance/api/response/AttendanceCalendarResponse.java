package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceCalendarResponse {

    private LocalDate attendanceDate;

    private AttendanceStatus attendanceStatus;

}