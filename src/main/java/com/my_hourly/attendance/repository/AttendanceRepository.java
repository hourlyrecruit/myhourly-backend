package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>,
        JpaSpecificationExecutor<Attendance> {

    Optional<Attendance> findByEmployeeAndAttendanceDate(
            Employee employee,
            LocalDate attendanceDate
    );

    boolean existsByEmployeeAndAttendanceDate(
            Employee employee,
            LocalDate attendanceDate
    );

    List<Attendance> findByEmployeeAndAttendanceDateBetween(
            Employee employee,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Attendance> findByCheckOutTimeIsNull();



    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);




    List<Attendance> findByAttendanceDateAndAttendanceStatusIn(
            LocalDate attendanceDate,
            Collection<AttendanceStatus> statuses
    );

    List<LeaveRequest> findByUpdatedAtAfter(LocalDateTime updatedAt);

    List<Attendance>
    findByAttendanceDateAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(
            LocalDate attendanceDate
    );


}