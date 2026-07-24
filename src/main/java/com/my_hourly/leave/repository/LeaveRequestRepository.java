package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByEmployeeReportingManager(Employee reportingManager);

    List<LeaveRequest> findByStatus(LeaveStatus status);

    List<LeaveRequest> findByEmployeeAndStatus(Employee employee,
                                               LeaveStatus status);

    boolean existsByEmployeeAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Employee employee,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate);


    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByEmployeeAndStartDateBetween(
            Employee employee,
            LocalDate from,
            LocalDate to);

    List<LeaveRequest> findByStartDateBetween(
            LocalDate from,
            LocalDate to);

    long countByStatus(LeaveStatus status);

    long countByEmployee(Employee employee);

    /**
     * Calculates the total number of HR-approved leave days an employee consumed
     * within a calendar month for a specific leave type.
     *
     * <p>Used by the month-end scheduler to determine the unused portion of the
     * monthly guideline when carry-forward is disabled.</p>
     *
     * @param employee   the employee
     * @param leaveType  the leave type
     * @param monthStart first day of the month (inclusive)
     * @param monthEnd   last day of the month (inclusive)
     * @return total approved days in the month; 0 if none
     */
    @Query("SELECT COALESCE(SUM(lr.totalDays), 0) FROM LeaveRequest lr " +
            "WHERE lr.employee = :employee " +
            "AND lr.leaveType = :leaveType " +
            "AND lr.status = 'HR_APPROVED' " +
            "AND lr.startDate >= :monthStart " +
            "AND lr.startDate <= :monthEnd")
    Integer sumApprovedLeaveDaysInMonth(
            @Param("employee") Employee employee,
            @Param("leaveType") LeaveType leaveType,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);


    @Query("""
            SELECT lr
            FROM LeaveRequest lr
            WHERE lr.status = :status
            AND lr.startDate <= :endDate
            AND lr.endDate >= :startDate
            """)
    List<LeaveRequest> findCalendarLeaves(
            @Param("status") LeaveStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}


