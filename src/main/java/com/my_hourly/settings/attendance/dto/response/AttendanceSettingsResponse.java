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

    // DISABLED: entity field unused (no business-logic reader).
//    private Boolean attendanceRegularizationEnabled;

    // DISABLED: entity field unused (no business-logic reader).
//    private Boolean multipleBreaksAllowed;

    // DISABLED: entity field unused (no business-logic reader).
//    private Integer maximumBreakMinutes;

    // DISABLED: entity field unused (no business-logic reader, never seeded).
//    private Integer maximumBreaksPerDay;

    private Boolean weekendAttendanceAllowed;

    private Boolean holidayAttendanceAllowed;

    // DISABLED: entity field unused (no business-logic reader).
//    private Boolean lateMarkEnabled;

    // DISABLED: entity field unused (no business-logic reader).
//    private Boolean earlyExitEnabled;

    // DISABLED: entity field unused (no business-logic reader).
//    private Boolean autoCheckoutEnabled;

    private Boolean active;
}
