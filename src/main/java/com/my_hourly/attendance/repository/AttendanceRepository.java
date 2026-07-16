package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.enums.AttendanceStatus;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {


	Optional<Attendance> findByEmployeeAndAttendanceDate(
			Employee employee,
			LocalDate attendanceDate);

	Optional<Employee> findByEmployeeCode(String employeeCode);
	List<Attendance> findByEmployee(Employee employee);


	List<Attendance> findByEmployeeAndAttendanceDateBetween(
			Employee employee,
			LocalDate startDate,
			LocalDate endDate);


	Long countByAttendanceDate(LocalDate attendanceDate);

	Long countByAttendanceDateAndAttendanceStatus(
			LocalDate attendanceDate,
			AttendanceStatus attendanceStatus);


	List<Attendance> findByEmployeeAndAttendanceStatus(
			Employee employee,
			AttendanceStatus attendanceStatus);


	boolean existsByEmployeeAndAttendanceDate(
			Employee employee,
			LocalDate attendanceDate);
	List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

}