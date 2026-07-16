package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceLog;
import com.my_hourly.attendance.enums.AttendanceLogType;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {


    List<AttendanceLog> findByEmployee(Employee employee);


    List<AttendanceLog> findByAttendance(Attendance attendance);


    List<AttendanceLog> findByEmployeeAndLogType(
            Employee employee,
            AttendanceLogType logType
    );
    Optional<Employee> findByEmployeeCode(String employeeCode);

    List<AttendanceLog> findByAttendanceAndLogType(
            Attendance attendance,
            AttendanceLogType logType
    );

}