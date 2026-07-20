package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.response.LeaveTransactionResponse;
import com.my_hourly.leave.service.LeaveTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-transactions")
@RequiredArgsConstructor
public class LeaveTransactionController {

    private final LeaveTransactionService leaveTransactionService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveTransactionResponse>>> getMyTransactions() {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveTransactionResponse>>builder()
                        .success(true)
                        .message("Leave transactions fetched successfully.")
                        .data(leaveTransactionService.getMyTransactions())
                        .build());
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveTransactionResponse>>> getEmployeeTransactions(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveTransactionResponse>>builder()
                        .success(true)
                        .message("Employee leave transactions fetched successfully.")
                        .data(leaveTransactionService.getEmployeeTransactions(employeeId))
                        .build());
    }

    @GetMapping("/leave-request/{leaveRequestId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveTransactionResponse>>> getLeaveTransactions(
            @PathVariable Long leaveRequestId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveTransactionResponse>>builder()
                        .success(true)
                        .message("Leave transactions fetched successfully.")
                        .data(leaveTransactionService.getLeaveTransactions(leaveRequestId))
                        .build());
    }

}