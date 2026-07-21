package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.service.LeaveApprovalService;
import com.my_hourly.leave.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-approvals")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;
    private final LeaveRequestService leaveRequestService;


    @PutMapping("/{leaveRequestId}/leave-approval-by-manager")
    @PreAuthorize("hasAnyRole('MANAGER', 'SUPER_ADMIN', 'HR_ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> managerAction(
            @PathVariable Long leaveRequestId,
            @Valid @RequestBody LeaveActionRequest request) {

        LeaveRequestResponse response =
                leaveRequestService.managerAction(
                        leaveRequestId,
                        request);

        return ResponseEntity.ok(
                ApiResponse.<LeaveRequestResponse>builder()
                        .success(true)
                        .message("Manager action completed successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


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