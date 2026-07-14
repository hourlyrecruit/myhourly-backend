package com.my_hourly.authentication.service.impl;

import com.my_hourly.authentication.api.request.ChangePasswordRequest;
import com.my_hourly.authentication.api.request.LoginRequest;
import com.my_hourly.authentication.api.request.RefreshTokenRequest;
import com.my_hourly.authentication.api.response.LoginResponse;
import com.my_hourly.authentication.api.response.RefreshTokenResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.entity.*;
import com.my_hourly.authentication.mapper.AuthenticationMapper;
import com.my_hourly.authentication.mapper.UserMapper;
import com.my_hourly.authentication.repository.RefreshTokenRepository;
import com.my_hourly.authentication.repository.RolePermissionRepository;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.authentication.repository.UserRoleRepository;
import com.my_hourly.authentication.service.AuthenticationService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.enums.RoleName;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.UnauthorizedException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.security.token.JwtProperties;
import com.my_hourly.security.token.JwtService;
import com.my_hourly.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl
        implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationMapper authenticationMapper;

    private final UserMapper userMapper;

    private final JwtProperties jwtProperties;

    private final RolePermissionRepository rolePermissionRepository;


    private void authenticate(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException ex) {

            throw new UnauthorizedException(
                    "Invalid username or password.",
                    ErrorCode.INVALID_CREDENTIALS
            );

        }
    }

    private User getUser(String usernameOrEmail) {

        return userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getId());
        claims.put("employeeId", user.getEmployeeId());
        claims.put("email", user.getEmail());

        return jwtService.generateAccessToken(
                user.getUsername(),
                claims
        );

    }

    private String generateRefreshToken(User user) {

        return jwtService.generateRefreshToken(
                user.getUsername()
        );

    }

    private void saveRefreshToken(
            User user,
            String refreshToken
    ) {

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(
                        LocalDateTime.now()
                                .plus(Duration.ofMillis(
                                        jwtProperties.getRefreshTokenExpiration()))
                )
                .revoked(false)
                .build();

        refreshTokenRepository.save(token);

    }

    private void updateLastLogin(User user) {

        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

    }

    private LoginResponse buildLoginResponse(
            User user,
            String accessToken,
            String refreshToken
    ) {

        List<UserRole> userRoles =
                userRoleRepository.findByUserId(user.getId());

        UserProfileResponse userProfile =
                userMapper.toUserProfile(user, userRoles);

        return authenticationMapper.toLoginResponse(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpiration(),
                userProfile
        );

    }

    @Override
    @Transactional
    public LoginResponse login(
            LoginRequest request
    ) {

        authenticate(request);

        User user =
                getUser(request.getUsernameOrEmail());

        String accessToken =
                generateAccessToken(user);

        String refreshToken =
                generateRefreshToken(user);

        saveRefreshToken(
                user,
                refreshToken
        );

        updateLastLogin(user);

        return buildLoginResponse(
                user,
                accessToken,
                refreshToken
        );

    }

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        RefreshToken refreshToken =
                validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken =
                generateAccessToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(jwtProperties.getAccessTokenExpiration())
                .tokenType("Bearer")
                .build();
    }

    private RefreshToken validateRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->
                                new UnauthorizedException(
                                        "Invalid refresh token.",
                                        ErrorCode.INVALID_REFRESH_TOKEN
                                ));

        if (refreshToken.isRevoked()) {
            throw new UnauthorizedException(
                    "Refresh token has been revoked.",
                    ErrorCode.INVALID_REFRESH_TOKEN
            );
        }

        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException(
                    "Refresh token has expired.",
                    ErrorCode.REFRESH_TOKEN_EXPIRED
            );
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        RefreshToken token = validateRefreshToken(refreshToken);

        revokeRefreshToken(token);

    }

    private void revokeRefreshToken(
            RefreshToken refreshToken
    ) {

        refreshToken.setRevoked(true);

        refreshToken.setRevokedAt(LocalDateTime.now());

        refreshTokenRepository.save(refreshToken);

    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        User user = SecurityUtils.getCurrentUser();

        validateOldPassword(user, request.getOldPassword());

        validateNewPassword(request);

        updatePassword(user, request.getNewPassword());

        revokeAllRefreshTokens(user);

    }

    private void validateOldPassword(
            User user,
            String oldPassword
    ) {

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {

            throw new ValidationException(
                    "Old password is incorrect.",
                    ErrorCode.INVALID_PASSWORD
            );

        }

    }

    private void validateNewPassword(
            ChangePasswordRequest request
    ) {

        if (!request.getNewPassword()
                .equals(request.getConfirmPassword())) {

            throw new ValidationException(
                    "New password and confirm password do not match.",
                    ErrorCode.PASSWORD_MISMATCH
            );

        }

    }

    private void updatePassword(
            User user,
            String password
    ) {

        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setPasswordChangedAt(LocalDateTime.now());

        userRepository.save(user);

    }

    private void revokeAllRefreshTokens(User user) {

        List<RefreshToken> tokens =
                refreshTokenRepository.findByUserAndRevokedFalse(user);

        if (tokens.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setRevokedAt(now);
        });

        refreshTokenRepository.saveAll(tokens);

    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {

        User user = SecurityUtils.getCurrentUser();

        return buildUserProfile(user);

    }

    private UserProfileResponse buildUserProfile(User user) {

        List<UserRole> userRoles =
                userRoleRepository.findAllByUser(user);

        List<RoleName> roles = userRoles.stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .toList();

        Set<String> permissions = new HashSet<>();

        for (UserRole userRole : userRoles) {

            List<RolePermission> rolePermissions =
                    rolePermissionRepository.findAllByRole(
                            userRole.getRole()
                    );

            rolePermissions.forEach(rolePermission ->
                    permissions.add(
                            rolePermission
                                    .getPermission()
                                    .getName()
                    )
            );

        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userStatus(user.getUserStatus())
                .roles(roles)
                .permissions(new ArrayList<>(permissions))
                .build();

    }
}
