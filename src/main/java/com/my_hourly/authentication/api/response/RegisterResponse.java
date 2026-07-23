package com.my_hourly.authentication.api.response;

import com.my_hourly.authentication.entity.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;

    private String username;

    private String email;

    private List<RoleName> roles;

}
