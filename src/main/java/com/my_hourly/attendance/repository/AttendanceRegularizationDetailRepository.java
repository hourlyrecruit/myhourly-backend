package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceRegularizationDetail;
import com.my_hourly.attendance.entity.RegularizationDetailStatus;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRegularizationDetailRepository extends JpaRepository<AttendanceRegularizationDetail, Long> {

    List<AttendanceRegularizationDetail> findByRegularizationId(Long regularizationId);

    @Query("""
            SELECT d FROM AttendanceRegularizationDetail d
            WHERE d.attendance.id = :attendanceId
            AND d.status IN ('PENDING', 'APPROVED')
            """)
    List<AttendanceRegularizationDetail> findActiveByAttendanceId(@Param("attendanceId") Long attendanceId);

    long countByRegularizationIdAndStatus(Long regularizationId, RegularizationDetailStatus status);

    long countByRegularizationId(Long regularizationId);

    @Query("""
            SELECT d FROM AttendanceRegularizationDetail d
            WHERE d.regularization.employee.id = :employeeId
            ORDER BY d.createdAt DESC
            """)
    List<AttendanceRegularizationDetail> findByEmployeeId(@Param("employeeId") Long employeeId);
}
