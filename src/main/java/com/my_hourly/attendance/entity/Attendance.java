package com.my_hourly.attendance.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Integer workingMinutes;

    private Integer totalBreakMinutes;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkInLatitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkInLongitude;

    private String checkInAddress;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkOutLatitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal checkOutLongitude;

    private String checkOutAddress;

    @Builder.Default
    @OneToMany(
            mappedBy = "attendance",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AttendanceBreak> breaks = new ArrayList<>();

}
