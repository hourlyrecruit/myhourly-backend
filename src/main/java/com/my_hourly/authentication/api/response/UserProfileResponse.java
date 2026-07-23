package com.my_hourly.authentication.api.response;

import com.my_hourly.authentication.entity.UserStatus;
import com.my_hourly.authentication.entity.RoleName;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

//    private Long employeeId;

    private String username;

    private String email;

    private UserStatus userStatus;

    private List<RoleName> roles;

    private List<String> permissions;

}
