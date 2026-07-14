package com.my_hourly.security.config;

public final class SecurityConstants {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/actuator/health"
    };

    private SecurityConstants() {}
}
