package com.my_hourly.attendance.dto;

import java.time.LocalDateTime;

import com.my_hourly.attendance.enums.AttendanceLogType;

public class AttendanceLogResponse {

    private Long id;
    private AttendanceLogType logType;
    private LocalDateTime logTime;

    public AttendanceLogResponse() {
    }

	public AttendanceLogResponse(Long id, AttendanceLogType logType, LocalDateTime logTime) {
		super();
		this.id = id;
		this.logType = logType;
		this.logTime = logTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

}