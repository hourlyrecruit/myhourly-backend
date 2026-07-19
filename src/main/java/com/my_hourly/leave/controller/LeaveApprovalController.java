package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.service.LeaveApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-approvals")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    @GetMapping("/leave-request/{leaveRequestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LeaveApprovalResponse>>> getApprovalHistory(
            @PathVariable Long leaveRequestId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveApprovalResponse>>builder()
                        .success(true)
                        .message("Approval history fetched successfully.")
                        .data(leaveApprovalService.getApprovalHistory(leaveRequestId))
                        .build()
        );
    }

}