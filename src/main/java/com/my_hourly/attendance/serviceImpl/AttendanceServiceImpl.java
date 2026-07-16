package com.my_hourly.attendance.serviceImpl;

import com.my_hourly.attendance.dto.*;
import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceBreak;
import com.my_hourly.attendance.entity.AttendanceLog;
import com.my_hourly.attendance.entity.AttendancePolicy;
import com.my_hourly.attendance.enums.AttendanceLogType;
import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.attendance.repository.AttendanceBreakRepository;
import com.my_hourly.attendance.repository.AttendanceLogRepository;
import com.my_hourly.attendance.repository.AttendancePolicyRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceService;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.security.user.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
	@Autowired
	private AttendanceRepository attendanceRepository;

	@Autowired
	private AttendanceBreakRepository attendanceBreakRepository;

	@Autowired
	private AttendanceLogRepository attendanceLogRepository;

	@Autowired
	private AttendancePolicyRepository attendancePolicyRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

// Later
//@Autowired
//private HolidayRepository holidayRepository;

//@Autowired
//private LeaveRepository leaveRepository;
	private AttendancePolicy getPolicy() {
	return attendancePolicyRepository.findByActiveTrue().orElseThrow(() ->
					new RuntimeException("Attendance policy not configured."));
}
	private boolean isWeekend(LocalDate date, AttendancePolicy policy) {
		DayOfWeek day = date.getDayOfWeek();
		if(day == DayOfWeek.SATURDAY && !policy.getSaturdayWorking()) {
			return true;
		}
		if(day == DayOfWeek.SUNDAY && !policy.getSundayWorking()) {
			return true;
		}
		return false;
	}
	//Update later
	private boolean isHoliday(LocalDate date){
		return false;
	}
	//update later from leaveRepository
	private boolean isEmployeeOnLeave(Employee employee, LocalDate date){
		return false;
	}
	private AttendanceStatus getAttendanceStatus(LocalTime checkIn, AttendancePolicy policy){

		LocalTime lateTime = policy.getOfficeStartTime()
					.plusMinutes(policy.getGracePeriodMinutes());

		if(checkIn.isAfter(lateTime)){
			return AttendanceStatus.LATE;
		}
		return AttendanceStatus.PRESENT;
	}
	private Employee getLoggedInEmployee() {

		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null){
			throw new RuntimeException("User not authenticated");
		}

		CustomUserDetails userDetails =
				(CustomUserDetails) authentication.getPrincipal();


		User user = userDetails.getUser();


		return employeeRepository.findByUser(user)
				.orElseThrow(() ->
						new RuntimeException(
								"Employee not linked with this user"));
	}


	@Override
	@Transactional
	public AttendanceResponse checkIn(CheckInRequest request) {

		// Validate Employee
		Employee employee = getLoggedInEmployee();

		// Get Attendance Policy
		AttendancePolicy policy = getPolicy();

		LocalDate today = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Weekend Validation
		if (isWeekend(today, policy)) {
			throw new RuntimeException("Today is a weekend. Check-In is not allowed.");
		}

		// Holiday Validation
		if (isHoliday(today) && !policy.getHolidayCheckInAllowed()) {
			throw new RuntimeException("Check-In is not allowed on holidays.");
		}

		// Leave Validation
		if (isEmployeeOnLeave(employee, today)) {
			throw new RuntimeException("Employee is on leave today.");
		}

		// Duplicate Check-In Validation
		if (attendanceRepository.existsByEmployeeAndAttendanceDate(employee, today)) {
			throw new RuntimeException("Employee has already checked in today.");
		}

		// Create Attendance
		Attendance attendance = new Attendance();
		attendance.setEmployee(employee);
		attendance.setEmployeeCode(employee.getEmployeeCode());
		attendance.setAttendanceDate(today);
		attendance.setCheckInTime(currentTime);
		attendance.setAttendanceType(request.getAttendanceType());
		attendance.setAttendanceStatus(getAttendanceStatus(currentTime, policy));
		attendance.setRemarks(request.getRemarks());
		attendance.setCreatedAt(currentDateTime);
		attendance.setUpdatedAt(currentDateTime);

		attendance = attendanceRepository.save(attendance);

		// Create Attendance Log
		AttendanceLog attendanceLog = AttendanceLog.builder()
				.attendance(attendance)
				.employee(employee)
				.employeeCode(employee.getEmployeeCode())
				.logType(AttendanceLogType.CHECK_IN)
				.logTime(currentDateTime)
				.createdAt(currentDateTime)
				.build();

		attendanceLogRepository.save(attendanceLog);

		// Return Response
		return mapAttendance(attendance);
	}
	private Duration calculateWorkingHours(Attendance attendance) {
		Duration totalHours = Duration.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
		if (attendance.getTotalBreakHours() != null) {
			totalHours = totalHours.minus(attendance.getTotalBreakHours());
		}
		return totalHours;
	}
	private AttendanceStatus getFinalAttendanceStatus(Attendance attendance, AttendancePolicy policy) {
		Duration workingHours = attendance.getWorkingHours();
		long workedHours = workingHours.toHours();
		if (workedHours < policy.getHalfDayWorkingHours()) {
			return AttendanceStatus.HALF_DAY;
		}
		if (attendance.getAttendanceStatus() == AttendanceStatus.LATE) {
			return AttendanceStatus.LATE;
		}
		return AttendanceStatus.PRESENT;
	}
	private void closeRunningBreak(Attendance attendance) {
		AttendanceBreak activeBreak = attendanceBreakRepository
						.findByAttendanceAndBreakStatus(attendance, BreakStatus.STARTED)
						.orElse(null);
		if (activeBreak == null) {
			return;
		}
		activeBreak.setEndTime(LocalDateTime.now());
		activeBreak.setBreakStatus(BreakStatus.ENDED);
		attendanceBreakRepository.save(activeBreak);

		Duration breakDuration = Duration.between(activeBreak.getStartTime(), activeBreak.getEndTime());

		if (attendance.getTotalBreakHours() == null) {
			attendance.setTotalBreakHours(breakDuration);
		} else {
			attendance.setTotalBreakHours(
					attendance.getTotalBreakHours().plus(breakDuration));
		}
	}
	@Override
	@Transactional
	public AttendanceResponse checkOut(CheckOutRequest request) {

		// Validate Employee
		Employee employee = getLoggedInEmployee();

		LocalDate today = LocalDate.now();
		LocalTime currentTime = LocalTime.now();
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Get Today's Attendance
		Attendance attendance = attendanceRepository
				.findByEmployeeAndAttendanceDate(employee, today)
				.orElseThrow(() -> new RuntimeException("No check-in found for today."));

		// Prevent Duplicate Check-Out
		if (attendance.getCheckOutTime() != null) {
			throw new RuntimeException("Employee has already checked out today.");
		}

		// Get Attendance Policy
		AttendancePolicy policy = getPolicy();

		// Auto Close Running Break (if any)
		closeRunningBreak(attendance);

		// Update Attendance
		attendance.setCheckOutTime(currentTime);
		attendance.setWorkingHours(calculateWorkingHours(attendance));
		attendance.setAttendanceStatus(getFinalAttendanceStatus(attendance, policy));
		attendance.setRemarks(request.getRemarks());
		attendance.setUpdatedAt(currentDateTime);

		attendance = attendanceRepository.save(attendance);

		// Create Attendance Log
		AttendanceLog attendanceLog = AttendanceLog.builder()
				.attendance(attendance)
				.employee(employee)
				.employeeCode(employee.getEmployeeCode())
				.logType(AttendanceLogType.CHECK_OUT)
				.logTime(currentDateTime)
				.createdAt(currentDateTime)
				.build();

		attendanceLogRepository.save(attendanceLog);

		// Return Response
		return mapAttendance(attendance);
	}
	private AttendanceResponse mapAttendance(Attendance attendance) {

		Employee employee = attendance.getEmployee();

		AttendanceResponse response = new AttendanceResponse();

		response.setAttendanceId(attendance.getId());

		response.setEmployeeId(employee.getId());

		response.setEmployeeCode(employee.getEmployeeCode());

		response.setEmployeeName(
				employee.getFirstName() +
						(employee.getLastName() != null
								? " " + employee.getLastName()
								: "")
		);

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
	@Transactional
	public AttendanceBreakResponse startBreak(AttendanceBreakRequest request) {

		Employee employee = getLoggedInEmployee();

		Attendance attendance = attendanceRepository
				.findByEmployeeAndAttendanceDate(employee, LocalDate.now())
				.orElseThrow(() -> new RuntimeException("Please check in first."));

		if (attendance.getCheckOutTime() != null) {
			throw new RuntimeException("Cannot start break after checkout.");
		}

		Optional<AttendanceBreak> activeBreak =
				attendanceBreakRepository.findByAttendanceAndBreakStatus(
						attendance,
						BreakStatus.STARTED);

		if (activeBreak.isPresent()) {
			throw new RuntimeException("A break is already in progress.");
		}

		LocalDateTime currentDateTime = LocalDateTime.now();

		AttendanceBreak attendanceBreak = AttendanceBreak.builder()
				.attendance(attendance)
				.employee(employee)
				.breakType(request.getBreakType())
				.startTime(currentDateTime)
				.breakStatus(BreakStatus.STARTED)
				.remarks(request.getRemarks())
				.build();

		attendanceBreak = attendanceBreakRepository.save(attendanceBreak);

		AttendanceLog attendanceLog = AttendanceLog.builder()
				.attendance(attendance)
				.employee(employee)
				.employeeCode(employee.getEmployeeCode())
				.logType(AttendanceLogType.BREAK_START)
				.logTime(currentDateTime)
				.createdAt(currentDateTime)
				.build();

		attendanceLogRepository.save(attendanceLog);

		return mapBreak(attendanceBreak);
	}
	@Override
	@Transactional
	public AttendanceBreakResponse endBreak(Long attendanceId) {

		Attendance attendance = attendanceRepository.findById(attendanceId)
				.orElseThrow(() -> new RuntimeException("Attendance not found."));

		if (attendance.getCheckOutTime() != null) {
			throw new RuntimeException("Employee has already checked out.");
		}

		AttendanceBreak attendanceBreak = attendanceBreakRepository
				.findByAttendanceAndBreakStatus(
						attendance,
						BreakStatus.STARTED)
				.orElseThrow(() -> new RuntimeException("No active break found."));

		LocalDateTime currentDateTime = LocalDateTime.now();

		attendanceBreak.setEndTime(currentDateTime);
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

		attendance.setUpdatedAt(currentDateTime);

		attendanceRepository.save(attendance);

		Employee employee = attendance.getEmployee();

		AttendanceLog attendanceLog = AttendanceLog.builder()
				.attendance(attendance)
				.employee(employee)
				.employeeCode(employee.getEmployeeCode())
				.logType(AttendanceLogType.BREAK_END)
				.logTime(currentDateTime)
				.createdAt(currentDateTime)
				.build();

		attendanceLogRepository.save(attendanceLog);

		return mapBreak(attendanceBreak);
	}

	private AttendanceBreakResponse mapBreak(AttendanceBreak attendanceBreak) {

		Employee employee = attendanceBreak.getEmployee();

		AttendanceBreakResponse response = new AttendanceBreakResponse();

		response.setBreakId(attendanceBreak.getId());

		response.setAttendanceId(attendanceBreak.getAttendance().getId());

		response.setEmployeeId(employee.getId());

		response.setEmployeeName(
				employee.getFirstName() +
						(employee.getLastName() != null
								? " " + employee.getLastName()
								: "")
		);

		response.setBreakType(attendanceBreak.getBreakType());

		response.setStartTime(attendanceBreak.getStartTime());

		response.setEndTime(attendanceBreak.getEndTime());

		if (attendanceBreak.getStartTime() != null &&
				attendanceBreak.getEndTime() != null) {

			response.setDuration(Duration.between(
					attendanceBreak.getStartTime(),
					attendanceBreak.getEndTime()));
		}

		response.setBreakStatus(attendanceBreak.getBreakStatus());

		response.setRemarks(attendanceBreak.getRemarks());

		return response;
	}

	@Override
	public List<AttendanceCalendarResponse> getAttendanceCalendar(
			LocalDate startDate,
			LocalDate endDate) {

		Employee employee = getLoggedInEmployee();

		return attendanceRepository
				.findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate)
				.stream()
				.map(attendance -> {

					AttendanceCalendarResponse response =
							new AttendanceCalendarResponse();

					response.setAttendanceDate(attendance.getAttendanceDate());
					response.setAttendanceStatus(attendance.getAttendanceStatus());

					return response;
				})
				.toList();
	}

	@Override
	public List<AttendanceResponse> getAttendanceHistory() {

		Employee employee = getLoggedInEmployee();

		return attendanceRepository.findByEmployee(employee)
				.stream()
				.sorted(Comparator.comparing(Attendance::getAttendanceDate).reversed())
				.map(this::mapAttendance)
				.toList();
	}
	@Override
	public AttendanceResponse getTodayAttendance() {

		Employee employee = getLoggedInEmployee();

		Attendance attendance = attendanceRepository
				.findByEmployeeAndAttendanceDate(employee, LocalDate.now())
				.orElseThrow(() -> new RuntimeException("Attendance not found."));

		return mapAttendance(attendance);
	}
	@Override
	public AttendanceDashboardResponse getDashboard() {
		LocalDate today = LocalDate.now();
		AttendanceDashboardResponse response = new AttendanceDashboardResponse();
		long totalEmployees = employeeRepository.count();
		long present = attendanceRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.PRESENT);
		long late = attendanceRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.LATE);
		long leave = attendanceRepository.countByAttendanceDateAndAttendanceStatus(today, AttendanceStatus.ON_LEAVE);
		long absent = totalEmployees - (present + late + leave);
		response.setTotalEmployees(totalEmployees);
		response.setPresentEmployees(present);
		response.setLateEmployees(late);
		response.setLeaveEmployees(leave);
		response.setAbsentEmployees(absent);
		return response;
	}

	@Override
	public AttendanceResponse getTodayAttendance(Long employeeId) {

		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException("Employee not found."));

		Attendance attendance = attendanceRepository
				.findByEmployeeAndAttendanceDate(employee, LocalDate.now())
				.orElseThrow(() -> new RuntimeException("Today's attendance not found."));

		return mapAttendance(attendance);
	}
	@Override
	public List<AttendanceResponse> getAttendanceHistory(Long employeeId) {

		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException("Employee not found."));

		return attendanceRepository.findByEmployee(employee)
				.stream()
				.sorted(Comparator.comparing(Attendance::getAttendanceDate).reversed())
				.map(this::mapAttendance)
				.toList();
	}
	@Override
	public List<AttendanceCalendarResponse> getAttendanceCalendar(
			Long employeeId,
			LocalDate startDate,
			LocalDate endDate) {

		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new RuntimeException("Employee not found."));

		List<Attendance> attendances = attendanceRepository
				.findByEmployeeAndAttendanceDateBetween(
						employee,
						startDate,
						endDate);

		return attendances.stream()
				.map(attendance -> {
					AttendanceCalendarResponse response =
							new AttendanceCalendarResponse();

					response.setAttendanceDate(attendance.getAttendanceDate());
					response.setAttendanceStatus(attendance.getAttendanceStatus());

					return response;
				})
				.toList();
	}
	@Override
	public List<AttendanceResponse> getTodayAttendanceForAllEmployees() {

		LocalDate today = LocalDate.now();

		List<Employee> employees = employeeRepository.findByActiveTrueOrderByFirstNameAsc();

		List<AttendanceResponse> responses = new ArrayList<>();

		for (Employee employee : employees) {

			Optional<Attendance> attendance =
					attendanceRepository.findByEmployeeAndAttendanceDate(employee, today);

			if (attendance.isPresent()) {

				responses.add(mapAttendance(attendance.get()));

			} else {

				AttendanceResponse response = new AttendanceResponse();

				response.setEmployeeId(employee.getId());
				response.setEmployeeCode(employee.getEmployeeCode());
				response.setEmployeeName(
						employee.getFirstName() +
								(employee.getLastName() != null
										? " " + employee.getLastName()
										: "")
				);

				response.setAttendanceDate(today);
				response.setAttendanceStatus(AttendanceStatus.ABSENT);

				responses.add(response);
			}
		}

		return responses;
	}

}
