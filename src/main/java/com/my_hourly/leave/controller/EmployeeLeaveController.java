package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/employee/leaves")
@CrossOrigin(origins = "*")
public class EmployeeLeaveController {

    @Autowired
    private LeaveService leaveService;

    // =====================================================
    // Employee - Apply Leave
    // =====================================================

    @PostMapping
    public ResponseEntity<LeaveResponse> applyLeave(
            @RequestBody LeaveRequest request) {

        LeaveResponse response = leaveService.applyLeave(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // =====================================================
    // Employee - View Own Leave History
    // =====================================================

    @GetMapping("/{employeeCode}")
    public ResponseEntity<List<LeaveResponse>> getEmployeeLeaves(
            @PathVariable String employeeCode) {

        List<LeaveResponse> response =
                leaveService.getEmployeeLeaves(employeeCode);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Employee - View Leave Status
    // =====================================================

    @GetMapping("/status/{employeeCode}")
    public ResponseEntity<List<LeaveResponse>> getLeaveStatus(
            @PathVariable String employeeCode) {

        List<LeaveResponse> response =
                leaveService.getLeaveStatus(employeeCode);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Employee - View Leave Balance
    // =====================================================

    @GetMapping("/balance/{employeeCode}")
    public ResponseEntity<Integer> getLeaveBalance(
            @PathVariable String employeeCode) {

        Integer balance =
                leaveService.getLeaveBalance(employeeCode);

        return ResponseEntity.ok(balance);
    }

}