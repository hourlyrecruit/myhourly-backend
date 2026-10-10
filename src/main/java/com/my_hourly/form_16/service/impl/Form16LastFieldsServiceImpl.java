
        package com.my_hourly.form_16.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;

import com.my_hourly.form_16.dto.Form16LastFieldsRequest;
import com.my_hourly.form_16.dto.Form16LastFieldsResponse;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16LastFields;
import com.my_hourly.form_16.repository.Form16LastFieldsRepository;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.service.Form16LastFieldsService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16LastFieldsServiceImpl
        implements Form16LastFieldsService {

    private final Form16LastFieldsRepository lastFieldsRepository;

    private final Form16Repository form16Repository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16LastFieldsResponse createLastFields(
            Long form16Id,
            Form16LastFieldsRequest request) {

        validateRequest(request);

        // -----------------------------------------------------
        // Find Form16
        // -----------------------------------------------------

        Form16 form16 =
                form16Repository
                        .findById(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 not found with id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // -----------------------------------------------------
        // Duplicate check
        // -----------------------------------------------------

        if (lastFieldsRepository.existsByForm16Id(form16Id)) {

            throw new IllegalArgumentException(
                    "Form16 last fields already exist for Form16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Create entity
        // -----------------------------------------------------

        Form16LastFields entity =
                new Form16LastFields();

        entity.setForm16(form16);


        // -----------------------------------------------------
        // Set input fields
        // -----------------------------------------------------

        setInputValues(
                entity,
                request
        );


        // -----------------------------------------------------
        // Calculate
        // -----------------------------------------------------

        calculateTaxValues(entity);


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Form16LastFields saved =
                lastFieldsRepository.save(entity);


        // -----------------------------------------------------
        // Response
        // -----------------------------------------------------

        return toResponse(saved);
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16LastFieldsResponse getLastFields(
            Long form16Id) {

        Form16LastFields entity =
                lastFieldsRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );

        return toResponse(entity);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16LastFieldsResponse updateLastFields(
            Long form16Id,
            Form16LastFieldsRequest request) {

        validateRequest(request);


        // -----------------------------------------------------
        // Find existing
        // -----------------------------------------------------

        Form16LastFields entity =
                lastFieldsRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // -----------------------------------------------------
        // Update input fields
        // -----------------------------------------------------

        setInputValues(
                entity,
                request
        );


        // -----------------------------------------------------
        // Recalculate
        // -----------------------------------------------------

        calculateTaxValues(entity);


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Form16LastFields updated =
                lastFieldsRepository.save(entity);


        // -----------------------------------------------------
        // Response
        // -----------------------------------------------------

        return toResponse(updated);
    }


    // =========================================================
    // PATCH AUTO SAVE
    // =========================================================

    @Override
    public Form16LastFieldsResponse autoSaveLastFields(
            Long form16Id,
            Form16LastFieldsRequest request) {

        validateRequest(request);


        // -----------------------------------------------------
        // Find existing
        // -----------------------------------------------------

        Form16LastFields entity =
                lastFieldsRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id,
                                        ErrorCode.RESOURCE_NOT_FOUND
                                )
                        );


        // -----------------------------------------------------
        // Update input values
        // -----------------------------------------------------

        setInputValues(
                entity,
                request
        );


        // -----------------------------------------------------
        // Automatic calculation
        // -----------------------------------------------------

        calculateTaxValues(entity);


        // -----------------------------------------------------
        // Save
        // -----------------------------------------------------

        Form16LastFields saved =
                lastFieldsRepository.save(entity);


        return toResponse(saved);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteLastFields(
            Long form16Id) {

        if (!lastFieldsRepository.existsByForm16Id(form16Id)) {

            throw new ResourceNotFoundException(
                    "Form16 last fields not found for Form16 id: "
                            + form16Id,
                    ErrorCode.RESOURCE_NOT_FOUND
            );
        }


        lastFieldsRepository.deleteByForm16Id(
                form16Id
        );
    }


    // =========================================================
    // SET INPUT VALUES
    // =========================================================

    private void setInputValues(
            Form16LastFields entity,
            Form16LastFieldsRequest request) {

        entity.setTaxOnTotalIncome(
                zeroIfNull(
                        request.getTaxOnTotalIncome()
                )
        );


        entity.setRebateUnderSection87A(
                zeroIfNull(
                        request.getRebateUnderSection87A()
                )
        );


        entity.setSurcharge(
                zeroIfNull(
                        request.getSurcharge()
                )
        );


        entity.setHealthAndEducationCess(
                zeroIfNull(
                        request.getHealthAndEducationCess()
                )
        );


        entity.setReliefUnderSection89(
                zeroIfNull(
                        request.getReliefUnderSection89()
                )
        );


        entity.setTaxDeductedAtSourceForm12BAA(
                zeroIfNull(
                        request.getTaxDeductedAtSourceForm12BAA()
                )
        );


        entity.setTaxCollectedAtSourceForm12BAA(
                zeroIfNull(
                        request.getTaxCollectedAtSourceForm12BAA()
                )
        );
    }


    // =========================================================
    // AUTOMATIC CALCULATION
    // =========================================================
    //
    // Tax Payable =
    //
    // Tax on Total Income
    // + Surcharge
    // + Health & Education Cess
    // - Rebate u/s 87A
    //
    // Net Tax Payable =
    //
    // Tax Payable
    // - Relief u/s 89
    // - TDS u/s 12BAA
    // - TCS u/s 12BAA
    //
    // =========================================================

    private void calculateTaxValues(
            Form16LastFields entity) {


        // -----------------------------------------------------
        // TAX PAYABLE
        // -----------------------------------------------------

        BigDecimal taxPayable =
                entity.getTaxOnTotalIncome()
                        .add(entity.getSurcharge())
                        .add(entity.getHealthAndEducationCess())
                        .subtract(entity.getRebateUnderSection87A());


        taxPayable =
                scale(taxPayable);


        entity.setTaxPayable(
                taxPayable
        );


        // -----------------------------------------------------
        // NET TAX PAYABLE
        // -----------------------------------------------------

        BigDecimal netTaxPayable =
                taxPayable
                        .subtract(entity.getReliefUnderSection89())
                        .subtract(
                                entity.getTaxDeductedAtSourceForm12BAA()
                        )
                        .subtract(
                                entity.getTaxCollectedAtSourceForm12BAA()
                        );


        netTaxPayable =
                scale(netTaxPayable);


        entity.setNetTaxPayable(
                netTaxPayable
        );
    }


    // =========================================================
    // ENTITY -> RESPONSE
    // =========================================================

    private Form16LastFieldsResponse toResponse(
            Form16LastFields entity) {

        return Form16LastFieldsResponse.builder()

                .id(
                        entity.getId()
                )

                .form16Id(
                        entity.getForm16().getId()
                )

                // -------------------------------------------------
                // Tax on Total Income
                // -------------------------------------------------

                .taxOnTotalIncome(
                        scale(entity.getTaxOnTotalIncome())
                )

                // -------------------------------------------------
                // Rebate u/s 87A
                // -------------------------------------------------

                .rebateUnderSection87A(
                        scale(entity.getRebateUnderSection87A())
                )

                // -------------------------------------------------
                // Surcharge
                // -------------------------------------------------

                .surcharge(
                        scale(entity.getSurcharge())
                )

                // -------------------------------------------------
                // Health & Education Cess
                // -------------------------------------------------

                .healthAndEducationCess(
                        scale(entity.getHealthAndEducationCess())
                )

                // -------------------------------------------------
                // Tax Payable - Calculated
                // -------------------------------------------------

                .taxPayable(
                        scale(entity.getTaxPayable())
                )

                // -------------------------------------------------
                // Relief u/s 89
                // -------------------------------------------------

                .reliefUnderSection89(
                        scale(entity.getReliefUnderSection89())
                )

                // -------------------------------------------------
                // TDS u/s 12BAA
                // -------------------------------------------------

                .taxDeductedAtSourceForm12BAA(
                        scale(
                                entity
                                        .getTaxDeductedAtSourceForm12BAA()
                        )
                )

                // -------------------------------------------------
                // TCS u/s 12BAA
                // -------------------------------------------------

                .taxCollectedAtSourceForm12BAA(
                        scale(
                                entity
                                        .getTaxCollectedAtSourceForm12BAA()
                        )
                )

                // -------------------------------------------------
                // Net Tax Payable - Calculated
                // -------------------------------------------------

                .netTaxPayable(
                        scale(entity.getNetTaxPayable())
                )

                .build();
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateRequest(
            Form16LastFieldsRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request cannot be null"
            );
        }
    }


    // =========================================================
    // NULL -> ZERO
    // =========================================================

    private BigDecimal zeroIfNull(
            BigDecimal value) {

        if (value == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return scale(value);
    }


    // =========================================================
    // SCALE
    // =========================================================

    private BigDecimal scale(
            BigDecimal value) {

        if (value == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}
