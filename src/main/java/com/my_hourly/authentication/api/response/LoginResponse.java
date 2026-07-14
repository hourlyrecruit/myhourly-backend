package com.my_hourly.authentication.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private String tokenType;

    private UserProfileResponse user;

}
