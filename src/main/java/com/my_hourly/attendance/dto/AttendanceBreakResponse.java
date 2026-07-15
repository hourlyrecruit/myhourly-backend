package com.my_hourly.attendance.dto;


import java.time.Duration;
import java.time.LocalDateTime;

import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.enums.BreakType;

public class AttendanceBreakResponse {

    private Long id;
    private BreakType breakType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;
    private BreakStatus status;
    private String remarks;

    public AttendanceBreakResponse() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public BreakStatus getStatus() {
		return status;
	}

	public void setStatus(BreakStatus status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

    
}