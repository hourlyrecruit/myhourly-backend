package com.my_hourly.payroll.repository;

import com.my_hourly.payroll.entity.SalaryStructure;
import com.my_hourly.payroll.enums.SalaryStructureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryStructureRepository
        extends JpaRepository<SalaryStructure, Long> {

    /**
     * Get active salary structure of an employee.
     */
    Optional<SalaryStructure> findByEmployeeIdAndStatus(
            Long employeeId,
            SalaryStructureStatus status);

    /**
     * Get salary history of an employee.
     */
    List<SalaryStructure> findByEmployeeIdOrderByEffectiveFromDesc(
            Long employeeId);

    /**
     * Check if employee already has an active salary structure.
     */
    boolean existsByEmployeeIdAndStatus(
            Long employeeId,
            SalaryStructureStatus status);

    /**
     * Find salary structure effective on a given date.
     */
    @Query("""
    SELECT s
    FROM SalaryStructure s
    WHERE s.employee.id = :employeeId
      AND s.effectiveFrom <= :date
      AND (s.effectiveTo IS NULL OR s.effectiveTo >= :date)
""")
    Optional<SalaryStructure> findEffectiveSalaryStructure(
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date);

    /**
     * Find open-ended active salary structure.
     */
    Optional<SalaryStructure> findByEmployeeIdAndEffectiveToIsNull(
            Long employeeId);

    /**
     * Get all active salary structures.
     */
    List<SalaryStructure> findByStatus(
            SalaryStructureStatus status);

}
