package com.my_hourly.payroll.service;

import com.my_hourly.payroll.dto.request.CreateSalaryStructureRequest;
import com.my_hourly.payroll.dto.response.SalaryStructureResponse;

import java.util.List;

public interface SalaryStructureService {

    /**
     * Create initial salary structure for an employee.
     */
    SalaryStructureResponse create(CreateSalaryStructureRequest request);

    /**
     * Create salary revision.
     */
    SalaryStructureResponse createRevision(CreateSalaryStructureRequest request);

    /**
     * Get salary structure by id.
     */
    SalaryStructureResponse getById(Long id);

    /**
     * Get current active salary structure of employee.
     */
    SalaryStructureResponse getActiveByEmployee(Long employeeId);

    /**
     * Get salary history of employee.
     */
    List<SalaryStructureResponse> getHistory(Long employeeId);

    /**
     * Get all active salary structures.
     */
    List<SalaryStructureResponse> getAllActive();

}
