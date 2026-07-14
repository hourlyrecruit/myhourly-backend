package com.my_hourly.authentication.mapper;

import com.my_hourly.authentication.api.response.LoginResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationMapper {

    public LoginResponse toLoginResponse(
            String accessToken,
            String refreshToken,
            Long expiresIn,
            UserProfileResponse user
    ) {

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .tokenType("Bearer")
                .user(user)
                .build();
    }

}