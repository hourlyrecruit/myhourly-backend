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

    @Column(nullable = false)
    private Boolean attendanceRegularizationEnabled;

    @Column(nullable = false)
    private Boolean multipleBreaksAllowed;

    @Column(nullable = false)
    private Integer maximumBreakMinutes;


    private Integer MaximumBreaksPerDay;

    @Column(nullable = false)
    private boolean active;
}
