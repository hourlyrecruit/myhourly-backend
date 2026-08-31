package com.my_hourly.attendance.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_regularization_detail")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRegularizationDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regularization_id", nullable = false)
    private AttendanceRegularization regularization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;

    /* ---- Original snapshot (fetched from Attendance by backend) ---- */

    @Enumerated(EnumType.STRING)
    @Column(name = "original_status", nullable = false, length = 30)
    private AttendanceStatus originalStatus;

    @Column(name = "original_check_in")
    private LocalDateTime originalCheckIn;

    @Column(name = "original_check_out")
    private LocalDateTime originalCheckOut;

    /* ---- Requested values (from employee) ---- */

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_status", nullable = false, length = 30)
    private AttendanceStatus requestedStatus;

    @Column(name = "requested_check_in")
    private LocalDateTime requestedCheckIn;

    @Column(name = "requested_check_out")
    private LocalDateTime requestedCheckOut;

    /* ---- Approved values (set by manager on approval) ---- */

    @Enumerated(EnumType.STRING)
    @Column(name = "approved_status", length = 30)
    private AttendanceStatus approvedStatus;

    @Column(name = "approved_check_in")
    private LocalDateTime approvedCheckIn;

    @Column(name = "approved_check_out")
    private LocalDateTime approvedCheckOut;

    /* ---- Detail status ---- */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private RegularizationDetailStatus status = RegularizationDetailStatus.PENDING;

    @Column(length = 500)
    private String remarks;
}
