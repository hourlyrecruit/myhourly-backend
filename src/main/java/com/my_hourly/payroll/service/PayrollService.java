package com.my_hourly.payroll.service;

import com.my_hourly.payroll.dto.request.CreatePayrollRequest;
import com.my_hourly.payroll.dto.request.UpdateDraftPayrollRequest;
import com.my_hourly.payroll.dto.request.UpdatePayrollStatusRequest;
import com.my_hourly.payroll.dto.response.PayrollResponse;
import com.my_hourly.payroll.dto.response.PayrollSummaryResponse;
import com.my_hourly.payroll.enums.PayrollStatus;

import java.time.LocalDate;
import java.util.List;

public interface PayrollService {

    /**
     * Generate payroll for one or more employees.
     */
    PayrollSummaryResponse generatePayroll(CreatePayrollRequest request);

    /**
     * Get payroll by ID.
     */
    PayrollResponse getById(Long payrollId);

    /**
     * Get payroll by payroll number.
     */
    PayrollResponse getByPayrollNumber(String payrollNumber);

    /**
     * Get payroll history of an employee.
     */
    List<PayrollResponse> getByEmployee(Long employeeId);

    /**
     * Get all payrolls for a given month.
     */
    List<PayrollResponse> getByPayrollMonth(LocalDate payrollMonth);

    /**
     * Get payrolls by status.
     */
    List<PayrollResponse> getByStatus(PayrollStatus status);

    /**
     * Update a DRAFT payroll before finalizing.
     */
    PayrollResponse update(Long payrollId, UpdateDraftPayrollRequest request);

    /**
     * Update the status of a payroll (Approve, Pay, Cancel).
     */
    PayrollResponse updateStatus(Long payrollId, UpdatePayrollStatusRequest request);

    /**
     * Regenerate a payroll (supersedes current, creates new version).
     */
    PayrollResponse regenerate(Long payrollId);

}