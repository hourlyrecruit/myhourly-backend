package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getMyLeaveBalances() {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveBalanceResponse>>builder()
                        .success(true)
                        .message("Leave balances fetched successfully.")
                        .data(leaveBalanceService.getMyLeaveBalances())
                        .build()
        );
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getEmployeeLeaveBalances(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveBalanceResponse>>builder()
                        .success(true)
                        .message("Employee leave balances fetched successfully.")
                        .data(leaveBalanceService.getEmployeeLeaveBalances(employeeId))
                        .build()
        );
    }

    @GetMapping("/{leaveBalanceId}")
    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> getLeaveBalance(
            @PathVariable Long leaveBalanceId) {

        return ResponseEntity.ok(
                ApiResponse.<LeaveBalanceResponse>builder()
                        .success(true)
                        .message("Leave balance fetched successfully.")
                        .data(leaveBalanceService.getLeaveBalance(leaveBalanceId))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getAllLeaveBalances() {

        return ResponseEntity.ok(
                ApiResponse.<List<LeaveBalanceResponse>>builder()
                        .success(true)
                        .message("Leave balances fetched successfully.")
                        .data(leaveBalanceService.getAllLeaveBalances())
                        .build()
        );
    }

}
