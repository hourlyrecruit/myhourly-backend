package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.MonthType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeAndLeaveTypeAndYearAndMonth(
            Employee employee,
            LeaveType leaveType,
            Integer year,
            MonthType month);

    boolean existsByEmployeeAndLeaveTypeAndYearAndMonth(
            Employee employee,
            LeaveType leaveType,
            Integer year,
            MonthType month);

    List<LeaveBalance> findByEmployee(Employee employee);

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeAndYear(
            Employee employee,
            Integer year);

    List<LeaveBalance> findByYear(Integer year);
    boolean existsByEmployee(Employee employee);


}