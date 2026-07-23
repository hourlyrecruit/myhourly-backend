package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.api.response.LeaveApprovalResponse;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.service.LeaveApprovalService;
import com.my_hourly.leave.service.LeaveRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="08-Leave Approval Controller", description = "Manger can approve the requested leave")
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;
    private final LeaveRequestService leaveRequestService;


    @Operation(summary = "Manager Leave Action. Access: MANAGER")
    @PutMapping("/{leaveRequestId}/leave-approval-by-manager")
    @PreAuthorize("hasRole('MANAGER')")
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


    @Operation(summary = "Get Approval History. Access: Authenticated Users")
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