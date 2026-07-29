package com.my_hourly.payroll.repository;

import com.my_hourly.payroll.entity.EmployeePaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeePaymentDetailsRepository
        extends JpaRepository<EmployeePaymentDetails, Long> {

    /**
     * Find payment details by employee id.
     */
    Optional<EmployeePaymentDetails> findByEmployeeId(Long employeeId);

    /**
     * Find payment details by employee code.
     */
    Optional<EmployeePaymentDetails> findByEmployeeEmployeeCode(String employeeCode);

    /**
     * Check whether payment details already exist for an employee.
     */
    boolean existsByEmployeeId(Long employeeId);

    /**
     * Delete payment details by employee id.
     */
    void deleteByEmployeeId(Long employeeId);

}
