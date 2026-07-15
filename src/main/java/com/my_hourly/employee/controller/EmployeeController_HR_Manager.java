package com.my_hourly.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.CreateEmployeeRequest;
import com.my_hourly.employee.dto.request.UpdateEmployeeRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController_HR_Manager {

    @Autowired
    private EmployeeService employeeService;

    // =====================================================
    // HR / Manager - Create Employee
    // =====================================================

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody CreateEmployeeRequest request) {

        EmployeeResponse response =
                employeeService.createEmployee(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // =====================================================
    // HR / Manager - Update Employee
    // =====================================================

    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable String employeeCode,
            @RequestBody UpdateEmployeeRequest request) {

        EmployeeResponse response =
                employeeService.updateEmployee(employeeCode, request);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // HR / Manager - Delete Employee
    // =====================================================

    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable String employeeCode) {

        employeeService.deleteEmployee(employeeCode);

        return ResponseEntity.ok("Employee Deleted Successfully.");
    }

    // =====================================================
    // HR / Manager - Get Employee By Employee Code
    // =====================================================

    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeCode(
            @PathVariable String employeeCode) {

        EmployeeResponse response =
                employeeService.getEmployeeByEmployeeCode(employeeCode);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // HR / Manager - Get All Employees
    // =====================================================

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        List<EmployeeResponse> response =
                employeeService.getAllEmployees();

        return ResponseEntity.ok(response);
    }

}