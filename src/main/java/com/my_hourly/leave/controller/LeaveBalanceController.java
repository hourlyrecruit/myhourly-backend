package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.api.response.LeaveBalanceResponse;
import com.my_hourly.leave.service.LeaveBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leave-balances")
@RequiredArgsConstructor
@Tag(name="10-Leave Balance", description = "Details of remaining leave of employee")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @Operation(summary = "Get My Leave Balances. Access: EMPLOYEE")
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

    @Operation(summary = "Get Employee Leave Balances. Access: HR_ADMIN, SUPER_ADMIN, MANAGER")
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

    @Operation(summary = "Get Leave Balance by ID. Access: HR_ADMIN, SUPER_ADMIN, MANAGER")
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

    @Operation(summary = "Get All Leave Balances. Access: HR_ADMIN, SUPER_ADMIN, MANAGER")
    @GetMapping
    @PreAuthorize("hasAnyRole('HR_ADMIN','SUPER_ADMIN', 'MANAGER')")
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
