
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

//import com.my_hourly.payment_details.entity.EmployeePaymentDetails;
//import com.my_hourly.payment_details.repository.EmployeePaymentDetailsRepository;

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
        // 3. Automatically Generate Assessment Year
        // -----------------------------------------------------

        String assessmentYear = generateAssessmentYear();


        // -----------------------------------------------------
        // 4. Check Duplicate Form16
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
        // 5. Get Employee PAN
        // -----------------------------------------------------

        String employeePan = null;

        Optional<EmployeePaymentDetails> paymentDetailsOptional =
                employeePaymentDetailsRepository
                        .findByEmployeeId(employeeId);

        if (paymentDetailsOptional.isPresent()) {

            EmployeePaymentDetails paymentDetails =
                    paymentDetailsOptional.get();

            employeePan = paymentDetails.getPanNumber();
        }


        // -----------------------------------------------------
        // 6. Generate Certificate Number
        // -----------------------------------------------------

        String certificateNo = generateCertificateNumber();


        // -----------------------------------------------------
        // 7. Generate Employment Period
        // -----------------------------------------------------

        LocalDate financialYearStart =
                getFinancialYearStart();

        LocalDate financialYearEnd =
                getFinancialYearEnd();


        LocalDate dateOfJoining =
                employee.getDateOfJoining();


        LocalDate employmentFrom;

        if (dateOfJoining != null
                && dateOfJoining.isAfter(financialYearStart)) {

            employmentFrom = dateOfJoining;

        } else {

            employmentFrom = financialYearStart;
        }


        LocalDate employmentTo =
                financialYearEnd;


        // -----------------------------------------------------
        // 8. Employee Address
        // -----------------------------------------------------

        String employeeAddress = request.getEmployeeAddress();


        // -----------------------------------------------------
        // 9. Reuse Existing Employee Address
        // -----------------------------------------------------

        Optional<Form16> firstForm16Optional =
                form16Repository
                        .findFirstByEmployeeIdOrderByIdAsc(employeeId);

        if (firstForm16Optional.isPresent()) {

            Form16 firstForm16 =
                    firstForm16Optional.get();

            if (firstForm16.getEmployeeAddress() != null
                    && !firstForm16.getEmployeeAddress()
                    .trim()
                    .isEmpty()) {

                employeeAddress =
                        firstForm16.getEmployeeAddress();
            }
        }


        // -----------------------------------------------------
        // 10. Create Form16
        // -----------------------------------------------------

        Form16 form16 =
                Form16.builder()

                        .employee(employee)

                        .certificateNo(certificateNo)

                        .lastUpdatedOn(LocalDate.now())


                        // Employer Master
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


                        // Employee
                        .employeeAddress(employeeAddress)


                        // Tax Deductor
                        .deductorPan(
                                employerMaster.getDeductorPan()
                        )

                        .deductorTan(
                                employerMaster.getDeductorTan()
                        )

                        .employeePan(employeePan)

                        .citTdsAddress(
                                employerMaster.getCitTdsAddress()
                        )


                        // Automatically Generated
                        .assessmentYear(assessmentYear)

                        .employmentFrom(employmentFrom)

                        .employmentTo(employmentTo)


                        // Request value
                        .optingOutOfTaxation115BAC1A(
                                request.getOptingOutOfTaxation115BAC1A()
                        )


                        // Default
                        .active(false)

                        .build();


        // -----------------------------------------------------
        // 11. Save
        // -----------------------------------------------------

        Form16 savedForm16 =
                form16Repository.save(form16);


        // -----------------------------------------------------
        // 12. Return Response
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
    // AUTOMATIC ASSESSMENT YEAR
    // =========================================================

    private String generateAssessmentYear() {

        LocalDate today =
                LocalDate.now();

        int year =
                today.getYear();

        int month =
                today.getMonthValue();


        /*
         * Indian Financial Year:
         *
         * April 1 to March 31
         *
         * Example:
         *
         * September 2026
         * FY = 2026-27
         * AY = 2027-28
         */

        int assessmentStartYear;

        if (month >= 4) {

            assessmentStartYear =
                    year + 1;

        } else {

            assessmentStartYear =
                    year;
        }


        return assessmentStartYear
                + "-"
                + String.valueOf(
                assessmentStartYear + 1
        ).substring(2);
    }


    // =========================================================
    // FINANCIAL YEAR START
    // =========================================================

    private LocalDate getFinancialYearStart() {

        LocalDate today =
                LocalDate.now();

        int year =
                today.getYear();

        int month =
                today.getMonthValue();


        if (month >= 4) {

            return LocalDate.of(
                    year,
                    4,
                    1
            );

        } else {

            return LocalDate.of(
                    year - 1,
                    4,
                    1
            );
        }
    }


    // =========================================================
    // FINANCIAL YEAR END
    // =========================================================

    private LocalDate getFinancialYearEnd() {

        LocalDate today =
                LocalDate.now();

        int year =
                today.getYear();

        int month =
                today.getMonthValue();


        if (month >= 4) {

            return LocalDate.of(
                    year + 1,
                    3,
                    31
            );

        } else {

            return LocalDate.of(
                    year,
                    3,
                    31
            );
        }
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
             * IMPORTANT:
             *
             * If your Designation entity has a getter such as
             * getDesignationName(), use:
             *
             * designation =
             *     employee.getDesignation()
             *             .getDesignationName();
             *
             * For now we avoid the incorrect object toString()
             * output.
             */
            designation =
                    employee.getDesignation()
                            .toString();
        }


        return Form16Response.builder()

                .id(form16.getId())

                .employeeId(
                        employee != null
                                ? employee.getId()
                                : null
                )

                .employeeCode(
                        employee != null
                                ? employee.getEmployeeCode()
                                : null
                )

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

                .designation(designation)

                .dateOfJoining(
                        employee != null
                                ? employee.getDateOfJoining()
                                : null
                )

                .certificateNo(
                        form16.getCertificateNo()
                )

                .lastUpdatedOn(
                        form16.getLastUpdatedOn()
                )

                .employeePan(
                        form16.getEmployeePan()
                )

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

                .employeeAddress(
                        form16.getEmployeeAddress()
                )

                .deductorPan(
                        form16.getDeductorPan()
                )

                .deductorTan(
                        form16.getDeductorTan()
                )

                .citTdsAddress(
                        form16.getCitTdsAddress()
                )

                .assessmentYear(
                        form16.getAssessmentYear()
                )

                .employmentFrom(
                        form16.getEmploymentFrom()
                )

                .employmentTo(
                        form16.getEmploymentTo()
                )

                .optingOutOfTaxation115BAC1A(
                        form16
                                .getOptingOutOfTaxation115BAC1A()
                )

                .active(
                        form16.getActive()
                )

                .build();
    }
}


