package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.attendance.enums.AttendanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

	private Long attendanceId;

	private Long employeeId;

	private String employeeCode;

	private String employeeName;

	private LocalDate attendanceDate;

	private LocalTime checkInTime;

	private LocalTime checkOutTime;

	private Duration workingHours;

	private Duration totalBreakHours;

	private AttendanceStatus attendanceStatus;

	private AttendanceType attendanceType;

	private String remarks;
}