package com.my_hourly.attendance.repository;

import com.my_hourly.attendance.entity.AttendanceRegularization;
import com.my_hourly.attendance.entity.RegularizationStatus;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceRegularizationRepository extends JpaRepository<AttendanceRegularization, Long> {

    List<AttendanceRegularization> findByEmployeeOrderByCreatedAtDesc(Employee employee);

    List<AttendanceRegularization> findByStatus(RegularizationStatus status);

    @Query("""
            SELECT r FROM AttendanceRegularization r
            WHERE r.status = :status
            AND r.employee.reportingManager.id = :managerId
            ORDER BY r.createdAt DESC
            """)
    List<AttendanceRegularization> findPendingByManagerId(
            @Param("managerId") Long managerId,
            @Param("status") RegularizationStatus status
    );

    @Query("""
            SELECT r FROM AttendanceRegularization r
            WHERE r.employee.reportingManager.id = :managerId
            ORDER BY r.createdAt DESC
            """)
    List<AttendanceRegularization> findAllByManagerId(@Param("managerId") Long managerId);

    Optional<AttendanceRegularization> findByIdAndEmployeeId(Long id, Long employeeId);

    @Query("""
            SELECT COUNT(r) > 0 FROM AttendanceRegularization r
            JOIN r.details d
            WHERE d.attendance.id = :attendanceId
            AND r.status IN ('PENDING', 'PARTIALLY_APPROVED')
            """)
    boolean existsActiveRegularizationForAttendance(@Param("attendanceId") Long attendanceId);

    @Query("""
            SELECT COUNT(r) > 0 FROM AttendanceRegularization r
            JOIN r.details d
            WHERE d.attendance.id = :attendanceId
            AND r.status = 'APPROVED'
            """)
    boolean existsApprovedRegularizationForAttendance(@Param("attendanceId") Long attendanceId);
}
