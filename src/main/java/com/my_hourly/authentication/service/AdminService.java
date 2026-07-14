package com.my_hourly.authentication.service;

import com.my_hourly.authentication.api.request.AdminRegisterRequest;
import com.my_hourly.authentication.api.request.GrantRoleRequest;
import com.my_hourly.authentication.api.request.UpdateUserStatusRequest;
import com.my_hourly.authentication.api.response.RegisterResponse;
import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.common.enums.RoleName;

import java.util.List;


public interface AdminService {

    /**
     * Register a new user with any specified role (SUPER_ADMIN only).
     */
    RegisterResponse registerUser(AdminRegisterRequest request);

    /**
     * Grant a role to an existing user.
     */
    void grantRole(Long userId, GrantRoleRequest request);

    /**
     * Revoke a role from an existing user.
     */
    void revokeRole(Long userId, RoleName roleName);

    /**
     * Retrieve all users with their roles and permissions.
     */
    List<UserProfileResponse> getAllUsers();

    /**
     * Retrieve a single user by ID.
     */
    UserProfileResponse getUserById(Long userId);

    /**
     * Delete an existing user.
     */
    void deleteUser(Long userId);

    /**
     * Update the status of an existing user (ACTIVE, INACTIVE, LOCKED, DISABLED, PASSWORD_EXPIRED).
     * Revoking sessions when user is set to any non-ACTIVE status.
     */
    void updateUserStatus(Long userId, UpdateUserStatusRequest request);

}
