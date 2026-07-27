package com.my_hourly.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class AttendanceBreakResponse {

    private LocalTime breakStartTime;

    private LocalTime breakEndTime;

    private Integer breakDurationMinutes;

    private String breakType;
}