package com.my_hourly.payroll.service;

import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.payroll.dto.request.CreateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.request.UpdateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.request.UpdateSalaryTemplateStatusRequest;
import com.my_hourly.payroll.dto.response.SalaryTemplateResponse;

import java.util.List;

public interface SalaryTemplateService {

    /**
     * Create Salary Template.
     */
    SalaryTemplateResponse create(CreateSalaryTemplateRequest request);

    /**
     * Update Salary Template.
     */
    SalaryTemplateResponse update(
            Long id,
            UpdateSalaryTemplateRequest request);

    /**
     * Get Salary Template by Id.
     */
    SalaryTemplateResponse getById(Long id);

    /**
     * Get Salary Template by Employee Type.
     */
    SalaryTemplateResponse getByEmployeeType(
            EmploymentType employeeType);

    /**
     * Get all Salary Templates.
     */
    List<SalaryTemplateResponse> getAll(Boolean activeOnly);

    /**
     * Update Salary Template status.
     */
    SalaryTemplateResponse updateStatus(Long id, UpdateSalaryTemplateStatusRequest request);

}
