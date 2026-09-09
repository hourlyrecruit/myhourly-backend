package com.my_hourly.settings.attendance.entity;

import com.my_hourly.settings.BaseSettings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "attendance_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSettings extends BaseSettings {

    @Column(nullable = false)
    private LocalTime officeStartTime;

    @Column(nullable = false)
    private LocalTime officeEndTime;

    @Column(nullable = false)
    private Integer gracePeriodMinutes;

    @Column(nullable = false)
    private Integer minimumWorkingMinutes;

    @Column(nullable = false)
    private Integer halfDayWorkingMinutes;

    @Column(nullable = false)
    private Integer checkoutCutoffMinutes;

    @Column(nullable = false)
    private Boolean overtimeEnabled;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean attendanceRegularizationEnabled;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean multipleBreaksAllowed;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Integer maximumBreakMinutes;

    // DISABLED: no business-logic reader and never seeded (stayed null in DB).
//    private Integer maximumBreaksPerDay;


    @Column(nullable = false)
    private Boolean weekendAttendanceAllowed;

    @Column(nullable = false)
    private Boolean holidayAttendanceAllowed;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean lateMarkEnabled;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean earlyExitEnabled;

    // DISABLED: no business-logic reader — only round-trips through the settings API.
//    @Column(nullable = false)
//    private Boolean autoCheckoutEnabled;


    // NOTE: removed — duplicate `active` field shadowed the one inherited from
    // BaseSettings (this.active vs super.active ambiguity). The inherited
    // BaseSettings#active is the column actually used.
//    @Column(nullable = false)
//    @Builder.Default
//    private boolean active = true;
}
