package com.my_hourly.attendance.dto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.attendance.enums.AttendanceType;

public class AttendanceResponse {

    private Long attendanceId;
    private Long employeeId;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Duration workingHours;
    private Duration totalBreakHours;
    private AttendanceStatus attendanceStatus;
    private AttendanceType attendanceType;
    private String remarks;

    public AttendanceResponse() {
    }

	public AttendanceResponse(Long attendanceId, Long employeeId, LocalDate attendanceDate, LocalTime checkInTime,
			LocalTime checkOutTime, Duration workingHours, Duration totalBreakHours,
			AttendanceStatus attendanceStatus, AttendanceType attendanceType, String remarks) {
		super();
		this.attendanceId = attendanceId;
		this.employeeId = employeeId;
		this.attendanceDate = attendanceDate;
		this.checkInTime = checkInTime;
		this.checkOutTime = checkOutTime;
		this.workingHours = workingHours;
		this.totalBreakHours = totalBreakHours;
		this.attendanceStatus = attendanceStatus;
		this.attendanceType = attendanceType;
		this.remarks = remarks;
	}

	public Long getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Long attendanceId) {
		this.attendanceId = attendanceId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public LocalDate getAttendanceDate() {
		return attendanceDate;
	}

	public void setAttendanceDate(LocalDate attendanceDate) {
		this.attendanceDate = attendanceDate;
	}

	public LocalTime getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(LocalTime checkInTime) {
		this.checkInTime = checkInTime;
	}

	public LocalTime getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(LocalTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	} 

	public Duration getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(Duration workingHours) {
		this.workingHours = workingHours;
	}

	public Duration getTotalBreakHours() {
		return totalBreakHours;
	}

	public void setTotalBreakHours(Duration totalBreakHours) {
		this.totalBreakHours = totalBreakHours;
	}

	public AttendanceStatus getAttendanceStatus() {
		return attendanceStatus;
	}

	public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
		this.attendanceStatus = attendanceStatus;
	}

	public AttendanceType getAttendanceType() {
		return attendanceType;
	}

	public void setAttendanceType(AttendanceType attendanceType) {
		this.attendanceType = attendanceType;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

    
}