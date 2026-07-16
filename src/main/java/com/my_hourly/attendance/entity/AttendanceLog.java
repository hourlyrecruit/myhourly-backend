package com.my_hourly.attendance.entity;

import com.my_hourly.attendance.enums.AttendanceLogType;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendance_id", nullable = false)
	private Attendance attendance;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;


	@Column(name = "employee_code")
	private String employeeCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttendanceLogType logType;

	@Column(nullable = false)
	private LocalDateTime logTime;

	@Column(nullable = false)
	private LocalDateTime createdAt;
}