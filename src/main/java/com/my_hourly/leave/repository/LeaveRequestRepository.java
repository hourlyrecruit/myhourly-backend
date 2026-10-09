package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>, JpaSpecificationExecutor<LeaveRequest> {

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

    /**
     * Bulk-deletes every leave request raised against the given leave type.
     * Used only when cascading the deletion of a leave type.
     */
    @Modifying
    @Query("delete from LeaveRequest lr where lr.leaveType.id = :leaveTypeId")
    int deleteByLeaveTypeId(@Param("leaveTypeId") Long leaveTypeId);

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
     * <p>Only {@link LeaveStatus#APPROVED} requests count. Approval is a single
     * stage in this system (see the commented-out HR pass in
     * {@code LeaveRequestServiceImpl}), so there is no MANAGER_APPROVED /
     * HR_APPROVED state to match - the {@code LeaveStatus} enum and the
     * {@code leave_requests.status} check constraint both allow exactly
     * PENDING / APPROVED / REJECTED / CANCELLED.</p>
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
            "AND lr.status = 'APPROVED' " +
            "AND lr.startDate >= :monthStart " +
            "AND lr.startDate <= :monthEnd")
    Integer sumApprovedLeaveDaysInMonth(
            @Param("employee") Employee employee,
            @Param("leaveType") LeaveType leaveType,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * Same calculation as {@link #sumApprovedLeaveDaysInMonth} but for every
     * employee / leave type at once, so the month-end expiry scheduler can
     * replace the per-employee, per-leave-type N+1 queries with a single query.
     */
    interface UsedDaysProjection {
        Long getEmployeeId();

        Long getLeaveTypeId();

        Long getTotalDays();
    }

    @Query("""
            SELECT lr.employee.id AS employeeId,
                   lr.leaveType.id AS leaveTypeId,
                   COALESCE(SUM(lr.totalDays), 0) AS totalDays
            FROM LeaveRequest lr
            WHERE lr.status = 'APPROVED'
              AND lr.startDate >= :monthStart
              AND lr.startDate <= :monthEnd
            GROUP BY lr.employee.id, lr.leaveType.id
            """)
    List<UsedDaysProjection> sumApprovedLeaveDaysInMonthGrouped(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * Total PAID leave days already approved for an employee / leave type in a
     * calendar month - the number the monthly paid-leave allowance is spent on.
     *
     * <p>Only {@link LeaveStatus#APPROVED} requests count, so pending and
     * rejected requests never consume the allowance.</p>
     *
     * <p>{@code COALESCE(paidDays, totalDays)} keeps historical approvals
     * correct: rows created before the PAID/LOP split existed have a null
     * {@code paidDays}, and every one of their days was deducted from the
     * annual balance, so they must count as fully PAID.</p>
     *
     * <p>Like {@link #sumApprovedLeaveDaysInMonth}, this attributes a request to
     * the month its {@code startDate} falls in.</p>
     */
    @Query("SELECT COALESCE(SUM(COALESCE(lr.paidDays, lr.totalDays)), 0) FROM LeaveRequest lr " +
            "WHERE lr.employee = :employee " +
            "AND lr.leaveType = :leaveType " +
            "AND lr.status = 'APPROVED' " +
            "AND lr.startDate >= :monthStart " +
            "AND lr.startDate <= :monthEnd")
    Integer sumPaidLeaveDaysInMonth(
            @Param("employee") Employee employee,
            @Param("leaveType") LeaveType leaveType,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * Loads a leave request under a write lock so two concurrent approvals of
     * the same request serialise: the loser re-reads the already-APPROVED status
     * and is rejected, and a double-click cannot deduct the balance twice.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lr from LeaveRequest lr where lr.id = :id")
    Optional<LeaveRequest> findByIdForUpdate(@Param("id") Long id);


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


    List<LeaveRequest> findByUpdatedAtAfter(LocalDateTime updatedAt);

    List<LeaveRequest> findByUpdatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    @EntityGraph(attributePaths = {
            "employee",
            "employee.department",
            "leaveType",
            "approvedBy"
    })
    @Override
    List<LeaveRequest> findAll(Specification<LeaveRequest> specification);
}


