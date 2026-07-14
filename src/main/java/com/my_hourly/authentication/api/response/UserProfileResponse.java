package com.my_hourly.authentication.api.response;

import com.my_hourly.authentication.entity.UserStatus;
import com.my_hourly.common.enums.RoleName;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;

    private UUID employeeId;

    private String username;

    private String email;

    private UserStatus userStatus;

    private List<RoleName> roles;

    private List<String> permissions;

}
