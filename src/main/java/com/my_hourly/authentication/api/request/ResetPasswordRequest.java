package com.my_hourly.authentication.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank
    private String password;

}
