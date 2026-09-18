package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16LastFieldsRequest;
import com.my_hourly.form_16.dto.Form16LastFieldsResponse;

public interface Form16LastFieldsService {

    Form16LastFieldsResponse createLastFields(
            Long form16Id,
            Form16LastFieldsRequest request
    );

    Form16LastFieldsResponse getLastFields(
            Long form16Id
    );

    Form16LastFieldsResponse updateLastFields(
            Long form16Id,
            Form16LastFieldsRequest request
    );

    Form16LastFieldsResponse autoSaveLastFields(
            Long form16Id,
            Form16LastFieldsRequest request
    );

    void deleteLastFields(
            Long form16Id
    );
}