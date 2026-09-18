package com.my_hourly.Form_16A.service;

import com.my_hourly.Form_16A.dto.Form16ChallanListResponse;
import com.my_hourly.Form_16A.dto.Form16ChallanRequest;
import com.my_hourly.Form_16A.dto.Form16ChallanResponse;

public interface Form16ChallanService {

    // =========================================================
    // CREATE
    // =========================================================

    Form16ChallanResponse createChallan(
            Long form16Id,
            Form16ChallanRequest request
    );


    // =========================================================
    // GET ALL
    // =========================================================

    Form16ChallanListResponse getAllChallans(
            Long form16Id
    );


    // =========================================================
    // GET SINGLE
    // =========================================================

    Form16ChallanResponse getChallan(
            Long form16Id,
            Long challanId
    );


    // =========================================================
    // UPDATE
    // =========================================================

    Form16ChallanResponse updateChallan(
            Long form16Id,
            Long challanId,
            Form16ChallanRequest request
    );


    // =========================================================
    // DELETE SINGLE
    // =========================================================

    void deleteChallan(
            Long form16Id,
            Long challanId
    );


    // =========================================================
    // DELETE ALL
    // =========================================================

    void deleteAllChallans(
            Long form16Id
    );
}