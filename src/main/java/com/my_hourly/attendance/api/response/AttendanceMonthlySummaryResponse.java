package com.my_hourly.attendance.api.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceMonthlySummaryResponse {

    private Integer month;

    private Integer year;

    private Long totalAttendanceDays;

    private Long presentDays;

    private Long lateDays;

    private Long halfDays;

    private Long absentDays;

    private Integer totalWorkingMinutes;

    private String totalWorkingHours;

    private Integer averageWorkingMinutes;

    private String averageWorkingHours;

}
