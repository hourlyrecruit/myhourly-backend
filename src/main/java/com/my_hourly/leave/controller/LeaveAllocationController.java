package com.my_hourly.leave.controller;

import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.leave.service.LeaveAllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="06-Leave Allocation Controller", description = "Optional: Leave will be automatically allocate to newly add employee and reset every year and allocation based on leave policy, HR can allocation total leave in year(leave policy). Guideline for month leave like 2 leave/month")
public class LeaveAllocationController {

    private final LeaveAllocationService leaveAllocationService;

//    @PostMapping("/employee/{employeeId}")
//    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
//    @Operation(summary = "HR can allocation the leave to individual employee like if emp joined a in mid of year. Access: HR_ADMIN, MANAGER")
//    public ResponseEntity<ApiResponse<Void>> allocateForEmployee(
//            @PathVariable Long employeeId) {
//
//        leaveAllocationService.allocateForEmployee(employeeId);
//
//        return ResponseEntity.ok(
//                ApiResponse.<Void>builder()
//                        .success(true)
//                        .message("Leave allocated successfully.")
//                        .build()
//        );
//    }

    @PostMapping("/all-employee")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Allocation same leave policy to all employee at once. Access: HR_ADMIN, MANAGER, SUPER_ADMIN")
    public ResponseEntity<ApiResponse<Void>> allocateForAllEmployee() {

        leaveAllocationService.allocateForAllEmployees();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave allocated successfully to all Employee.")
                        .build()
        );
    }

    @PostMapping("/leave-type/{leaveTypeId}")
    @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Sync leave balances for all employees after a leave type's allocated days changed. Access: HR_ADMIN, SUPER_ADMIN, MANAGER")
    public ResponseEntity<ApiResponse<Void>> reallocateForLeaveType(
            @PathVariable Long leaveTypeId) {

        leaveAllocationService.reallocateForLeaveType(leaveTypeId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Leave balances synced for all employees.")
                        .build()
        );
    }

}