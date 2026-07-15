package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/hr/leaves")
public class HrLeaveController {

    @Autowired
    private LeaveService leaveService;

    // =====================================================
    // HR - View All Leave Requests
    // =====================================================

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {

        return leaveService.getAllLeaves();

    }

    // =====================================================
    // HR - View Approved Leave Requests
    // =====================================================

    @GetMapping("/approved")
    public List<LeaveResponse> getApprovedLeaves() {

        return leaveService.getApprovedLeaves();

    }

    // =====================================================
    // HR - View Rejected Leave Requests
    // =====================================================

    @GetMapping("/rejected")
    public List<LeaveResponse> getRejectedLeaves() {

        return leaveService.getRejectedLeaves();

    }

}