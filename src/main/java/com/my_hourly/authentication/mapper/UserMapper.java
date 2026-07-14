package com.my_hourly.authentication.mapper;

import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.entity.Role;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfile(User user, List<UserRole> userRoles) {

        List<String> roles = userRoles.stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}