package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceType;

public class CheckInRequest {

    private Long employeeId;
    private AttendanceType attendanceType;
    private String remarks;

    public CheckInRequest() {
    }

    public CheckInRequest(Long employeeId, AttendanceType attendanceType, String remarks) {
        this.employeeId = employeeId;
        this.setAttendanceType(attendanceType);
        this.remarks = remarks;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

	public AttendanceType getAttendanceType() {
		return attendanceType;
	}

	public void setAttendanceType(AttendanceType attendanceType) {
		this.attendanceType = attendanceType;
	}
}