package com.my_hourly.authentication.api.controller;

import com.my_hourly.authentication.api.request.AdminRegisterRequest;
import com.my_hourly.authentication.api.request.GrantRoleRequest;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.service.AdminService;
import com.my_hourly.common.enums.RoleName;
import com.my_hourly.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    /**
     * Register a new user with a specific role (any RoleName).
     * Only SUPER_ADMIN can access this endpoint.
     */
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

    /**
     * Grant a role to an existing user.
     * Only SUPER_ADMIN can access this endpoint.
     */
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

    /**
     * Revoke a role from an existing user.
     * Only SUPER_ADMIN can access this endpoint.
     */
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

    /**
     * List all users with their roles and permissions.
     * Only SUPER_ADMIN can access this endpoint.
     */
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

    /**
     * Get a single user by their ID.
     * Only SUPER_ADMIN can access this endpoint.
     */
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

}
