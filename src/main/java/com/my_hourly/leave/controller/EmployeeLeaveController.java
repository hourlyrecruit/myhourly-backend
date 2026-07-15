package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.leave.dto.request.LeaveRequest;
import com.my_hourly.leave.dto.response.LeaveBalanceResponse;
import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/employee/leaves")
public class EmployeeLeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    // =====================================================
    // Employee - Apply Leave
    // =====================================================

    @PostMapping("/apply")
    public LeaveResponse applyLeave(
            @RequestBody LeaveRequest request) {

        return leaveService.applyLeave(request);

    }

    // =====================================================
    // Employee - View Leave History
    // =====================================================

    @GetMapping("/history/{employeeCode}")
    public List<LeaveResponse> getLeaveHistory(
            @PathVariable String employeeCode) {

        return leaveService.getEmployeeLeaves(employeeCode);

    }

    // =====================================================
    // Employee - View Leave Status
    // =====================================================

    @GetMapping("/status/{employeeCode}")
    public List<LeaveResponse> getLeaveStatus(
            @PathVariable String employeeCode) {

        return leaveService.getLeaveStatus(employeeCode);

    }

    // =====================================================
    // Employee - View Leave Balance
    // =====================================================

    @GetMapping("/balance/{employeeCode}")
    public LeaveBalanceResponse getLeaveBalance(
            @PathVariable String employeeCode) {

        return leaveBalanceService.getLeaveBalance(employeeCode);

    }

}