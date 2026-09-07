package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16EmployerMasterRequest;
import com.my_hourly.form_16.dto.Form16EmployerMasterResponse;

import java.util.List;

public interface Form16EmployerMasterService {

    Form16EmployerMasterResponse createEmployerMaster(
            Form16EmployerMasterRequest request
    );

    Form16EmployerMasterResponse getActiveEmployerMaster();

    Form16EmployerMasterResponse getEmployerMasterById(
            Long id
    );

    Form16EmployerMasterResponse updateEmployerMaster(
            Long id,
            Form16EmployerMasterRequest request
    );

    Form16EmployerMasterResponse activateEmployerMaster(
            Long id
    );

    Form16EmployerMasterResponse deactivateEmployerMaster(
            Long id
    );

    void deleteEmployerMaster(
            Long id
    );

    List<Form16EmployerMasterResponse> getAllEmployerMasters();
}