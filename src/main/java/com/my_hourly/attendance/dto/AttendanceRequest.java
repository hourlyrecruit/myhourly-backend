package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceType;

public class AttendanceRequest {
	private Long employeeId;
	private AttendanceType attendanceType;
	public AttendanceRequest() {
		super();
	}
	
	public AttendanceRequest(Long employeeId, AttendanceType attendanceType) {
		super();
		this.employeeId = employeeId;
		this.attendanceType = attendanceType;
	}

	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public AttendanceType getAttendanceType() {
		return attendanceType;
	}
	public void setAttendanceType(AttendanceType attendanceType) {
		this.attendanceType = attendanceType;
	}
	
}
