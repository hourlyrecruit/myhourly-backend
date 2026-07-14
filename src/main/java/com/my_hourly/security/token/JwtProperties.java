package com.my_hourly.security.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key used to sign JWT tokens.
     */
    private String secret;

    /**
     * Access token expiration time in milliseconds.
     */
    private long accessTokenExpiration;

    /**
     * Refresh token expiration time in milliseconds.
     */
    private long refreshTokenExpiration;
}