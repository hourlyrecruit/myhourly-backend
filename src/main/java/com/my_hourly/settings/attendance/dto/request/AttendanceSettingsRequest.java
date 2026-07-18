package com.my_hourly.settings.attendance.dto.request;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettingsRequest {

    private LocalTime officeStartTime;

    private LocalTime officeEndTime;

    private Integer gracePeriodMinutes;

    private Integer minimumWorkingMinutes;

    private Integer halfDayWorkingMinutes;

    private Integer checkoutCutoffMinutes;

    private Boolean overtimeEnabled;

    private Boolean attendanceRegularizationEnabled;

    private Boolean multipleBreaksAllowed;

    private Integer maximumBreakMinutes;

    private Integer maximumBreaksPerDay;

    private Boolean active;
}
