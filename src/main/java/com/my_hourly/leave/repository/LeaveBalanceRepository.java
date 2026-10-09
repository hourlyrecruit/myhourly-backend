package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    /**
     * Find the annual leave balance for an employee in a specific year.
     */
    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYear(
            Employee employee,
            LeaveType leaveType,
            Integer year);

    /**
     * Same lookup as {@link #findByEmployeeAndLeaveTypeAndYear} but under a
     * {@code SELECT ... FOR UPDATE} row lock.
     *
     * <p>Leave approval takes this lock before it reads the balance and the
     * month's already-approved PAID days, so two approvals for the same
     * employee / leave type / year cannot both see the same remaining balance
     * (or the same leftover monthly allowance) and over-allocate. The lock is
     * also what keeps the annual balance from going negative.</p>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lb from LeaveBalance lb " +
            "where lb.employee = :employee and lb.leaveType = :leaveType and lb.year = :year")
    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYearForUpdate(
            @Param("employee") Employee employee,
            @Param("leaveType") LeaveType leaveType,
            @Param("year") Integer year);

    /**
     * Check whether an annual leave balance record already exists.
     */
    boolean existsByEmployeeAndLeaveTypeAndYear(
            Employee employee,
            LeaveType leaveType,
            Integer year);

    /**
     * Bulk-deletes every leave balance held against the given leave type.
     * Used only when cascading the deletion of a leave type.
     */
    @Modifying
    @Query("delete from LeaveBalance lb where lb.leaveType.id = :leaveTypeId")
    int deleteByLeaveTypeId(@Param("leaveTypeId") Long leaveTypeId);

    List<LeaveBalance> findByEmployee(Employee employee);

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeAndYear(
            Employee employee,
            Integer year);

    List<LeaveBalance> findByYear(Integer year);

    boolean existsByEmployee(Employee employee);

    /**
     * Used by month-end scheduler to find balances that could still have expiry applied.
     */
    List<LeaveBalance> findByRemainingLeavesGreaterThan(Integer remainingLeaves);
}