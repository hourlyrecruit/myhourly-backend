package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16EmployerMasterRequest;
import com.my_hourly.form_16.dto.Form16EmployerMasterResponse;

import java.util.List;

public interface Form16EmployerMasterService {


    // =========================================================
    // CREATE
    // =========================================================

    Form16EmployerMasterResponse createEmployerMaster(
            Form16EmployerMasterRequest request
    );


    // =========================================================
    // GET ACTIVE
    // =========================================================

    Form16EmployerMasterResponse getActiveEmployerMaster();


    // =========================================================
    // GET BY ID
    // =========================================================

    Form16EmployerMasterResponse getEmployerMasterById(
            Long id
    );


    // =========================================================
    // UPDATE
    // =========================================================

    Form16EmployerMasterResponse updateEmployerMaster(
            Long id,
            Form16EmployerMasterRequest request
    );


    // =========================================================
    // ACTIVATE
    // =========================================================

    Form16EmployerMasterResponse activateEmployerMaster(
            Long id
    );


    // =========================================================
    // DEACTIVATE
    // =========================================================

    Form16EmployerMasterResponse deactivateEmployerMaster(
            Long id
    );


    // =========================================================
    // DELETE
    // =========================================================

    void deleteEmployerMaster(
            Long id
    );


    // =========================================================
    // GET ALL
    // =========================================================

    List<Form16EmployerMasterResponse>
    getAllEmployerMasters();
}
