package com.my_hourly.settings.company.mapper;

import com.my_hourly.settings.company.dto.request.CompanySettingsRequest;
import com.my_hourly.settings.company.dto.response.CompanySettingsResponse;
import com.my_hourly.settings.company.entity.CompanySettings;
import org.springframework.stereotype.Component;

@Component
public class CompanySettingsMapper {

    public CompanySettingsResponse toResponse(CompanySettings entity) {

        return CompanySettingsResponse.builder()
                .id(entity.getId())
                .companyName(entity.getCompanyName())
                .companyCode(entity.getCompanyCode())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .website(entity.getWebsite())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .postalCode(entity.getPostalCode())
                .timeZone(entity.getTimeZone())
                .currency(entity.getCurrency())
                .workingDaysPerWeek(entity.getWorkingDaysPerWeek())
                .logoUrl(entity.getLogoUrl())
                .active(entity.getActive())
                .build();
    }

    public void updateEntity(CompanySettingsRequest request,
                             CompanySettings entity) {

        entity.setCompanyName(request.getCompanyName());
        entity.setCompanyCode(request.getCompanyCode());
        entity.setEmail(request.getEmail());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setWebsite(request.getWebsite());
        entity.setAddressLine1(request.getAddressLine1());
        entity.setAddressLine2(request.getAddressLine2());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setCountry(request.getCountry());
        entity.setPostalCode(request.getPostalCode());
        entity.setTimeZone(request.getTimeZone());
        entity.setCurrency(request.getCurrency());
        entity.setWorkingDaysPerWeek(request.getWorkingDaysPerWeek());
    }
}