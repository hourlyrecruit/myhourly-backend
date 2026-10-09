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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // -----------------------------------------------------------------------
    // Monthly PAID-leave usage - the number the monthly guideline spends.
    //
    // Usage is counted PER EMPLOYEE, PER CALENDAR MONTH, BY THE DATES THE
    // LEAVE DAYS ACTUALLY FALL ON (the guideline is one allowance per
    // employee across all paid leave types).
    //
    // Two sources are merged by the default methods below:
    //   1. leave_request_month_allocations - the per-month breakdown stored
    //      at approval time. Authoritative: a day is counted in the month it
    //      falls in, exactly once, no matter where the request starts.
    //   2. legacy fallback - requests approved before that table existed (no
    //      allocation rows). Only their aggregate paid/total exists, so they
    //      keep the old start-date attribution; COALESCE(paidDays, totalDays)
    //      treats a null paidDays as "fully deducted" (pre-split approvals).
    //      Rows of paid=false leave types are excluded: their days are not
    //      salary-paid and never consumed the paid allowance.
    //
    // Only APPROVED requests count, so pending and rejected requests never
    // consume the allowance.
    // -----------------------------------------------------------------------

    /**
     * PAID days with per-month attribution (rows approved after V10).
     */
    @Query("""
            SELECT COALESCE(SUM(a.paidDays), 0)
            FROM LeaveRequestMonthAllocation a
            WHERE a.leaveRequest.employee = :employee
              AND a.leaveRequest.status = 'APPROVED'
              AND a.allocationMonth >= :monthStart
              AND a.allocationMonth <= :monthEnd
            """)
    Integer sumAllocatedPaidLeaveDaysInMonth(
            @Param("employee") Employee employee,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * PAID days of requests that predate the per-month allocation table,
     * kept on the old start-date attribution because their per-month split
     * was never stored.
     */
    @Query("""
            SELECT COALESCE(SUM(COALESCE(lr.paidDays, lr.totalDays)), 0)
            FROM LeaveRequest lr
            WHERE lr.employee = :employee
              AND lr.leaveType.paid = true
              AND lr.status = 'APPROVED'
              AND lr.startDate >= :monthStart
              AND lr.startDate <= :monthEnd
              AND NOT EXISTS (
                  SELECT 1 FROM LeaveRequestMonthAllocation a
                  WHERE a.leaveRequest = lr
              )
            """)
    Integer sumLegacyPaidLeaveDaysInMonth(
            @Param("employee") Employee employee,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * Total PAID leave days already approved for an employee in a calendar
     * month, across all paid leave types - the number the monthly paid-leave
     * allowance is spent on. See the block comment above for how attribution
     * and legacy rows are handled.
     */
    default Integer sumPaidLeaveDaysInMonth(
            Employee employee,
            LocalDate monthStart,
            LocalDate monthEnd) {

        Integer allocated = sumAllocatedPaidLeaveDaysInMonth(employee, monthStart, monthEnd);
        Integer legacy = sumLegacyPaidLeaveDaysInMonth(employee, monthStart, monthEnd);

        return (allocated == null ? 0 : allocated)
                + (legacy == null ? 0 : legacy);
    }

    /**
     * Per-employee PAID-day attribution for the month-end report.
     */
    interface PaidDaysProjection {
        Long getEmployeeId();

        Integer getPaidDays();
    }

    @Query("""
            SELECT a.leaveRequest.employee.id AS employeeId,
                   COALESCE(SUM(a.paidDays), 0) AS paidDays
            FROM LeaveRequestMonthAllocation a
            WHERE a.leaveRequest.status = 'APPROVED'
              AND a.allocationMonth >= :monthStart
              AND a.allocationMonth <= :monthEnd
            GROUP BY a.leaveRequest.employee.id
            """)
    List<PaidDaysProjection> sumAllocatedPaidLeaveDaysInMonthGrouped(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    @Query("""
            SELECT lr.employee.id AS employeeId,
                   COALESCE(SUM(COALESCE(lr.paidDays, lr.totalDays)), 0) AS paidDays
            FROM LeaveRequest lr
            WHERE lr.status = 'APPROVED'
              AND lr.leaveType.paid = true
              AND lr.startDate >= :monthStart
              AND lr.startDate <= :monthEnd
              AND NOT EXISTS (
                  SELECT 1 FROM LeaveRequestMonthAllocation a
                  WHERE a.leaveRequest = lr
              )
            GROUP BY lr.employee.id
            """)
    List<PaidDaysProjection> sumLegacyPaidLeaveDaysInMonthGrouped(
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd);

    /**
     * PAID days per employee for a calendar month, merged across allocated
     * and legacy attribution (see the block comment above).
     */
    default List<PaidDaysProjection> sumPaidLeaveDaysInMonthGrouped(
            LocalDate monthStart,
            LocalDate monthEnd) {

        Map<Long, Integer> merged = new LinkedHashMap<>();

        for (PaidDaysProjection row : sumAllocatedPaidLeaveDaysInMonthGrouped(monthStart, monthEnd)) {
            if (row.getEmployeeId() != null && row.getPaidDays() != null) {
                merged.merge(row.getEmployeeId(), row.getPaidDays(), Integer::sum);
            }
        }

        for (PaidDaysProjection row : sumLegacyPaidLeaveDaysInMonthGrouped(monthStart, monthEnd)) {
            if (row.getEmployeeId() != null && row.getPaidDays() != null) {
                merged.merge(row.getEmployeeId(), row.getPaidDays(), Integer::sum);
            }
        }

        return merged.entrySet().stream()
                .map(entry -> (PaidDaysProjection) new MappedPaidDaysProjection(
                        entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Detached {@link PaidDaysProjection} used to merge the two attribution
     * sources without a database union.
     */
    record MappedPaidDaysProjection(Long employeeId, Integer paidDays)
            implements PaidDaysProjection {

        @Override
        public Long getEmployeeId() {
            return employeeId;
        }

        @Override
        public Integer getPaidDays() {
            return paidDays;
        }
    }

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


