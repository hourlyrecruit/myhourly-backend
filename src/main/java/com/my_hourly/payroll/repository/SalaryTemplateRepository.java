package com.my_hourly.payroll.repository;

import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.payroll.entity.SalaryTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryTemplateRepository
        extends JpaRepository<SalaryTemplate, Long> {

    /**
     * Find active salary template by employee type.
     */
    Optional<SalaryTemplate> findByEmployeeTypeAndActiveTrue(
            EmploymentType employeeType);

    /**
     * Find salary template by employee type.
     */
    Optional<SalaryTemplate> findByEmployeeType(
            EmploymentType employeeType);

    /**
     * Check whether template exists for employee type.
     */
    boolean existsByEmployeeType(
            EmploymentType employeeType);

    /**
     * Check duplicate except current template.
     */
    boolean existsByEmployeeTypeAndIdNot(
            EmploymentType employeeType,
            Long id);

    /**
     * Get all active templates.
     */
    List<SalaryTemplate> findByActiveTrue();

}