package com.my_hourly.leave.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.my_hourly.leave.entity.LeaveBalance;

@Repository
public interface LeaveBalanceRepository
        extends JpaRepository<LeaveBalance, Long> {

    // Find Latest Balance by Employee

    Optional<LeaveBalance> findByEmployeeCode(
            String employeeCode);

    // Find Balance by Employee + Month + Year

    Optional<LeaveBalance> findByEmployeeCodeAndMonthAndYear(
            String employeeCode,
            Integer month,
            Integer year);

}