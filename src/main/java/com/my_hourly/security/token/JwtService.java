package com.my_hourly.security.token;

import java.util.Map;

public interface JwtService {

    String generateAccessToken(
            String username,
            Map<String, Object> claims
    );

    String generateRefreshToken(
            String username
    );

    String extractUsername(String token);

    <T> T extractClaim(
            String token,
            java.util.function.Function<io.jsonwebtoken.Claims, T> claimsResolver
    );

    boolean isTokenExpired(String token);

    java.time.LocalDateTime extractExpiration(String token);

    boolean isTokenValid(
            String token,
            String username
    );

}
