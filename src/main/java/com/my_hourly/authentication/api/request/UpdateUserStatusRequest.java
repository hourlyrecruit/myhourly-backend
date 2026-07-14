package com.my_hourly.authentication.api.request;

import com.my_hourly.authentication.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserStatusRequest {

    @NotNull(message = "Status is mandatory.")
    private UserStatus status;

}
