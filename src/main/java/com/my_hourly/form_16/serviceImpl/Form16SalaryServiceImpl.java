package com.my_hourly.form_16.serviceImpl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16Salary;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16SalaryRepository;
import com.my_hourly.form_16.service.Form16SalaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16SalaryServiceImpl
        implements Form16SalaryService {

    private final Form16SalaryRepository form16SalaryRepository;

    private final Form16Repository form16Repository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16Salary createSalary(
            Long form16Id,
            Form16SalaryRequest request) {

        validateRequest(request);

        // -----------------------------------------------------
        // Find Form16
        // -----------------------------------------------------

        Form16 form16 =
                form16Repository
                        .findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        // -----------------------------------------------------
        // Check duplicate salary record
        // -----------------------------------------------------

        if (form16SalaryRepository
                .existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Salary details already exist for Form16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Create entity
        // -----------------------------------------------------

        Form16Salary salary =
                new Form16Salary();

        salary.setForm16(form16);


        // -----------------------------------------------------
        // Set values
        // -----------------------------------------------------

        setSalaryValues(
                salary,
                request
        );


        // -----------------------------------------------------
        // Calculate Gross Salary
        // -----------------------------------------------------

        calculateGrossSalary(
                salary
        );


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        return form16SalaryRepository.save(
                salary
        );
    }


    // =========================================================
    // GET BY FORM16 ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Salary getSalaryByForm16Id(
            Long form16Id) {

        return form16SalaryRepository
                .findByForm16Id(form16Id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Salary details not found for Form16 id: "
                                        + form16Id
                        )
                );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16Salary updateSalary(
            Long form16Id,
            Form16SalaryRequest request) {

        validateRequest(request);


        // -----------------------------------------------------
        // Find existing salary
        // -----------------------------------------------------

        Form16Salary salary =
                form16SalaryRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Salary details not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // -----------------------------------------------------
        // Update values
        // -----------------------------------------------------

        setSalaryValues(
                salary,
                request
        );


        // -----------------------------------------------------
        // Recalculate Gross Salary
        // -----------------------------------------------------

        calculateGrossSalary(
                salary
        );


        // -----------------------------------------------------
        // Save updated record
        // -----------------------------------------------------

        return form16SalaryRepository.save(
                salary
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteSalary(
            Long form16Id) {

        if (!form16SalaryRepository
                .existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Salary details not found for Form16 id: "
                            + form16Id
            );
        }


        form16SalaryRepository
                .deleteByForm16Id(form16Id);
    }


    // =========================================================
    // SET SALARY VALUES
    // =========================================================

    private void setSalaryValues(
            Form16Salary salary,
            Form16SalaryRequest request) {

        salary.setSalaryUnderSection17_1(
                zeroIfNull(
                        request.getSalaryUnderSection17_1()
                )
        );


        salary.setPerquisitesUnderSection17_2(
                zeroIfNull(
                        request.getPerquisitesUnderSection17_2()
                )
        );


        salary.setProfitsInLieuOfSalaryUnderSection17_3(
                zeroIfNull(
                        request
                                .getProfitsInLieuOfSalaryUnderSection17_3()
                )
        );


        salary.setSalaryReceivedFromOtherEmployers(
                zeroIfNull(
                        request
                                .getSalaryReceivedFromOtherEmployers()
                )
        );
    }


    // =========================================================
    // CALCULATE GROSS SALARY
    // =========================================================
    //
    // Gross Salary =
    //
    // 17(1)
    // + 17(2)
    // + 17(3)
    //
    // =========================================================

    private void calculateGrossSalary(
            Form16Salary salary) {

        BigDecimal grossSalary =
                zeroIfNull(
                        salary.getSalaryUnderSection17_1()
                )
                        .add(
                                zeroIfNull(
                                        salary
                                                .getPerquisitesUnderSection17_2()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        salary
                                                .getProfitsInLieuOfSalaryUnderSection17_3()
                                )
                        );


        salary.setGrossSalary(
                grossSalary
        );
    }


    // =========================================================
    // VALIDATE REQUEST
    // =========================================================

    private void validateRequest(
            Form16SalaryRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Form16 Salary request cannot be null."
            );
        }
    }


    // =========================================================
    // NULL → ZERO
    // =========================================================

    private BigDecimal zeroIfNull(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}