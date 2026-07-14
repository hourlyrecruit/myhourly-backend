package com.my_hourly.authentication.api.controller;

import com.my_hourly.authentication.api.request.ChangePasswordRequest;
import com.my_hourly.authentication.api.request.EmployeeRegisterRequest;
import com.my_hourly.authentication.api.request.LoginRequest;
import com.my_hourly.authentication.api.request.RefreshTokenRequest;
import com.my_hourly.authentication.api.response.LoginResponse;
import com.my_hourly.authentication.api.response.RefreshTokenResponse;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.service.AuthenticationService;
import com.my_hourly.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Public Endpoints: Registration, Login, Logout, Refresh Token, Change Password, CurrentUser")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(description = "Register Employee")
    @PostMapping("/register/employee")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerEmployee(
            @Valid @RequestBody EmployeeRegisterRequest request
    ) {

        RegisterResponse response =
                authenticationService.registerEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<RegisterResponse>builder()
                        .success(true)
                        .message("Employee registered successfully.")
                        .data(response)
                        .build()
        );

    }

    @Operation(description = "User login")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Login successful.")
                        .data(response)
                        .build()
        );

    }

    @Operation(description = "Refresh Token")
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        RefreshTokenResponse response =
                authenticationService.refreshToken(request);

        return ResponseEntity.ok(
                ApiResponse.<RefreshTokenResponse>builder()
                        .success(true)
                        .message("Access token refreshed successfully.")
                        .data(response)
                        .build()
        );
    }

    @Operation(description = "User Logout")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {

        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }

        authenticationService.logout(accessToken, request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logged out successfully.")
                        .build()
        );
    }

    @Operation(description = "Change Password")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        authenticationService.changePassword(request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Password changed successfully.")
                        .build()
        );
    }

    @Operation(description = "Getting About User")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser() {

        UserProfileResponse response =
                authenticationService.getCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.<UserProfileResponse>builder()
                        .success(true)
                        .message("User profile fetched successfully.")
                        .data(response)
                        .build()
        );
    }

}
