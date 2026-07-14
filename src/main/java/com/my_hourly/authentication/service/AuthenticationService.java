package com.my_hourly.authentication.service;

import com.my_hourly.authentication.api.request.*;
import com.my_hourly.authentication.api.response.LoginResponse;
import com.my_hourly.authentication.api.response.RefreshTokenResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    );

    void logout(String refreshToken);

    void changePassword(
            ChangePasswordRequest request
    );
//
//    void forgotPassword(
//            ForgotPasswordRequest request
//    );
//
//    void resetPassword(
//            ResetPasswordRequest request
//    );

    UserProfileResponse getCurrentUser();
}
