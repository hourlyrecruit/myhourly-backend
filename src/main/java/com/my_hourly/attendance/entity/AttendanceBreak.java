package com.my_hourly.attendance.entity;

import java.time.LocalDateTime;

import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.enums.BreakType;

import jakarta.persistence.Column;
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
@Table(name="attendance_breaks")
public class AttendanceBreak {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;	
	@ManyToOne
	@JoinColumn(name = "attendance_id")
	private Attendance attendance;
	private Long employeeId;
	@Enumerated(EnumType.STRING)
	private BreakType breakType;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	@Enumerated(EnumType.STRING)
	private BreakStatus breakStatus;
	@Column(length=500)
	private String remarks;
	public AttendanceBreak(Long id, Attendance attendance, Long employeeId, BreakType breakType,
			LocalDateTime startTime, LocalDateTime endTime, BreakStatus breakStatus, String remarks) {
		super();
		this.id = id;
		this.attendance = attendance;
		this.employeeId = employeeId;
		this.breakType = breakType;
		this.startTime = startTime;
		this.endTime = endTime;
		this.breakStatus = breakStatus;
		this.remarks = remarks;
	}
	public AttendanceBreak() {
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
	public BreakType getBreakType() {
		return breakType;
	}
	public void setBreakType(BreakType breakType) {
		this.breakType = breakType;
	}
	public LocalDateTime getStartTime() {
		return startTime;
	}
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	public LocalDateTime getEndTime() {
		return endTime;
	}
	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	public BreakStatus getBreakStatus() {
		return breakStatus;
	}
	public void setBreakStatus(BreakStatus breakStatus) {
		this.breakStatus = breakStatus;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
}
