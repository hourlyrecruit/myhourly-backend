package com.my_hourly.settings.company.entity;

import com.my_hourly.settings.BaseSettings;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "company_settings"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySettings extends BaseSettings {

    @Column(nullable = false, length = 150)
    private String companyName;

    @Column(nullable = false, unique = true, length = 30)
    private String companyCode;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 150)
    private String website;

    @Column(length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 20)
    private String postalCode;

    @Column(nullable = false, length = 50)
    private String timeZone;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private Integer workingDaysPerWeek;

    @Column(length = 500)
    private String logoUrl;

    private boolean active;
}
