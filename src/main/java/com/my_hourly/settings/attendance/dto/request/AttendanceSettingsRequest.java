package com.my_hourly.settings.attendance.dto.request;

import jakarta.persistence.Column;
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

}
