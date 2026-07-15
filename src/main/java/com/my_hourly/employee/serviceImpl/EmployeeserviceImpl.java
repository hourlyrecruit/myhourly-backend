package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.CreateEmployeeRequest;
import com.my_hourly.employee.dto.request.UpdateEmployeeRequest;
import com.my_hourly.employee.dto.request.UpdateMyProfileRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.exception.EmployeeAlreadyExistsException;
import com.my_hourly.employee.exception.EmployeeNotFoundException;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;

@Service
public class EmployeeserviceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // =====================================================
    // HR / Manager - Create Employee
    // =====================================================

    @Override
    public EmployeeResponse createEmployee(
            CreateEmployeeRequest request) {

        // Check Office Email
        if (employeeRepository.existsByOfficeEmail(
                request.getOfficeEmail())) {

            throw new EmployeeAlreadyExistsException(
                    "Office Email Already Exists.");
        }

        Employee employee = new Employee();

        // Generate Employee Code
        employee.setEmployeeCode(generateEmployeeCode());

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setOfficeEmail(request.getOfficeEmail());

        employee.setDepartmentName(request.getDepartmentName());
        employee.setDesignationName(request.getDesignationName());
        employee.setEmploymentType(request.getEmploymentType());

        // Reporting Manager
        if (request.getReportingManagerCode() != null &&
                !request.getReportingManagerCode().isBlank()) {

            Employee reportingManager =
                    employeeRepository
                            .findByEmployeeCode(
                                    request.getReportingManagerCode())
                            .orElseThrow(() ->
                                    new EmployeeNotFoundException(
                                            "Reporting Manager Not Found"));

            employee.setReportingManager(reportingManager);
        }

        // Dummy Password
        employee.setPassword(request.getPassword());

        // Default Active
        employee.setActive(true);

        Employee savedEmployee =
                employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    // =====================================================
    // Generate Employee Code
    // EMP0001
    // EMP0002
    // =====================================================

    private String generateEmployeeCode() {

        List<Employee> employees =
                employeeRepository.findAll();

        if (employees.isEmpty()) {
            return "EMP0001";
        }

        Employee lastEmployee = employees.stream()
                .max(Comparator.comparing(Employee::getId))
                .orElseThrow();

        String lastCode = lastEmployee.getEmployeeCode();

        int number =
                Integer.parseInt(lastCode.substring(3));

        return String.format("EMP%04d", number + 1);
    }
    
    
    // =====================================================
    // HR / Manager - Update Employee
    // =====================================================

    @Override
    public EmployeeResponse updateEmployee(
            String employeeCode,
            UpdateEmployeeRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee Not Found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setOfficeEmail(request.getOfficeEmail());

        employee.setDepartmentName(request.getDepartmentName());
        employee.setDesignationName(request.getDesignationName());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setDateOfJoining(request.getDateOfJoining());

        // Update Reporting Manager
        if (request.getReportingManagerCode() != null &&
                !request.getReportingManagerCode().isBlank()) {

            Employee reportingManager =
                    employeeRepository
                            .findByEmployeeCode(
                                    request.getReportingManagerCode())
                            .orElseThrow(() ->
                                    new EmployeeNotFoundException(
                                            "Reporting Manager Not Found"));

            employee.setReportingManager(reportingManager);
        } else {
            employee.setReportingManager(null);
        }

        employee.setActive(request.getActive());

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    // =====================================================
    // HR / Manager - Delete Employee
    // =====================================================

    @Override
    public void deleteEmployee(String employeeCode) {

        Employee employee = employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee Not Found"));

        employeeRepository.delete(employee);
    }

    // =====================================================
    // HR / Manager - Get Employee By Employee Code
    // =====================================================

    @Override
    public EmployeeResponse getEmployeeByEmployeeCode(
            String employeeCode) {

        Employee employee = employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee Not Found"));

        return mapToResponse(employee);
    }

    // =====================================================
    // HR / Manager - Get All Employees
    // =====================================================

    @Override
    public List<EmployeeResponse> getAllEmployees() {

        List<Employee> employeeList =
                employeeRepository.findAll();

        List<EmployeeResponse> responseList =
                new java.util.ArrayList<>();

        for (Employee employee : employeeList) {

            responseList.add(
                    mapToResponse(employee));

        }

        return responseList;
    }
    
    
    // =====================================================
    // Employee - View Own Profile
    // =====================================================

    @Override
    public EmployeeResponse getMyProfile(
            String employeeCode) {

        Employee employee = employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee Not Found"));

        return mapToResponse(employee);
    }

    // =====================================================
    // Employee - Update Own Profile
    // =====================================================

    @Override
    public EmployeeResponse updateMyProfile(
            String employeeCode,
            UpdateMyProfileRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() ->
                        new EmployeeNotFoundException(
                                "Employee Not Found"));

        // Personal Details
        employee.setPersonalEmail(request.getPersonalEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());

        // Profile Photo
        employee.setProfilePhoto(request.getProfilePhoto());

        Employee updatedEmployee =
                employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    // =====================================================
    // Entity -> Response
    // =====================================================

    private EmployeeResponse mapToResponse(
            Employee employee) {

        EmployeeResponse response =
                new EmployeeResponse();

        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());

        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());

        response.setOfficeEmail(employee.getOfficeEmail());
        response.setPersonalEmail(employee.getPersonalEmail());

        response.setPhoneNumber(employee.getPhoneNumber());

        response.setGender(employee.getGender());

        response.setDateOfBirth(employee.getDateOfBirth());
        response.setDateOfJoining(employee.getDateOfJoining());

        response.setDepartmentName(employee.getDepartmentName());
        response.setDesignationName(employee.getDesignationName());

        response.setEmploymentType(employee.getEmploymentType());

        if (employee.getReportingManager() != null) {

            response.setReportingManagerCode(
                    employee.getReportingManager().getEmployeeCode());

            response.setReportingManagerName(
                    employee.getReportingManager().getFirstName()
                            + " "
                            + employee.getReportingManager().getLastName());
        }

        response.setProfilePhoto(employee.getProfilePhoto());

        response.setActive(employee.getActive());

        return response;
    }

}





    
    
    