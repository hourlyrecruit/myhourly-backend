package com.my_hourly.settings.company.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

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

    @NotBlank(message = "Phone number is required.")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number."
    )
    private String phoneNumber;

    @URL(message = "Invalid website URL format.")
    @Size(max = 150)
    private String website;

    @Size(max = 255)
    @NotBlank(message = "Address line 1 is required.")
    private String addressLine1;

    @Size(max = 255)
    @NotBlank(message = "Address line 2 is required.")
    private String addressLine2;

    @Size(max = 100)
    @NotBlank(message = "City is required.")
    private String city;

    @Size(max = 100)
    @NotBlank(message = "State is required.")
    private String state;

    @Size(max = 100)
    @NotBlank(message = "Country is required.")
    private String country;

    @Size(max = 20)
    @NotBlank(message = "Postal code is required.")
    private String postalCode;

    @NotBlank(message = "Time zone is required.")
    private String timeZone;

    @NotBlank(message = "Currency is required.")
    private String currency;

    @Min(value = 1)
    @Max(value = 7)
    private Integer workingDaysPerWeek;
}