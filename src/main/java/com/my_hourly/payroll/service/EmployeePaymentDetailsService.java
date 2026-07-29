package com.my_hourly.payroll.service;

import com.my_hourly.payroll.dto.request.CreateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.request.UpdateEmployeePaymentDetailsRequest;
import com.my_hourly.payroll.dto.response.EmployeePaymentDetailsResponse;

public interface EmployeePaymentDetailsService {

    /**
     * Create employee payment details.
     *
     * @param request create request
     * @return created payment details
     */
    EmployeePaymentDetailsResponse create(
            CreateEmployeePaymentDetailsRequest request);

    /**
     * Update employee payment details.
     *
     * @param employeeId employee id
     * @param request update request
     * @return updated payment details
     */
    EmployeePaymentDetailsResponse update(
            Long employeeId,
            UpdateEmployeePaymentDetailsRequest request);

    /**
     * Get payment details by employee id.
     *
     * @param employeeId employee id
     * @return payment details
     */
    EmployeePaymentDetailsResponse getByEmployeeId(
            Long employeeId);

    /**
     * Delete payment details.
     *
     * @param employeeId employee id
     */
    void delete(Long employeeId);

}