package com.my_hourly.settings.company.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettingsRequest {

    @NotBlank(message = "Company name is required.")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Company code is required.")
    @Size(max = 30)
    private String companyCode;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @Pattern(
            regexp = "^[0-9]{10,15}$",
            message = "Phone number must contain 10 to 15 digits."
    )
    private String phoneNumber;

    @Size(max = 150)
    private String website;

    @Size(max = 255)
    private String addressLine1;

    @Size(max = 255)
    private String addressLine2;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;

    @Size(max = 20)
    private String postalCode;

    @NotBlank(message = "Time zone is required.")
    private String timeZone;

    @NotBlank(message = "Currency is required.")
    private String currency;

    @Min(value = 1)
    @Max(value = 7)
    private Integer workingDaysPerWeek;
}