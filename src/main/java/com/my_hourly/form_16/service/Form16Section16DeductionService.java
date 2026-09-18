package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16Section16DeductionRequest;
import com.my_hourly.form_16.dto.Form16Section16DeductionResponse;

public interface Form16Section16DeductionService {

    Form16Section16DeductionResponse createDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request
    );

    Form16Section16DeductionResponse getDeductionByForm16Id(
            Long form16Id
    );

    Form16Section16DeductionResponse updateDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request
    );

    Form16Section16DeductionResponse autoSaveDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request
    );

    void deleteDeduction(
            Long form16Id
    );
}