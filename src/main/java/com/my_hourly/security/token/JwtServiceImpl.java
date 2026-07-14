package com.my_hourly.security.token;

import java.util.Map;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(
            String username,
            Map<String, Object> claims
    ) {
        return generateToken(
                username,
                claims,
                jwtProperties.getAccessTokenExpiration()
        );
    }

    @Override
    public String generateRefreshToken(
            String username
    ) {
        return generateToken(
                username,
                new HashMap<>(),
                jwtProperties.getRefreshTokenExpiration()
        );
    }

    private String generateToken(
            String username,
            Map<String, Object> claims,
            long expiration
    ) {

        Date now = new Date();

        Date expiryDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(
                        getSigningKey(),
                        Jwts.SIG.HS256
                )
                .compact();

    }

    @Override
    public String extractUsername(
            String token
    ) {
        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    @Override
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    @Override
    public boolean isTokenExpired(
            String token
    ) {
        return extractClaim(
                token,
                Claims::getExpiration
        ).before(new Date());
    }

    @Override
    public boolean isTokenValid(
            String token,
            String username
    ) {

        String extractedUsername = extractUsername(token);

        return extractedUsername.equals(username)
                && !isTokenExpired(token);

    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.getSecret()
        );

        return Keys.hmacShaKeyFor(keyBytes);

    }

}