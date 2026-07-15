package com.my_hourly.authentication.api.request;

import com.my_hourly.authentication.entity.RoleName;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GrantRoleRequest {

    @NotNull(message = "Role is required.")
    private RoleName role;

}
