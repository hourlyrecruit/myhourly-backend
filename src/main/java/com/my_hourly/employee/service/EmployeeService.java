package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.CreateEmployeeRequest;
import com.my_hourly.employee.dto.request.UpdateEmployeeRequest;
import com.my_hourly.employee.dto.request.UpdateMyProfileRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;

public interface EmployeeService {

    // =====================================================
    // HR / Manager - Create Employee
    // =====================================================

    EmployeeResponse createEmployee(
            CreateEmployeeRequest request);

    // =====================================================
    // HR / Manager - Update Employee
    // =====================================================

    EmployeeResponse updateEmployee(
            String employeeCode,
            UpdateEmployeeRequest request);

    // =====================================================
    // HR / Manager - Delete Employee
    // =====================================================

    void deleteEmployee(
            String employeeCode);

    // =====================================================
    // HR / Manager - Get Employee By Employee Code
    // =====================================================

    EmployeeResponse getEmployeeByEmployeeCode(
            String employeeCode);

    // =====================================================
    // HR / Manager - Get All Employees
    // =====================================================

    List<EmployeeResponse> getAllEmployees();

    // =====================================================
    // Employee - View Own Profile
    // =====================================================

    EmployeeResponse getMyProfile(
            String employeeCode);

    // =====================================================
    // Employee - Update Own Profile
    // =====================================================

    EmployeeResponse updateMyProfile(
            String employeeCode,
            UpdateMyProfileRequest request);

}