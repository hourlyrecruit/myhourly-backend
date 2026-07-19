package com.my_hourly.leave.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveTransactionRepository
        extends JpaRepository<LeaveTransaction, Long> {

    List<LeaveTransaction> findByEmployee(Employee employee);

    List<LeaveTransaction> findByEmployeeId(Long employeeId);

    List<LeaveTransaction> findByLeaveRequest(
            LeaveRequest leaveRequest);

}
