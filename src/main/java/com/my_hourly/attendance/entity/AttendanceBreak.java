package com.my_hourly.attendance.entity;

import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.enums.BreakType;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_breaks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBreak {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendance_id", nullable = false)
	private Attendance attendance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;
	private String employeeCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BreakType breakType;

	@Column(nullable = false)
	private LocalDateTime startTime;

	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BreakStatus breakStatus;

	@Column(length = 500)
	private String remarks;
}