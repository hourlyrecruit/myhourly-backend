package com.my_hourly.leave.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.leave.dto.response.LeaveResponse;
import com.my_hourly.leave.service.LeaveService;

@RestController
@RequestMapping("/api/manager/leaves")
@CrossOrigin(origins = "*")
public class ManagerLeaveController {

    @Autowired
    private LeaveService leaveService;

    // =====================================================
    // Manager - View All Employee Leaves
    // =====================================================

    @GetMapping
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {

        List<LeaveResponse> response = leaveService.getAllLeaves();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Manager - View Pending Leave Requests
    // =====================================================

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveResponse>> getPendingLeaves() {

        List<LeaveResponse> response = leaveService.getPendingLeaves();

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Manager - Approve Leave
    // =====================================================

    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long leaveId,
            @RequestParam String managerRemarks) {

        LeaveResponse response =
                leaveService.approveLeave(leaveId, managerRemarks);

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // Manager - Reject Leave
    // =====================================================

    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long leaveId,
            @RequestParam String managerRemarks) {

        LeaveResponse response =
                leaveService.rejectLeave(leaveId, managerRemarks);

        return ResponseEntity.ok(response);
    }

}