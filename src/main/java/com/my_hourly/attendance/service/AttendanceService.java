package com.my_hourly.attendance.service;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.my_hourly.attendance.dto.AttendanceBreakRequest;
import com.my_hourly.attendance.dto.AttendanceBreakResponse;
import com.my_hourly.attendance.dto.AttendanceCalendarResponse;
import com.my_hourly.attendance.dto.AttendanceResponse;
import com.my_hourly.attendance.dto.CheckInRequest;
import com.my_hourly.attendance.dto.CheckOutRequest;
@Service 
public interface AttendanceService {
	
	AttendanceResponse checkIn(CheckInRequest request);
	AttendanceResponse checkOut(CheckOutRequest request);
	AttendanceBreakResponse startBreak(AttendanceBreakRequest request);
	List<AttendanceCalendarResponse> getAttendanceCalendar(
	        Long employeeId,
	        LocalDate startDate,
	        LocalDate endDate);
	List<AttendanceResponse> getAttendanceHistory(Long employeeId);
	AttendanceResponse getTodayAttendance(Long employeeId);
	AttendanceBreakResponse endBreak(Long attendanceId);
	
	

}
