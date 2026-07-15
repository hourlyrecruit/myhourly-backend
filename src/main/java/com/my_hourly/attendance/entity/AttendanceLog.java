package com.my_hourly.attendance.entity;

import java.time.LocalDateTime;

import com.my_hourly.attendance.enums.AttendanceLogType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="attendance_logs")
public class AttendanceLog {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name="attendance_id")
	private Attendance attendance;
	private Long employeeId;
	@Enumerated(EnumType.STRING)
	private AttendanceLogType logType;
	private LocalDateTime logTime;
	private LocalDateTime createdAt;
	public AttendanceLog(Long id, Attendance attendance, Long employeeId, AttendanceLogType logType,
			LocalDateTime logTime,LocalDateTime createdAt) {
		super();
		this.id = id;
		this.attendance = attendance;
		this.employeeId = employeeId;
		this.logType = logType;
		this.logTime = logTime;
		this.createdAt = createdAt;
	} 
	public AttendanceLog() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Attendance getAttendance() {
		return attendance;
	}
	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public AttendanceLogType getLogType() {
		return logType;
	}
	public void setLogType(AttendanceLogType logType) {
		this.logType = logType;
	}
	public LocalDateTime getLogTime() {
		return logTime;
	}
	public void setLogTime(LocalDateTime logTime) {
		this.logTime = logTime;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
}
