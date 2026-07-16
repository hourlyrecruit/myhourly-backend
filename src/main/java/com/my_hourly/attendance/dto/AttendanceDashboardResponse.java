package com.my_hourly.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
	public class AttendanceDashboardResponse {
		private Long totalEmployees;
		private Long presentEmployees;
		private Long absentEmployees;
		private Long lateEmployees;
		private Long leaveEmployees;

	}