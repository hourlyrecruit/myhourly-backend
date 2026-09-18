package com.my_hourly.form_16.serviceImpl;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.form_16.dto.Form16Request;
import com.my_hourly.form_16.dto.Form16Response;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16EmployerMaster;
import com.my_hourly.form_16.repository.Form16EmployerMasterRepository;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.service.Form16Service;
import com.my_hourly.payroll.entity.EmployeePaymentDetails;
import com.my_hourly.payroll.repository.EmployeePaymentDetailsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16ServiceImpl implements Form16Service {

    private final Form16Repository form16Repository;

    private final EmployeeRepository employeeRepository;

    private final EmployeePaymentDetailsRepository employeePaymentDetailsRepository;

    private final Form16EmployerMasterRepository employerMasterRepository;


    // =========================================================
    // CREATE FORM 16
    // =========================================================

    @Override
    public Form16Response createForm16(
            Long employeeId,
            Form16Request request) {

        // -----------------------------------------------------
        // 1. Find Employee
        // -----------------------------------------------------

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found with ID: " + employeeId
                        )
                );


        // -----------------------------------------------------
        // 2. Find Active Employer Master
        // -----------------------------------------------------

        Form16EmployerMaster employerMaster =
                employerMasterRepository.findByActiveTrue()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active employer master not found."
                                )
                        );


        // -----------------------------------------------------
        // 3. Get MANUAL values from Request
        // -----------------------------------------------------

        String employeeAddress =
                request.getEmployeeAddress();

        String assessmentYear =
                request.getAssessmentYear();

        LocalDate employmentFrom =
                request.getEmploymentFrom();

        LocalDate employmentTo =
                request.getEmploymentTo();


        // -----------------------------------------------------
        // 4. Validate Manual Fields
        // -----------------------------------------------------

        if (employeeAddress == null
                || employeeAddress.trim().isEmpty()) {

            throw new RuntimeException(
                    "Employee address is required."
            );
        }


        if (assessmentYear == null
                || assessmentYear.trim().isEmpty()) {

            throw new RuntimeException(
                    "Assessment year is required."
            );
        }


        if (employmentFrom == null) {

            throw new RuntimeException(
                    "Employment from date is required."
            );
        }


        if (employmentTo == null) {

            throw new RuntimeException(
                    "Employment to date is required."
            );
        }


        if (employmentFrom.isAfter(employmentTo)) {

            throw new RuntimeException(
                    "Employment from date cannot be after employment to date."
            );
        }


        // -----------------------------------------------------
        // 5. Check Duplicate Form16
        // -----------------------------------------------------

        if (form16Repository
                .existsByEmployeeIdAndAssessmentYear(
                        employeeId,
                        assessmentYear)) {

            throw new RuntimeException(
                    "Form16 already exists for employee ID "
                            + employeeId
                            + " and assessment year "
                            + assessmentYear
            );
        }


        // -----------------------------------------------------
        // 6. Get Employee PAN
        // -----------------------------------------------------

        String employeePan = null;

        Optional<EmployeePaymentDetails> paymentDetailsOptional =
                employeePaymentDetailsRepository
                        .findByEmployeeId(employeeId);

        if (paymentDetailsOptional.isPresent()) {

            EmployeePaymentDetails paymentDetails =
                    paymentDetailsOptional.get();

            employeePan =
                    paymentDetails.getPanNumber();
        }


        // -----------------------------------------------------
        // 7. Generate Certificate Number
        // -----------------------------------------------------

        String certificateNo =
                generateCertificateNumber();


        // -----------------------------------------------------
        // 8. Create Form16
        // -----------------------------------------------------

        Form16 form16 =
                Form16.builder()

                        // -------------------------------------------------
                        // Employee
                        // -------------------------------------------------

                        .employee(employee)

                        // -------------------------------------------------
                        // Certificate
                        // -------------------------------------------------

                        .certificateNo(certificateNo)

                        .lastUpdatedOn(LocalDate.now())


                        // -------------------------------------------------
                        // Employer Master
                        // -------------------------------------------------

                        .employerName(
                                employerMaster.getEmployerName()
                        )

                        .employerAddress(
                                employerMaster.getEmployerAddress()
                        )

                        .employerPhone(
                                employerMaster.getEmployerPhone()
                        )

                        .employerEmail(
                                employerMaster.getEmployerEmail()
                        )


                        // -------------------------------------------------
                        // Employee Address
                        // MANUAL
                        // -------------------------------------------------

                        .employeeAddress(
                                employeeAddress
                        )


                        // -------------------------------------------------
                        // Tax Deductor
                        // -------------------------------------------------

                        .deductorPan(
                                employerMaster.getDeductorPan()
                        )

                        .deductorTan(
                                employerMaster.getDeductorTan()
                        )


                        // -------------------------------------------------
                        // Employee PAN
                        // Automatically taken from Payment Details
                        // -------------------------------------------------

                        .employeePan(
                                employeePan
                        )


                        // -------------------------------------------------
                        // CIT TDS Address
                        // -------------------------------------------------

                        .citTdsAddress(
                                employerMaster.getCitTdsAddress()
                        )


                        // -------------------------------------------------
                        // Assessment Year
                        // MANUAL
                        // -------------------------------------------------

                        .assessmentYear(
                                assessmentYear
                        )


                        // -------------------------------------------------
                        // Employment From
                        // MANUAL
                        // -------------------------------------------------

                        .employmentFrom(
                                employmentFrom
                        )


                        // -------------------------------------------------
                        // Employment To
                        // MANUAL
                        // -------------------------------------------------

                        .employmentTo(
                                employmentTo
                        )


                        // -------------------------------------------------
                        // Taxation Option
                        // -------------------------------------------------

                        .optingOutOfTaxation115BAC1A(
                                request
                                        .getOptingOutOfTaxation115BAC1A()
                        )


                        // -------------------------------------------------
                        // Default
                        // -------------------------------------------------

                        .active(false)

                        .build();


        // -----------------------------------------------------
        // 9. Save Form16
        // -----------------------------------------------------

        Form16 savedForm16 =
                form16Repository.save(form16);


        // -----------------------------------------------------
        // 10. Return Response
        // -----------------------------------------------------

        return mapToResponse(savedForm16);
    }


    // =========================================================
    // GET FORM 16 BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Response getForm16(Long id) {

        Form16 form16 =
                form16Repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with ID: " + id
                                )
                        );

        return mapToResponse(form16);
    }


    // =========================================================
    // GET FORM 16 FOR LOGGED-IN EMPLOYEE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Response getForm16ForLoggedInEmployee(
            Long form16Id) {

        Form16 form16 =
                form16Repository.findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with ID: "
                                                + form16Id
                                )
                        );

        /*
         * Keep your existing logged-in employee
         * authorization logic here if you already have
         * employee authentication lookup.
         *
         * This method currently returns the Form16.
         */

        return mapToResponse(form16);
    }


    // =========================================================
    // GET FORM 16 BY EMPLOYEE + ASSESSMENT YEAR
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Response getForm16ByEmployeeAndAssessmentYear(
            Long employeeId,
            String assessmentYear) {

        Form16 form16 =
                form16Repository
                        .findByEmployeeIdAndAssessmentYear(
                                employeeId,
                                assessmentYear
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found for employee ID "
                                                + employeeId
                                                + " and assessment year "
                                                + assessmentYear
                                )
                        );

        return mapToResponse(form16);
    }


    // =========================================================
    // ACTIVATE EMPLOYEE FORM 16
    // =========================================================

    @Override
    public void activateEmployeeForm16(Long employeeId) {

        List<Form16> form16List =
                form16Repository.findAllByEmployeeId(employeeId);

        if (form16List.isEmpty()) {

            throw new RuntimeException(
                    "Form16 not found for employee ID: "
                            + employeeId
            );
        }


        for (Form16 form16 : form16List) {

            form16.setActive(true);

            form16.setLastUpdatedOn(
                    LocalDate.now()
            );
        }


        form16Repository.saveAll(form16List);
    }


    // =========================================================
    // ACTIVATE ALL FORM 16
    // =========================================================

    @Override
    public void activateAllForm16() {

        List<Form16> form16List =
                form16Repository.findAll();

        if (form16List.isEmpty()) {

            throw new RuntimeException(
                    "No Form16 records found."
            );
        }


        for (Form16 form16 : form16List) {

            form16.setActive(true);

            form16.setLastUpdatedOn(
                    LocalDate.now()
            );
        }


        form16Repository.saveAll(form16List);
    }


    // =========================================================
    // DEACTIVATE EMPLOYEE FORM 16
    // =========================================================

    @Override
    public void deactivateEmployeeForm16(
            Long employeeId) {

        List<Form16> form16List =
                form16Repository.findAllByEmployeeId(employeeId);

        if (form16List.isEmpty()) {

            throw new RuntimeException(
                    "Form16 not found for employee ID: "
                            + employeeId
            );
        }


        for (Form16 form16 : form16List) {

            form16.setActive(false);

            form16.setLastUpdatedOn(
                    LocalDate.now()
            );
        }


        form16Repository.saveAll(form16List);
    }


    // =========================================================
    // DELETE EMPLOYEE FORM 16
    // =========================================================

    @Override
    public void deleteEmployeeForm16(
            Long employeeId) {

        List<Form16> form16List =
                form16Repository.findAllByEmployeeId(employeeId);

        if (form16List.isEmpty()) {

            throw new RuntimeException(
                    "Form16 not found for employee ID: "
                            + employeeId
            );
        }


        form16Repository.deleteAll(form16List);
    }


    // =========================================================
    // CERTIFICATE NUMBER
    // =========================================================

    private String generateCertificateNumber() {

        String certificateNo;

        do {

            certificateNo =
                    randomAlphaNumeric(8);

        } while (
                form16Repository
                        .existsByCertificateNo(
                                certificateNo
                        )
        );


        return certificateNo;
    }


    // =========================================================
    // RANDOM ALPHANUMERIC
    // =========================================================

    private String randomAlphaNumeric(int length) {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder result =
                new StringBuilder(length);


        for (int i = 0; i < length; i++) {

            int index =
                    ThreadLocalRandom.current()
                            .nextInt(
                                    characters.length()
                            );

            result.append(
                    characters.charAt(index)
            );
        }


        return result.toString();
    }


    // =========================================================
    // MAP ENTITY TO RESPONSE
    // =========================================================

    private Form16Response mapToResponse(
            Form16 form16) {

        Employee employee =
                form16.getEmployee();


        String designation = null;

        if (employee != null
                && employee.getDesignation() != null) {

            /*
             * If your Designation entity has:
             *
             * getDesignationName()
             *
             * then preferably use:
             *
             * designation =
             *     employee.getDesignation()
             *             .getDesignationName();
             */

            designation =
                    employee.getDesignation()
                            .toString();
        }


        return Form16Response.builder()

                // -------------------------------------------------
                // Form16 ID
                // -------------------------------------------------

                .id(
                        form16.getId()
                )


                // -------------------------------------------------
                // Employee ID
                // -------------------------------------------------

                .employeeId(
                        employee != null
                                ? employee.getId()
                                : null
                )


                // -------------------------------------------------
                // Employee Code
                // -------------------------------------------------

                .employeeCode(
                        employee != null
                                ? employee.getEmployeeCode()
                                : null
                )


                // -------------------------------------------------
                // Employee Name
                // -------------------------------------------------

                .employeeName(
                        employee != null
                                ? (
                                employee.getFirstName()
                                + " "
                                + (
                                        employee.getLastName() != null
                                        ? employee.getLastName()
                                        : ""
                                )
                        ).trim()
                                : null
                )


                // -------------------------------------------------
                // Designation
                // -------------------------------------------------

                .designation(
                        designation
                )


                // -------------------------------------------------
                // Date Of Joining
                // -------------------------------------------------

                .dateOfJoining(
                        employee != null
                                ? employee.getDateOfJoining()
                                : null
                )


                // -------------------------------------------------
                // Certificate Number
                // -------------------------------------------------

                .certificateNo(
                        form16.getCertificateNo()
                )


                // -------------------------------------------------
                // Last Updated
                // -------------------------------------------------

                .lastUpdatedOn(
                        form16.getLastUpdatedOn()
                )


                // -------------------------------------------------
                // Employee PAN
                // -------------------------------------------------

                .employeePan(
                        form16.getEmployeePan()
                )


                // -------------------------------------------------
                // Employer
                // -------------------------------------------------

                .employerName(
                        form16.getEmployerName()
                )

                .employerAddress(
                        form16.getEmployerAddress()
                )

                .employerPhone(
                        form16.getEmployerPhone()
                )

                .employerEmail(
                        form16.getEmployerEmail()
                )


                // -------------------------------------------------
                // Employee Address
                // -------------------------------------------------

                .employeeAddress(
                        form16.getEmployeeAddress()
                )


                // -------------------------------------------------
                // Deductor
                // -------------------------------------------------

                .deductorPan(
                        form16.getDeductorPan()
                )

                .deductorTan(
                        form16.getDeductorTan()
                )


                // -------------------------------------------------
                // CIT TDS Address
                // -------------------------------------------------

                .citTdsAddress(
                        form16.getCitTdsAddress()
                )


                // -------------------------------------------------
                // Assessment Year
                // -------------------------------------------------

                .assessmentYear(
                        form16.getAssessmentYear()
                )


                // -------------------------------------------------
                // Employment Period
                // -------------------------------------------------

                .employmentFrom(
                        form16.getEmploymentFrom()
                )

                .employmentTo(
                        form16.getEmploymentTo()
                )


                // -------------------------------------------------
                // Taxation Option
                // -------------------------------------------------

                .optingOutOfTaxation115BAC1A(
                        form16
                                .getOptingOutOfTaxation115BAC1A()
                )


                // -------------------------------------------------
                // Active
                // -------------------------------------------------

                .active(
                        form16.getActive()
                )


                .build();
    }
}