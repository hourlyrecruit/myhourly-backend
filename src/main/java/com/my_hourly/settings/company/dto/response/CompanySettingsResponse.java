package com.my_hourly.settings.company.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettingsResponse {

    private Long id;

    private String companyName;

    private String companyCode;

    private String email;

    private String phoneNumber;

    private String website;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String timeZone;

    private String currency;

    private Integer workingDaysPerWeek;

    private String logoUrl;

    private Boolean active;
}
