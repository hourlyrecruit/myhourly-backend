package com.my_hourly.authentication.api.controller;

import com.my_hourly.authentication.api.request.AdminRegisterRequest;
import com.my_hourly.authentication.api.request.GrantRoleRequest;
import com.my_hourly.authentication.api.request.UpdateUserStatusRequest;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.service.AdminService;
import com.my_hourly.common.enums.RoleName;
import com.my_hourly.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER')")
@Tag(name = "Admin Controller", description = "Only SUPER_ADMIN and MANAGER can access these endpoint.")
public class AdminController {

    private final AdminService adminService;

    @Operation(description = "super_admin and manager can add any user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(
            @Valid @RequestBody AdminRegisterRequest request
    ) {

        RegisterResponse response = adminService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RegisterResponse>builder()
                        .success(true)
                        .message("User registered successfully with role: " + request.getRole())
                        .data(response)
                        .build()
        );

    }

    @Operation(description = "Change/Grant a role to an existing user.")
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<Void>> grantRole(
            @PathVariable Long userId,
            @Valid @RequestBody GrantRoleRequest request
    ) {

        adminService.grantRole(userId, request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Role '" + request.getRole() + "' granted successfully.")
                        .build()
        );

    }


    @Operation(description = "Change/Grant a role to an existing user.")
    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<ApiResponse<Void>> revokeRole(
            @PathVariable Long userId,
            @PathVariable RoleName roleName
    ) {

        adminService.revokeRole(userId, roleName);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Role '" + roleName + "' revoked successfully.")
                        .build()
        );

    }


    @Operation(description = "List all users with their roles and permissions.")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers() {

        List<UserProfileResponse> users = adminService.getAllUsers();

        return ResponseEntity.ok(
                ApiResponse.<List<UserProfileResponse>>builder()
                        .success(true)
                        .message("Users fetched successfully.")
                        .data(users)
                        .build()
        );

    }

    @Operation(description = "Get a single user by their ID.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable Long userId
    ) {

        UserProfileResponse user = adminService.getUserById(userId);

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("User fetched successfully.")
                        .data(user)
                        .build()
        );

    }

    @Operation(description = "Delete a user by ID.")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId
    ) {

        adminService.deleteUser(userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User deleted successfully.")
                        .build()
        );

    }


    @Operation(description = "Update the status of an existing user (ACTIVE, INACTIVE, LOCKED, DISABLED, PASSWORD_EXPIRED).")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {

        adminService.updateUserStatus(userId, request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("User status updated to '" + request.getStatus() + "' successfully.")
                        .build()
        );

    }

}
