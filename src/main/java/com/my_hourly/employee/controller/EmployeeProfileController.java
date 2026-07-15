package com.my_hourly.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.UpdateMyProfileRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeService;

@RestController
@RequestMapping("/api/employees/profile")
@CrossOrigin(origins = "*")
public class EmployeeProfileController {

    @Autowired
    private EmployeeService employeeService;

    // =====================================================
    // Employee - View Own Profile
    // =====================================================

    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponse> getMyProfile(
            @PathVariable String employeeCode) {

        EmployeeResponse response =
                employeeService.getMyProfile(employeeCode);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Employee - Update Own Profile
    // =====================================================

    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeResponse> updateMyProfile(
            @PathVariable String employeeCode,
            @RequestBody UpdateMyProfileRequest request) {

        EmployeeResponse response =
                employeeService.updateMyProfile(employeeCode, request);

        return ResponseEntity.ok(response);
    }

}