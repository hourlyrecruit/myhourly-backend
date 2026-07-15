package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/hr/leaves")
@CrossOrigin(origins = "*")
public class HrLeaveController {

    @Autowired
    private LeaveService leaveService;

    // =====================================================
    // HR - View All Employee Leaves
    // =====================================================

    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {

        List<LeaveResponse> response =
                leaveService.getAllLeaves();

        return ResponseEntity.ok(response);
    }

}