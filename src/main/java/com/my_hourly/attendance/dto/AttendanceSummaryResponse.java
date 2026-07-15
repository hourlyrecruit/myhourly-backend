package com.my_hourly.attendance.dto;

import java.time.Duration;

public class AttendanceSummaryResponse {

    private Long employeeId;
    private Integer month;
    private Integer year;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    private Integer halfDays;
    private Integer lateMarks;
    private Duration totalWorkingHours;
    private Duration totalOvertimeHours;
    private Double averageWorkingHours;
    private Double attendancePercentage;

    public AttendanceSummaryResponse() {
    }

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getPresentDays() {
		return presentDays;
	}

	public void setPresentDays(Integer presentDays) {
		this.presentDays = presentDays;
	}

	public Integer getAbsentDays() {
		return absentDays;
	}

	public void setAbsentDays(Integer absentDays) {
		this.absentDays = absentDays;
	}

	public Integer getLeaveDays() {
		return leaveDays;
	}

	public void setLeaveDays(Integer leaveDays) {
		this.leaveDays = leaveDays;
	}

	public Integer getHalfDays() {
		return halfDays;
	}

	public void setHalfDays(Integer halfDays) {
		this.halfDays = halfDays;
	}

	public Integer getLateMarks() {
		return lateMarks;
	}

	public void setLateMarks(Integer lateMarks) {
		this.lateMarks = lateMarks;
	}

	public Duration getTotalWorkingHours() {
		return totalWorkingHours;
	}

	public void setTotalWorkingHours(Duration totalWorkingHours) {
		this.totalWorkingHours = totalWorkingHours;
	}

	public Duration getTotalOvertimeHours() {
		return totalOvertimeHours;
	}

	public void setTotalOvertimeHours(Duration totalOvertimeHours) {
		this.totalOvertimeHours = totalOvertimeHours;
	}

	public Double getAverageWorkingHours() {
		return averageWorkingHours;
	}

	public void setAverageWorkingHours(Double averageWorkingHours) {
		this.averageWorkingHours = averageWorkingHours;
	}

	public Double getAttendancePercentage() {
		return attendancePercentage;
	}

	public void setAttendancePercentage(Double attendancePercentage) {
		this.attendancePercentage = attendancePercentage;
	}

    
}