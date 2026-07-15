package com.my_hourly.attendance.dto;

public class AttendanceDashboardResponse {

    private Long totalEmployees;
    private Long presentEmployees;
    private Long absentEmployees;
    private Long leaveEmployees;
    private Long halfDayEmployees;

    public AttendanceDashboardResponse() {
    }

	public Long getTotalEmployees() {
		return totalEmployees;
	}

	public void setTotalEmployees(Long totalEmployees) {
		this.totalEmployees = totalEmployees;
	}

	public Long getPresentEmployees() {
		return presentEmployees;
	}

	public void setPresentEmployees(Long presentEmployees) {
		this.presentEmployees = presentEmployees;
	}

	public Long getAbsentEmployees() {
		return absentEmployees;
	}

	public void setAbsentEmployees(Long absentEmployees) {
		this.absentEmployees = absentEmployees;
	}

	public Long getLeaveEmployees() {
		return leaveEmployees;
	}

	public void setLeaveEmployees(Long leaveEmployees) {
		this.leaveEmployees = leaveEmployees;
	}

	public Long getHalfDayEmployees() {
		return halfDayEmployees;
	}

	public void setHalfDayEmployees(Long halfDayEmployees) {
		this.halfDayEmployees = halfDayEmployees;
	}

    
}