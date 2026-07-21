package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> applyLeave(
            @Valid @RequestBody LeaveRequestRequest request) {

        LeaveRequestResponse response =
                leaveRequestService.applyLeave(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<LeaveRequestResponse>builder()
                                .success(true)
                                .message("Leave applied successfully.")
                                .data(response)
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @PutMapping("/{leaveRequestId}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> cancelLeave(
            @PathVariable Long leaveRequestId) {

        LeaveRequestResponse response =
                leaveRequestService.cancelLeave(leaveRequestId);

        return ResponseEntity.ok(
                ApiResponse.<LeaveRequestResponse>builder()
                        .success(true)
                        .message("Leave cancelled successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

//    @PutMapping("/{leaveRequestId}/leave-approval-by-manager")
//    @PreAuthorize("hasAnyRole('MANAGER', 'SUPER_ADMIN', 'HR_ADMIN')")
//    public ResponseEntity<ApiResponse<LeaveRequestResponse>> managerAction(
//            @PathVariable Long leaveRequestId,
//            @Valid @RequestBody LeaveActionRequest request) {
//
//        LeaveRequestResponse response =
//                leaveRequestService.managerAction(
//                        leaveRequestId,
//                        request);
//
//        return ResponseEntity.ok(
//                ApiResponse.<LeaveRequestResponse>builder()
//                        .success(true)
//                        .message("Manager action completed successfully.")
//                        .data(response)
//                        .timestamp(LocalDateTime.now())
//                        .build()
//        );
//    }

//    @PutMapping("/{leaveRequestId}/hr-action")
//    @PreAuthorize("hasAnyRole('HR_ADMIN', 'SUPER_ADMIN', 'MANAGER')")
//    public ResponseEntity<ApiResponse<LeaveRequestResponse>> hrAction(
//            @PathVariable Long leaveRequestId,
//            @Valid @RequestBody LeaveActionRequest request) {
//
//        LeaveRequestResponse response =
//                leaveRequestService.hrAction(
//                        leaveRequestId,
//                        request);
//
//        return ResponseEntity.ok(
//                ApiResponse.<LeaveRequestResponse>builder()
//                        .success(true)
//                        .message("HR action completed successfully.")
//                        .data(response)
//                        .timestamp(LocalDateTime.now())
//                        .build()
//        );
//    }

    @GetMapping("/{leaveRequestId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestResponse>> getLeaveRequest(
            @PathVariable Long leaveRequestId) {

        LeaveRequestResponse response =
                leaveRequestService.getLeaveRequest(leaveRequestId);

        return ResponseEntity.ok(
                ApiResponse.<LeaveRequestResponse>builder()
                        .success(true)
                        .message("Leave request fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponse>>> getMyLeaveRequests() {

        List<LeaveRequestResponse> response =
                leaveRequestService.getMyLeaveRequests();

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveRequestResponse>>builder()
                        .success(true)
                        .message("Leave requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponse>>> getTeamLeaveRequests() {

        List<LeaveRequestResponse> response =
                leaveRequestService.getTeamLeaveRequests();

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveRequestResponse>>builder()
                        .success(true)
                        .message("Team leave requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'SUPER_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveRequestResponse>>> getAllLeaveRequests() {

        List<LeaveRequestResponse> response =
                leaveRequestService.getAllLeaveRequests();

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveRequestResponse>>builder()
                        .success(true)
                        .message("All leave requests fetched successfully.")
                        .data(response)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}