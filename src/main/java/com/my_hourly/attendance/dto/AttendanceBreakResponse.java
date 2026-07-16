package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.enums.BreakType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBreakResponse {

	private Long breakId;

	private Long attendanceId;

	private Long employeeId;

	private String employeeName;

	private BreakType breakType;

	private LocalDateTime startTime;

	private LocalDateTime endTime;

	private Duration duration;

	private BreakStatus breakStatus;

	private String remarks;
}