package com.my_hourly.Form_16A.service;

import com.my_hourly.Form_16A.dto.Form16QuarterRequest;
import com.my_hourly.Form_16A.dto.Form16QuarterResponse;

public interface Form16QuarterService {

    // =========================================================
    // CREATE
    // =========================================================

    Form16QuarterResponse createQuarter(
            Long form16Id,
            Form16QuarterRequest request
    );


    // =========================================================
    // GET
    // =========================================================

    Form16QuarterResponse getQuarterByForm16Id(
            Long form16Id
    );


    // =========================================================
    // UPDATE
    // =========================================================

    Form16QuarterResponse updateQuarter(
            Long form16Id,
            Form16QuarterRequest request
    );


    // =========================================================
    // DELETE
    // =========================================================

    void deleteQuarter(
            Long form16Id
    );
}