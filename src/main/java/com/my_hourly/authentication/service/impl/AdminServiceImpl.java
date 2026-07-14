package com.my_hourly.authentication.service.impl;

import com.my_hourly.authentication.api.request.AdminRegisterRequest;
import com.my_hourly.authentication.api.request.GrantRoleRequest;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.entity.*;
import com.my_hourly.authentication.repository.*;
import com.my_hourly.authentication.service.AdminService;
import com.my_hourly.authentication.mapper.UserMapper;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.enums.RoleName;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.DuplicateResourceException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public RegisterResponse registerUser(AdminRegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException(
                    "Username '" + request.getUsername() + "' is already taken.",
                    ErrorCode.USER_ALREADY_EXISTS
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email '" + request.getEmail() + "' is already registered.",
                    ErrorCode.USER_ALREADY_EXISTS
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userStatus(UserStatus.ACTIVE)
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(List.of(user.getRole()))
                .build();

    }

    @Override
    @Transactional
    public void grantRole(Long userId, GrantRoleRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        if (user.getRole() == request.getRole()) {
            throw new DuplicateResourceException(
                    "Role '" + request.getRole() + "' is already assigned to user.",
                    ErrorCode.ROLE_ALREADY_ASSIGNED
            );
        }

        user.setRole(request.getRole());
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void revokeRole(Long userId, RoleName roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        if (user.getRole() != roleName) {
            throw new BadRequestException(
                    "Role '" + roleName + "' is not assigned to this user.",
                    ErrorCode.ROLE_NOT_ASSIGNED
            );
        }

        user.setRole(RoleName.EMPLOYEE);
        userRepository.save(user);

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::buildUserProfile)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getUserById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + userId,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        return buildUserProfile(user);

    }

    private UserProfileResponse buildUserProfile(User user) {
        return userMapper.toUserProfile(user);
    }

}
