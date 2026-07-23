package com.my_hourly.settings.attendance.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettingsResponse {

    private Long id;

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

    private Boolean weekendAttendanceAllowed;

    private Boolean holidayAttendanceAllowed;

    private Boolean lateMarkEnabled;

    private Boolean earlyExitEnabled;

    private Boolean autoCheckoutEnabled;

    private Boolean active;
}
