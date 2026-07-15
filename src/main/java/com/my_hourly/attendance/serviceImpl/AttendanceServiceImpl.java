package com.my_hourly.attendance.serviceImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.attendance.dto.AttendanceBreakRequest;
import com.my_hourly.attendance.dto.AttendanceBreakResponse;
import com.my_hourly.attendance.dto.AttendanceCalendarResponse;
import com.my_hourly.attendance.dto.AttendanceDashboardResponse;
import com.my_hourly.attendance.dto.AttendanceResponse;
import com.my_hourly.attendance.dto.CheckInRequest;
import com.my_hourly.attendance.dto.CheckOutRequest;
import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceBreak;
import com.my_hourly.attendance.entity.AttendanceLog;
import com.my_hourly.attendance.enums.AttendanceLogType;
import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.repository.AttendanceBreakRepository;
import com.my_hourly.attendance.repository.AttendanceLogRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceService;
@Service
public class AttendanceServiceImpl implements AttendanceService {
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private AttendanceLogRepository attendanceLogRepository;
	@Autowired
	private AttendanceBreakRepository attendanceBreakRepository;


	@Override
	public AttendanceResponse checkIn(CheckInRequest request) {
		Optional<Attendance> existingAttendance =  
				attendanceRepository.findByEmployeeIdAndAttendanceDate
				(request.getEmployeeId(), LocalDate.now());
		if(existingAttendance.isPresent()) {
			throw new RuntimeException("Employee has already CheckedIn today.");
		}
		Attendance attendance = new Attendance();
		attendance.setEmployeeId(request.getEmployeeId());
		attendance.setAttendanceDate(LocalDate.now());
		attendance.setCheckInTime(LocalTime.now());
		attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
		attendance.setAttendanceType(request.getAttendanceType());
		attendance.setRemarks(request.getRemarks());
		attendance.setCreatedAt(LocalDateTime.now());
		attendance.setUpdatedAt(LocalDateTime.now());
		
		attendance = attendanceRepository.save(attendance);
		
		AttendanceLog log = new AttendanceLog();
		log.setAttendance(attendance);
		log.setEmployeeId(attendance.getEmployeeId());
		log.setLogType(AttendanceLogType.CHECK_IN);
		log.setLogTime(LocalDateTime.now());
		log.setCreatedAt(LocalDateTime.now());
		
		attendanceLogRepository.save(log); 
		return mapAttendance(attendance);
		
	}

	@Override
	public AttendanceResponse checkOut(CheckOutRequest request) {
		Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(
				request.getEmployeeId(),LocalDate.now()).orElseThrow(
						()->new RuntimeException("No Check-in found for today")); 
		if(attendance.getCheckOutTime() != null) {
			throw new RuntimeException("Employee has already Checked out");
		}
		attendance.setCheckOutTime(LocalTime.now());
		
		Duration workingHours = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
		
		attendance.setWorkingHours(workingHours);
		attendance.setUpdatedAt(LocalDateTime.now());
		attendance.setRemarks(request.getRemarks());
		attendanceRepository.save(attendance);
		
		AttendanceLog log = new AttendanceLog();
		log.setAttendance(attendance);
		log.setEmployeeId(attendance.getEmployeeId());
		log.setLogType(AttendanceLogType.CHECK_OUT); 
		log.setLogTime(LocalDateTime.now());
		log.setCreatedAt(LocalDateTime.now());
		attendanceLogRepository.save(log);
		return mapAttendance(attendance); 
	}
    private AttendanceResponse mapAttendance(Attendance attendance) {

        AttendanceResponse response = new AttendanceResponse();

        response.setAttendanceId(attendance.getId());
        response.setEmployeeId(attendance.getEmployeeId());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setCheckInTime(attendance.getCheckInTime());
        response.setCheckOutTime(attendance.getCheckOutTime());
        response.setWorkingHours(attendance.getWorkingHours());
        response.setTotalBreakHours(attendance.getTotalBreakHours());
        response.setAttendanceStatus(attendance.getAttendanceStatus());
        response.setAttendanceType(attendance.getAttendanceType());
        response.setRemarks(attendance.getRemarks());
        return response;
    }

	@Override
	public AttendanceBreakResponse startBreak(AttendanceBreakRequest request) {
		Attendance attendance = attendanceRepository.findById(request.getAttendanceId()).orElseThrow( 
				()-> new RuntimeException("Attendance Not Found.") );
		 Optional<AttendanceBreak> activeBreak = attendanceBreakRepository.findByAttendanceIdAndBreakStatus(
		                    request.getAttendanceId(),
		                    BreakStatus.STARTED); 
		 if(activeBreak.isPresent()) {
			 throw new RuntimeException("Break Already Started.");
		 }
		 if (attendance.getCheckOutTime() != null) {
			    throw new RuntimeException("Cannot start break after check-out.");
			}
		 AttendanceBreak attendanceBreak = new AttendanceBreak();
		 attendanceBreak.setAttendance(attendance);
		 attendanceBreak.setEmployeeId(request.getEmployeeId());
		 attendanceBreak.setBreakType(request.getBreakType());
		 attendanceBreak.setStartTime(LocalDateTime.now());
		 attendanceBreak.setBreakStatus(BreakStatus.STARTED);
		 attendanceBreak.setRemarks(request.getRemarks());
		 
		 attendanceBreak = attendanceBreakRepository.save(attendanceBreak);  
		 
		 AttendanceLog log = new AttendanceLog();
		 log.setAttendance(attendance);
		 log.setEmployeeId(request.getEmployeeId());
		 log.setLogType(AttendanceLogType.BREAK_START);
		 log.setLogTime(LocalDateTime.now());
		 log.setCreatedAt(LocalDateTime.now());
		 
		 attendanceLogRepository.save(log);
		return mapBreak(attendanceBreak);
	}

