package com.my_hourly.attendance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_policies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendancePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String policyName;

    @Column(nullable = false)
    private LocalTime officeStartTime;

    @Column(nullable = false)
    private LocalTime officeEndTime;

    @Column(nullable = false)
    private Integer gracePeriodMinutes;

    @Column(nullable = false)
    private Integer minimumWorkingHours;

    @Column(nullable = false)
    private Integer halfDayWorkingHours;

    @Column(nullable = false)
    private Boolean saturdayWorking;

    @Column(nullable = false)
    private Boolean sundayWorking;

    @Column(nullable = false)
    private Boolean holidayCheckInAllowed;

    @Column(nullable = false)
    private Boolean overtimeEnabled;

    @Column(nullable = false)
    private Integer overtimeAfterHours;

    @Column(nullable = false)
    private Boolean active;

    @Column(length = 500)
    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}