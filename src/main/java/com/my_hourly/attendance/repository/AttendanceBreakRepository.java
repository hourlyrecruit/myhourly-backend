package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceBreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceBreakRepository extends JpaRepository<AttendanceBreak, Long> {

    List<AttendanceBreak> findByAttendance(Attendance attendance);

    Optional<AttendanceBreak> findFirstByAttendanceAndBreakEndTimeIsNullOrderByBreakStartTimeDesc(
            Attendance attendance
    );

}