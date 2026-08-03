package com.my_hourly.payroll.repository;

import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.enums.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    /**
     * Find payroll by payroll number.
     */
    Optional<Payroll> findByPayrollNumber(String payrollNumber);

    /**
     * Find payroll by employee and payroll month ordered by version.
     */
    List<Payroll> findByEmployeeIdAndPayrollMonthOrderByVersionDesc(
            Long employeeId,
            LocalDate payrollMonth);

    /**
     * Find latest payroll version for employee in a month.
     */
    Optional<Payroll> findFirstByEmployeeIdAndPayrollMonthOrderByVersionDesc(
            Long employeeId,
            LocalDate payrollMonth);

    /**
     * Check if an active payroll exists for employee in month (excludes SUPERSEDED & CANCELLED).
     */
    boolean existsByEmployeeIdAndPayrollMonthAndStatusNotIn(
            Long employeeId,
            LocalDate payrollMonth,
            List<PayrollStatus> excludedStatuses);

    /**
     * Check payroll exists for employee and month excluding a specific status.
     */
    boolean existsByEmployeeIdAndPayrollMonthAndStatusNot(
            Long employeeId,
            LocalDate payrollMonth,
            PayrollStatus status);

    /**
     * Find payrolls of a given month.
     */
    List<Payroll> findByPayrollMonth(LocalDate payrollMonth);

    /**
     * Find payrolls by status.
     */
    List<Payroll> findByStatus(PayrollStatus status);

    /**
     * Find all active payrolls (not superseded / cancelled).
     */
    List<Payroll> findByActiveTrue();

    /**
     * Find payroll history of employee (all versions, all months).
     */
    List<Payroll> findByEmployeeIdOrderByPayrollMonthDescVersionDesc(Long employeeId);

    /**
     * Find payroll by status and month.
     */
    List<Payroll> findByPayrollMonthAndStatus(
            LocalDate payrollMonth,
            PayrollStatus status);

    /**
     * Find latest payroll by month (used for sequence generation).
     */
    Optional<Payroll> findFirstByPayrollMonthOrderByIdDesc(LocalDate payrollMonth);

    /**
     * Check if active payroll exists for employee in month.
     */
    boolean existsByEmployeeIdAndPayrollMonthAndActiveTrue(
            Long employeeId,
            LocalDate payrollMonth);

    /**
     * Get the active payroll for employee in month.
     */
    Optional<Payroll> findFirstByEmployeeIdAndPayrollMonthBetweenAndActiveTrue(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate);
}
