package com.my_hourly.authentication.api.controller;

import com.my_hourly.authentication.api.request.AdminRegisterRequest;
import com.my_hourly.authentication.api.request.GrantRoleRequest;
import com.my_hourly.authentication.api.request.UpdateUserStatusRequest;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.service.AdminService;
import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.common.payload.response.ApiResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'MANAGER', 'HR')")
@Tag(name = "Admin Controller", description = "Only SUPER_ADMIN and MANAGER can access these endpoint.")
public class AdminController {

    private final AdminService adminService;
    private final EmployeeService employeeService;


    @Operation(summary = "HR and manager can add any user", description = "super_admin and manager can add any user")
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

//=====================================================================================================

    @PostMapping(value = "/user/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //@PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Create employees profile")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
                            @Encoding(name = "file", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    }
            )
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> createUserProfileByAdmin(
            @PathVariable("userId") Long userId,
            @Valid @RequestPart("request") CreateEmployeeRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        EmployeeResponse response = employeeService.createUserProfileByAdmin(userId, request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee created successfully.")
                        .data(response)
                        .build());
    }



    @PutMapping("user/{userId}")
   // @PreAuthorize("hasAnyRole('HR_ADMIN', 'MANAGER')")
    @Operation(summary = "Update employee profile")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateUserProfileByAdmin(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        EmployeeResponse response = employeeService.updateUserProfileByAdmin(userId, request);

        return ResponseEntity.ok(
                ApiResponse.<EmployeeResponse>builder()
                        .success(true)
                        .message("Employee updated successfully.")
                        .data(response)
                        .build());
    }




//==============================================================================================================


    @Operation(summary = "Change/Grant a role to an existing user.")
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


    @Operation(summary = "Change/Grant a role to an existing user.")
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


    @Operation(summary = "List all users with their roles and permissions.")
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

    @Operation(summary = "Get a single user by their ID.")
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

    @Operation(summary = "Delete a user by ID.")
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


    @Operation(summary = "Update the status of an existing user (ACTIVE, INACTIVE, LOCKED, DISABLED, PASSWORD_EXPIRED).")
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