	@Override
	public AttendanceBreakResponse endBreak(Long attendanceId) {

	    Attendance attendance = attendanceRepository
	            .findById(attendanceId)
	            .orElseThrow(() ->
	                    new RuntimeException("Attendance not found."));

	    AttendanceBreak attendanceBreak = attendanceBreakRepository
	            .findByAttendanceIdAndBreakStatus(
	                    attendanceId,
	                    BreakStatus.STARTED)
	            .orElseThrow(() ->
	                    new RuntimeException("No active break found."));
	    if (attendance.getCheckOutTime() != null) {
	        throw new RuntimeException("Cannot end break after check-out.");
	    }

	    attendanceBreak.setEndTime(LocalDateTime.now());
	    attendanceBreak.setBreakStatus(BreakStatus.ENDED);

	    attendanceBreak = attendanceBreakRepository.save(attendanceBreak);

	    Duration breakDuration = Duration.between(
	            attendanceBreak.getStartTime(),
	            attendanceBreak.getEndTime());
	    if (attendance.getTotalBreakHours() == null) {
	        attendance.setTotalBreakHours(breakDuration);
	    } else {
	        attendance.setTotalBreakHours(
	                attendance.getTotalBreakHours().plus(breakDuration));
	    }
	    attendance.setUpdatedAt(LocalDateTime.now());
	    attendanceRepository.save(attendance);

	    AttendanceLog log = new AttendanceLog();

	    log.setAttendance(attendance);
	    log.setEmployeeId(attendance.getEmployeeId());
	    log.setLogType(AttendanceLogType.BREAK_END);
	    log.setLogTime(LocalDateTime.now());
	    log.setCreatedAt(LocalDateTime.now());

	    attendanceLogRepository.save(log);

	    return mapBreak(attendanceBreak);
	}
	
	
	private AttendanceBreakResponse mapBreak(AttendanceBreak attendanceBreak) {

	    AttendanceBreakResponse response = new AttendanceBreakResponse();

	    response.setId(attendanceBreak.getId());
	    response.setBreakType(attendanceBreak.getBreakType());
	    response.setStartTime(attendanceBreak.getStartTime());
	    response.setEndTime(attendanceBreak.getEndTime());
	    response.setStatus(attendanceBreak.getBreakStatus());
	    response.setRemarks(attendanceBreak.getRemarks());
	    if (attendanceBreak.getStartTime() != null &&
	            attendanceBreak.getEndTime() != null) {
	        response.setDuration(Duration.between(
	                attendanceBreak.getStartTime(),
	                attendanceBreak.getEndTime()));
	    }
	    return response;
	}

	@Override
	public List<AttendanceCalendarResponse> getAttendanceCalendar(
	        Long employeeId,
	        LocalDate startDate,
	        LocalDate endDate) {

	    List<Attendance> attendanceList =
	            attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(
	                    employeeId,
	                    startDate,
	                    endDate);

	    return attendanceList.stream()
	            .map(attendance -> new AttendanceCalendarResponse(
	                    attendance.getAttendanceDate(),
	                    attendance.getAttendanceStatus()))
	            .toList();
	}



	@Override
	public List<AttendanceResponse> getAttendanceHistory(Long employeeId) {

	    List<Attendance> attendanceList =
	            attendanceRepository.findByEmployeeId(employeeId);

	    return attendanceList.stream()
	            .map(this::mapAttendance)
	            .toList();
	}
	@Override
	public AttendanceResponse getTodayAttendance(Long employeeId) {

	    Attendance attendance = attendanceRepository
	            .findByEmployeeIdAndAttendanceDate(
	                    employeeId,
	                    LocalDate.now())
	            .orElseThrow(() ->
	                    new RuntimeException("Today's attendance not found."));

	    return mapAttendance(attendance);
	}
//	@Override
//	public AttendanceDashboardResponse getDashboardAttendance() {
//
//	    LocalDate today = LocalDate.now();
//
//	    AttendanceDashboardResponse response =
//	            new AttendanceDashboardResponse();
//
//	    response.setTotalEmployees(employeeRepository.count());
//	    response.setPresentEmployees(
//	            attendanceRepository.countByAttendanceDateAndAttendanceStatus(
//	                    today,
//	                    AttendanceStatus.PRESENT));
//	    response.setAbsentEmployees(
//	            attendanceRepository.countByAttendanceDateAndAttendanceStatus(
//	                    today,
//	                    AttendanceStatus.ABSENT));
//	    response.setLeaveEmployees(
//	            attendanceRepository.countByAttendanceDateAndAttendanceStatus(
//	                    today,
//	                    AttendanceStatus.ON_LEAVE));
//	    response.setHalfDayEmployees(
//	            attendanceRepository.countByAttendanceDateAndAttendanceStatus(
//	                    today,
//	                    AttendanceStatus.HALF_DAY));
//	    return response;
//	}


}
