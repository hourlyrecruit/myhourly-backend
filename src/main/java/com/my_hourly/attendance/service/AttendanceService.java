package com.my_hourly.attendance.service;

import com.my_hourly.attendance.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface AttendanceService {
	
	AttendanceResponse checkIn(CheckInRequest request);
	AttendanceResponse checkOut(CheckOutRequest request);
	AttendanceBreakResponse startBreak(AttendanceBreakRequest request);
	List<AttendanceCalendarResponse> getAttendanceCalendar(
	        LocalDate startDate,
	        LocalDate endDate);
	List<AttendanceResponse> getAttendanceHistory();
	AttendanceResponse getTodayAttendance();
	AttendanceBreakResponse endBreak(Long attendanceId);
	AttendanceDashboardResponse getDashboard();
	AttendanceResponse getTodayAttendance(Long employeeId);

	List<AttendanceResponse> getAttendanceHistory(Long employeeId);

	List<AttendanceCalendarResponse> getAttendanceCalendar(
			Long employeeId,
			LocalDate startDate,
			LocalDate endDate);
	List<AttendanceResponse> getTodayAttendanceForAllEmployees();
	

}
