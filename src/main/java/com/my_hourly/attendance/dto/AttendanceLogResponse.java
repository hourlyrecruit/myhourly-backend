package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceLogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceLogResponse {

	private Long logId;

	private Long attendanceId;

	private Long employeeId;

	private String employeeCode;

	private String employeeName;

	private AttendanceLogType logType;

	private LocalDateTime logTime;

	private LocalDateTime createdAt;
}