package com.my_hourly.form_16.service;

import com.my_hourly.form_16.dto.Form16Request;
import com.my_hourly.form_16.dto.Form16Response;

public interface Form16Service {


    // =========================================================
    // CREATE FORM16
    // =========================================================

    Form16Response createForm16(
            Long employeeId,
            Form16Request request
    );


    // =========================================================
    // GET FORM16 BY ID
    // =========================================================

    Form16Response getForm16(
            Long id
    );


    // =========================================================
    // GET FORM16 FOR LOGGED-IN EMPLOYEE
    // =========================================================

    Form16Response getForm16ForLoggedInEmployee(
            Long form16Id
    );


    // =========================================================
    // GET BY EMPLOYEE + ASSESSMENT YEAR
    // =========================================================

    Form16Response getForm16ByEmployeeAndAssessmentYear(
            Long employeeId,
            String assessmentYear
    );


    // =========================================================
    // ACTIVATE ONE EMPLOYEE
    // =========================================================

    void activateEmployeeForm16(
            Long employeeId
    );


    // =========================================================
    // ACTIVATE ALL
    // =========================================================

    void activateAllForm16();


    // =========================================================
    // DEACTIVATE ONE EMPLOYEE
    // =========================================================

    void deactivateEmployeeForm16(
            Long employeeId
    );


    // =========================================================
    // DELETE ONE EMPLOYEE FORM16
    // =========================================================

    void deleteEmployeeForm16(
            Long employeeId
    );
}