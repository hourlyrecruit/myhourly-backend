package com.my_hourly.payroll.dto;

public record AttendanceSummary(
        int totalWorkingDays,
        int workedDays,
        int lopDays
) {
}
