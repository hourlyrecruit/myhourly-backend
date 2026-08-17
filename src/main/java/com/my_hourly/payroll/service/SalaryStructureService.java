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
     * Get salary structures of employee.
     */
    List<SalaryStructureResponse> getByEmployee(Long employeeId, Boolean activeOnly);

    /**
     * Get all salary structures.
     */
    List<SalaryStructureResponse> getAll(Boolean activeOnly);

}
