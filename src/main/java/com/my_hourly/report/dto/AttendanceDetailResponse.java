package com.my_hourly.report.dto;

import com.my_hourly.attendance.entity.AttendanceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class AttendanceDetailResponse {

    private LocalDate attendanceDate;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    private Integer workingMinutes;

    private Integer totalBreakMinutes;

    private Integer lateMinutes;

    private Integer earlyExitMinutes;

    private Integer overtimeMinutes;

    private AttendanceStatus attendanceStatus;

    private List<AttendanceBreakResponse> breaks;
}