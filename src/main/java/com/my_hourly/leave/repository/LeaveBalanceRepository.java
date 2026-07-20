package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * Check whether an annual leave balance record already exists.
     */
    boolean existsByEmployeeAndLeaveTypeAndYear(
            Employee employee,
            LeaveType leaveType,
            Integer year);

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