package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceBreak;
import com.my_hourly.attendance.enums.BreakStatus;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceBreakRepository extends JpaRepository<AttendanceBreak, Long> {


    List<AttendanceBreak> findByEmployee(Employee employee);
    List<AttendanceBreak> findByAttendance(Attendance attendance);
    Optional<AttendanceBreak> findByAttendanceAndBreakStatus(Attendance attendance, BreakStatus breakStatus);
    Optional<Employee> findByEmployeeCode(String employeeCode);
}