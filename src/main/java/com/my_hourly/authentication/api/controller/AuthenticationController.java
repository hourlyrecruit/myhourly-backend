package com.my_hourly.authentication.api.controller;

import com.my_hourly.authentication.api.request.ChangePasswordRequest;
import com.my_hourly.authentication.api.request.LoginRequest;
import com.my_hourly.authentication.api.request.RefreshTokenRequest;
import com.my_hourly.authentication.api.response.LoginResponse;
import com.my_hourly.authentication.api.response.RefreshTokenResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.service.AuthenticationService;
import com.my_hourly.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        authenticationService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Logged out successfully.")
                        .build()
        );
    }

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
