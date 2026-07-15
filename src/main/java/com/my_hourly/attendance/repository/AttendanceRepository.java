package com.my_hourly.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.enums.AttendanceStatus;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
	Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);
	List<Attendance> findByEmployeeId(Long employeeId);
	List<Attendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
	Long countByAttendanceDate(LocalDate attendanceDate);

	Long countByAttendanceDateAndAttendanceStatus(
	        LocalDate attendanceDate,
	        AttendanceStatus attendanceStatus);
}
