package com.my_hourly.attendance.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.my_hourly.attendance.entity.AttendanceBreak;
import com.my_hourly.attendance.enums.BreakStatus;

@Repository
public interface AttendanceBreakRepository extends JpaRepository<AttendanceBreak, Long> {
    List<AttendanceBreak> findByEmployeeId(Long employeeId);
    List<AttendanceBreak> findByAttendanceId(Long attendanceId);
    Optional<AttendanceBreak> findByAttendanceIdAndBreakStatus(Long attendanceId, BreakStatus breakStatus);

}