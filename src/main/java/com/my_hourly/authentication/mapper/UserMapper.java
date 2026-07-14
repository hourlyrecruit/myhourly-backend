package com.my_hourly.authentication.mapper;

import com.my_hourly.authentication.api.response.UserProfileResponse;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.enums.RoleName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public UserProfileResponse toUserProfile(User user) {
        if (user == null) {
            return null;
        }

        List<RoleName> roles = user.getRole() != null ? List.of(user.getRole()) : List.of();
        List<String> permissions = new ArrayList<>();

        if (user.getRole() != null) {
            switch (user.getRole()) {
                case SUPER_ADMIN:
                case HR_ADMIN:
                    permissions.addAll(List.of(
                            "department:create", "department:view", "department:update", "department:delete",
                            "designation:create", "designation:view", "designation:update", "designation:delete"
                    ));
                    break;
                case MANAGER:
                case EMPLOYEE:
                    permissions.addAll(List.of("department:view", "designation:view"));
                    break;
                default:
                    break;
            }
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .employeeId(user.getEmployeeId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userStatus(user.getUserStatus())
                .roles(roles)
                .permissions(permissions)
                .build();
    }
}