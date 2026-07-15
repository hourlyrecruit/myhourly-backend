package com.my_hourly.attendance.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.my_hourly.attendance.entity.AttendanceLog;

@Repository
public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
    List<AttendanceLog> findByEmployeeId(Long employeeId);
    List<AttendanceLog> findByAttendanceId(Long attendanceId);
}