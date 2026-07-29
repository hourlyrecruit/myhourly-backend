package com.my_hourly.payroll.repository;

import com.my_hourly.payroll.entity.PayrollHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollHistoryRepository
        extends JpaRepository<PayrollHistory, Long> {

    List<PayrollHistory> findByPayrollIdOrderByCreatedAtDesc(Long payrollId);

}
