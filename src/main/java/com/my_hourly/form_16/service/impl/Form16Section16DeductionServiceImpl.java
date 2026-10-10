package com.my_hourly.form_16.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.form_16.dto.Form16Section16DeductionRequest;
import com.my_hourly.form_16.dto.Form16Section16DeductionResponse;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16Exemption;
import com.my_hourly.form_16.entity.Form16Section16Deduction;
import com.my_hourly.form_16.repository.Form16ExemptionRepository;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16Section16DeductionRepository;
import com.my_hourly.form_16.service.Form16Section16DeductionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16Section16DeductionServiceImpl
        implements Form16Section16DeductionService {

    private final Form16Repository form16Repository;

    private final Form16ExemptionRepository form16ExemptionRepository;

    private final Form16Section16DeductionRepository deductionRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16Section16DeductionResponse createDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request) {

        // -----------------------------------------------------
        // Find Form16
        // -----------------------------------------------------

        Form16 form16 =
                form16Repository.findById(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 not found with id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // -----------------------------------------------------
        // Check duplicate
        // -----------------------------------------------------

        if (deductionRepository.existsByForm16Id(form16Id)) {

            throw new IllegalArgumentException(
                    "Section 16 deduction already exists for Form16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Get Form16 Exemption
        // -----------------------------------------------------

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // =====================================================
        // Salary from current employer
        // AUTOMATIC
        // =====================================================

        BigDecimal salaryReceivedFromCurrentEmployer =
                safe(
                        exemption
                                .getTotalSalaryReceivedFromCurrentEmployer()
                );


        // =====================================================
        // INPUT VALUES
        // =====================================================

        BigDecimal salaryReceivedFromOtherEmployers =
                safe(request.getSalaryReceivedFromOtherEmployers());

        BigDecimal standardDeduction =
                safe(request.getStandardDeductionSection16I());

        BigDecimal entertainmentAllowance =
                safe(request.getEntertainmentAllowanceSection16II());

        BigDecimal taxOnEmployment =
                safe(request.getTaxOnEmploymentSection16III());

        BigDecimal houseProperty =
                safe(request.getIncomeLossHouseProperty());

        BigDecimal otherSources =
                safe(request.getIncomeUnderOtherSources());


        // =====================================================
        // Total deductions under Section 16
        //
        // 4(a) + 4(b) + 4(c)
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // Income chargeable under Salaries
        //
        // 3 + 1(e) - 5
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // Total other income
        //
        // 7(a) + 7(b)
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // Gross Total Income
        //
        // 6 + 8
        // =====================================================

        BigDecimal grossTotalIncome =
                incomeChargeableUnderSalaries
                        .add(totalOtherIncome);


        // =====================================================
        // CREATE ENTITY
        // =====================================================

        Form16Section16Deduction deduction =
                Form16Section16Deduction.builder()

                        .form16(form16)

                        .salaryReceivedFromCurrentEmployer(
                                salaryReceivedFromCurrentEmployer
                        )

                        .salaryReceivedFromOtherEmployers(
                                salaryReceivedFromOtherEmployers
                        )

                        .standardDeductionSection16I(
                                standardDeduction
                        )

                        .entertainmentAllowanceSection16II(
                                entertainmentAllowance
                        )

                        .taxOnEmploymentSection16III(
                                taxOnEmployment
                        )

                        .totalDeductionsSection16(
                                totalDeductions
                        )

                        .incomeChargeableUnderSalaries(
                                incomeChargeableUnderSalaries
                        )

                        .incomeLossHouseProperty(
                                houseProperty
                        )

                        .incomeUnderOtherSources(
                                otherSources
                        )

                        .totalOtherIncome(
                                totalOtherIncome
                        )

                        .grossTotalIncome(
                                grossTotalIncome
                        )

                        .build();


        // =====================================================
        // SAVE
        // =====================================================

        Form16Section16Deduction saved =
                deductionRepository.save(deduction);


        return mapToResponse(saved);
    }


    // =========================================================
    // GET BY FORM16 ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Section16DeductionResponse getDeductionByForm16Id(
            Long form16Id) {

        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        return mapToResponse(deduction);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16Section16DeductionResponse updateDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request) {

        // -----------------------------------------------------
        // Find existing deduction
        // -----------------------------------------------------

        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // -----------------------------------------------------
        // Get latest Form16 Exemption
        // -----------------------------------------------------

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // =====================================================
        // Automatically get current salary
        // =====================================================

        BigDecimal salaryReceivedFromCurrentEmployer =
                safe(
                        exemption
                                .getTotalSalaryReceivedFromCurrentEmployer()
                );


        // =====================================================
        // INPUT VALUES
        // =====================================================

        BigDecimal salaryReceivedFromOtherEmployers =
                safe(request.getSalaryReceivedFromOtherEmployers());

        BigDecimal standardDeduction =
                safe(request.getStandardDeductionSection16I());

        BigDecimal entertainmentAllowance =
                safe(request.getEntertainmentAllowanceSection16II());

        BigDecimal taxOnEmployment =
                safe(request.getTaxOnEmploymentSection16III());

        BigDecimal houseProperty =
                safe(request.getIncomeLossHouseProperty());

        BigDecimal otherSources =
                safe(request.getIncomeUnderOtherSources());


        // =====================================================
        // TOTAL DEDUCTIONS
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // INCOME CHARGEABLE UNDER SALARIES
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // TOTAL OTHER INCOME
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // GROSS TOTAL INCOME
        // =====================================================

        BigDecimal grossTotalIncome =
                incomeChargeableUnderSalaries
                        .add(totalOtherIncome);


        // =====================================================
        // UPDATE ENTITY
        // =====================================================

        deduction.setSalaryReceivedFromCurrentEmployer(
                salaryReceivedFromCurrentEmployer
        );

        deduction.setSalaryReceivedFromOtherEmployers(
                salaryReceivedFromOtherEmployers
        );

        deduction.setStandardDeductionSection16I(
                standardDeduction
        );

        deduction.setEntertainmentAllowanceSection16II(
                entertainmentAllowance
        );

        deduction.setTaxOnEmploymentSection16III(
                taxOnEmployment
        );

        deduction.setTotalDeductionsSection16(
                totalDeductions
        );

        deduction.setIncomeChargeableUnderSalaries(
                incomeChargeableUnderSalaries
        );

        deduction.setIncomeLossHouseProperty(
                houseProperty
        );

        deduction.setIncomeUnderOtherSources(
                otherSources
        );

        deduction.setTotalOtherIncome(
                totalOtherIncome
        );

        deduction.setGrossTotalIncome(
                grossTotalIncome
        );


        // =====================================================
        // SAVE UPDATED RECORD
        // =====================================================

        Form16Section16Deduction updated =
                deductionRepository.save(deduction);


        return mapToResponse(updated);
    }


    // =========================================================
    // AUTO SAVE
    // =========================================================

    @Override
    public Form16Section16DeductionResponse autoSaveDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request) {

        // -----------------------------------------------------
        // Find existing deduction
        // -----------------------------------------------------

        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // =====================================================
        // GET LATEST EXEMPTION
        // =====================================================

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // =====================================================
        // AUTOMATIC SALARY
        // =====================================================

        BigDecimal salaryReceivedFromCurrentEmployer =
                safe(
                        exemption
                                .getTotalSalaryReceivedFromCurrentEmployer()
                );


        // =====================================================
        // INPUT VALUES FROM REACT
        // =====================================================

        BigDecimal salaryReceivedFromOtherEmployers =
                safe(request.getSalaryReceivedFromOtherEmployers());

        BigDecimal standardDeduction =
                safe(request.getStandardDeductionSection16I());

        BigDecimal entertainmentAllowance =
                safe(request.getEntertainmentAllowanceSection16II());

        BigDecimal taxOnEmployment =
                safe(request.getTaxOnEmploymentSection16III());

        BigDecimal houseProperty =
                safe(request.getIncomeLossHouseProperty());

        BigDecimal otherSources =
                safe(request.getIncomeUnderOtherSources());


        // =====================================================
        // TOTAL DEDUCTIONS
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // INCOME CHARGEABLE UNDER SALARIES
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // TOTAL OTHER INCOME
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // GROSS TOTAL INCOME
        // =====================================================

        BigDecimal grossTotalIncome =
                incomeChargeableUnderSalaries
                        .add(totalOtherIncome);


        // =====================================================
        // UPDATE DATABASE
        // =====================================================

        deduction.setSalaryReceivedFromCurrentEmployer(
                salaryReceivedFromCurrentEmployer
        );

        deduction.setSalaryReceivedFromOtherEmployers(
                salaryReceivedFromOtherEmployers
        );

        deduction.setStandardDeductionSection16I(
                standardDeduction
        );

        deduction.setEntertainmentAllowanceSection16II(
                entertainmentAllowance
        );

        deduction.setTaxOnEmploymentSection16III(
                taxOnEmployment
        );

        deduction.setTotalDeductionsSection16(
                totalDeductions
        );

        deduction.setIncomeChargeableUnderSalaries(
                incomeChargeableUnderSalaries
        );

        deduction.setIncomeLossHouseProperty(
                houseProperty
        );

        deduction.setIncomeUnderOtherSources(
                otherSources
        );

        deduction.setTotalOtherIncome(
                totalOtherIncome
        );

        deduction.setGrossTotalIncome(
                grossTotalIncome
        );


        // =====================================================
        // SAVE AUTOMATICALLY
        // =====================================================

        Form16Section16Deduction saved =
                deductionRepository.save(deduction);


        return mapToResponse(saved);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteDeduction(
            Long form16Id) {

        if (!deductionRepository.existsByForm16Id(form16Id)) {

            throw new ResourceNotFoundException(
                    "Section 16 deduction not found for Form16 id: "
                            + form16Id,
                    ErrorCode.RESOURCE_NOT_FOUND
            );
        }


        deductionRepository.deleteByForm16Id(form16Id);
    }


    // =========================================================
    // RESPONSE MAPPER
    // =========================================================

    private Form16Section16DeductionResponse mapToResponse(
            Form16Section16Deduction deduction) {

        return Form16Section16DeductionResponse.builder()

                .id(
                        deduction.getId()
                )

                .form16Id(
                        deduction.getForm16().getId()
                )

                .salaryReceivedFromCurrentEmployer(
                        safe(
                                deduction
                                        .getSalaryReceivedFromCurrentEmployer()
                        )
                )

                .salaryReceivedFromOtherEmployers(
                        safe(
                                deduction
                                        .getSalaryReceivedFromOtherEmployers()
                        )
                )

                .standardDeductionSection16I(
                        safe(
                                deduction
                                        .getStandardDeductionSection16I()
                        )
                )

                .entertainmentAllowanceSection16II(
                        safe(
                                deduction
                                        .getEntertainmentAllowanceSection16II()
                        )
                )

                .taxOnEmploymentSection16III(
                        safe(
                                deduction
                                        .getTaxOnEmploymentSection16III()
                        )
                )

                .totalDeductionsSection16(
                        safe(
                                deduction
                                        .getTotalDeductionsSection16()
                        )
                )

                .incomeChargeableUnderSalaries(
                        safe(
                                deduction
                                        .getIncomeChargeableUnderSalaries()
                        )
                )

                .incomeLossHouseProperty(
                        safe(
                                deduction
                                        .getIncomeLossHouseProperty()
                        )
                )

                .incomeUnderOtherSources(
                        safe(
                                deduction
                                        .getIncomeUnderOtherSources()
                        )
                )

                .totalOtherIncome(
                        safe(
                                deduction
                                        .getTotalOtherIncome()
                        )
                )

                .grossTotalIncome(
                        safe(
                                deduction
                                        .getGrossTotalIncome()
                        )
                )

                .build();
    }


    // =========================================================
    // NULL HANDLER
    // =========================================================

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}
