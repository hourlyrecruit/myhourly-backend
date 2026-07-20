package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.service.LeaveAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave-allocation")
@RequiredArgsConstructor
public class LeaveAllocationController {

    private final LeaveAllocationService leaveAllocationService;

    @PostMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> allocateForEmployee(
            @PathVariable Long employeeId) {

        leaveAllocationService.allocateForEmployee(employeeId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave allocated successfully.")
                        .build()
        );
    }

}