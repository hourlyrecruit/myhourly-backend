package com.my_hourly.authentication.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, max = 20, message = "New password must be between 8 and 20 characters.")
    private String password;

}
