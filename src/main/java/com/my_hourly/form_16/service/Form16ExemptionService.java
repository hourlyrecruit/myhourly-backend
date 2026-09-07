package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16ExemptionRequest;
import com.my_hourly.form_16.dto.Form16ExemptionResponse;

public interface Form16ExemptionService {

    Form16ExemptionResponse createExemption(
            Long form16Id,
            Form16ExemptionRequest request
    );


    Form16ExemptionResponse getExemptionByForm16Id(
            Long form16Id
    );


    Form16ExemptionResponse updateExemption(
            Long form16Id,
            Form16ExemptionRequest request
    );


    void deleteExemption(
            Long form16Id
    );
}