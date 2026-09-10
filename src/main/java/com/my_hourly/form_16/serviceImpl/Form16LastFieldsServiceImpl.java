package com.my_hourly.form_16.service.impl;

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
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        // -----------------------------------------------------
        // Duplicate check
        // -----------------------------------------------------

        if (lastFieldsRepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
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
                                new RuntimeException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id
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
                                new RuntimeException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id
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
                                new RuntimeException(
                                        "Form16 last fields not found for Form16 id: "
                                                + form16Id
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

            throw new RuntimeException(
                    "Form16 last fields not found for Form16 id: "
                            + form16Id
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
    // 17 = 13 + 15 + 16 - 14
    //
    // 21 = 17 - 18 - 19 - 20
    // =========================================================

    private void calculateTaxValues(
            Form16LastFields entity) {


        // -----------------------------------------------------
        // 17. TAX PAYABLE
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
        // 21. NET TAX PAYABLE
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
                // 13
                // -------------------------------------------------

                .taxOnTotalIncome(
                        scale(entity.getTaxOnTotalIncome())
                )

                // -------------------------------------------------
                // 14
                // -------------------------------------------------

                .rebateUnderSection87A(
                        scale(entity.getRebateUnderSection87A())
                )

                // -------------------------------------------------
                // 15
                // -------------------------------------------------

                .surcharge(
                        scale(entity.getSurcharge())
                )

                // -------------------------------------------------
                // 16
                // -------------------------------------------------

                .healthAndEducationCess(
                        scale(entity.getHealthAndEducationCess())
                )

                // -------------------------------------------------
                // 17 - CALCULATED
                // -------------------------------------------------

                .taxPayable(
                        scale(entity.getTaxPayable())
                )

                // -------------------------------------------------
                // 18
                // -------------------------------------------------

                .reliefUnderSection89(
                        scale(entity.getReliefUnderSection89())
                )

                // -------------------------------------------------
                // 19
                // -------------------------------------------------

                .taxDeductedAtSourceForm12BAA(
                        scale(
                                entity
                                        .getTaxDeductedAtSourceForm12BAA()
                        )
                )

                // -------------------------------------------------
                // 20
                // -------------------------------------------------

                .taxCollectedAtSourceForm12BAA(
                        scale(
                                entity
                                        .getTaxCollectedAtSourceForm12BAA()
                        )
                )

                // -------------------------------------------------
                // 21 - CALCULATED
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