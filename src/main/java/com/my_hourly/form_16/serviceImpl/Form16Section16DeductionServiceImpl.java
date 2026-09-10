package com.my_hourly.form_16.service.impl;

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

        Form16 form16 =
                form16Repository.findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        // Check duplicate

        if (deductionRepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Section 16 deduction already exists for Form16 id: "
                            + form16Id
            );
        }


        // Get Form16 Exemption

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // 3.
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
        // 5.
        // Total deductions under Section 16
        //
        // 4(a) + 4(b) + 4(c)
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // 6.
        // Income chargeable under Salaries
        //
        // 3 + 1(e) - 5
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // 8.
        // Total other income
        //
        // 7(a) + 7(b)
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // 9.
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


        Form16Section16Deduction saved =
                deductionRepository.save(deduction);


        return mapToResponse(saved);
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16Section16DeductionResponse getDeductionByForm16Id(
            Long form16Id) {

        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id
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


        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // Get latest Form16 Exemption

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // 3.
        // Automatically get current salary again
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
        // 5.
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // 6.
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // 8.
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // 9.
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


        Form16Section16Deduction updated =
                deductionRepository.save(deduction);


        return mapToResponse(updated);
    }


    // =========================================================
    // AUTO SAVE
    //
    // React calls this automatically.
    //
    // User does NOT click Save.
    // =========================================================

    @Override
    public Form16Section16DeductionResponse autoSaveDeduction(
            Long form16Id,
            Form16Section16DeductionRequest request) {


        Form16Section16Deduction deduction =
                deductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // GET LATEST EXEMPTION
        // =====================================================

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Exemption not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // 3.
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
        // 5.
        // Total deductions
        //
        // 4(a) + 4(b) + 4(c)
        // =====================================================

        BigDecimal totalDeductions =
                standardDeduction
                        .add(entertainmentAllowance)
                        .add(taxOnEmployment);


        // =====================================================
        // 6.
        // Income chargeable under Salaries
        //
        // 3 + 1(e) - 5
        // =====================================================

        BigDecimal incomeChargeableUnderSalaries =
                salaryReceivedFromCurrentEmployer
                        .add(salaryReceivedFromOtherEmployers)
                        .subtract(totalDeductions);


        // =====================================================
        // 8.
        // Total other income
        //
        // 7(a) + 7(b)
        // =====================================================

        BigDecimal totalOtherIncome =
                houseProperty
                        .add(otherSources);


        // =====================================================
        // 9.
        // Gross Total Income
        //
        // 6 + 8
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

            throw new RuntimeException(
                    "Section 16 deduction not found for Form16 id: "
                            + form16Id
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

                // 3
                .salaryReceivedFromCurrentEmployer(
                        safe(
                                deduction
                                        .getSalaryReceivedFromCurrentEmployer()
                        )
                )

                // 1(e)
                .salaryReceivedFromOtherEmployers(
                        safe(
                                deduction
                                        .getSalaryReceivedFromOtherEmployers()
                        )
                )

                // 4(a)
                .standardDeductionSection16I(
                        safe(
                                deduction
                                        .getStandardDeductionSection16I()
                        )
                )

                // 4(b)
                .entertainmentAllowanceSection16II(
                        safe(
                                deduction
                                        .getEntertainmentAllowanceSection16II()
                        )
                )

                // 4(c)
                .taxOnEmploymentSection16III(
                        safe(
                                deduction
                                        .getTaxOnEmploymentSection16III()
                        )
                )

                // 5
                .totalDeductionsSection16(
                        safe(
                                deduction
                                        .getTotalDeductionsSection16()
                        )
                )

                // 6
                .incomeChargeableUnderSalaries(
                        safe(
                                deduction
                                        .getIncomeChargeableUnderSalaries()
                        )
                )

                // 7(a)
                .incomeLossHouseProperty(
                        safe(
                                deduction
                                        .getIncomeLossHouseProperty()
                        )
                )

                // 7(b)
                .incomeUnderOtherSources(
                        safe(
                                deduction
                                        .getIncomeUnderOtherSources()
                        )
                )

                // 8
                .totalOtherIncome(
                        safe(
                                deduction
                                        .getTotalOtherIncome()
                        )
                )

                // 9
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

    private BigDecimal safe(BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}