package com.my_hourly.attendance.entity;

import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.attendance.enums.AttendanceType;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	@Column(nullable = false)
	private String employeeCode;

	@Column(nullable = false)
	private LocalDate attendanceDate;

	private LocalTime checkInTime;

	private LocalTime checkOutTime;

	private Duration workingHours;

	private Duration totalBreakHours;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttendanceStatus attendanceStatus;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttendanceType attendanceType;

	@Column(length = 500)
	private String remarks;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}