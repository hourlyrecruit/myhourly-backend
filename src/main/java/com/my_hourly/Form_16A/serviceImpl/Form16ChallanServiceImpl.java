package com.my_hourly.Form_16A.serviceImpl;

import com.my_hourly.Form_16A.dto.Form16ChallanListResponse;
import com.my_hourly.Form_16A.dto.Form16ChallanRequest;
import com.my_hourly.Form_16A.dto.Form16ChallanResponse;
import com.my_hourly.Form_16A.entity.Form16Challan;
import com.my_hourly.Form_16A.repository.Form16ChallanRepository;
import com.my_hourly.Form_16A.service.Form16ChallanService;

import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.repository.Form16Repository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16ChallanServiceImpl
        implements Form16ChallanService {

    private final Form16ChallanRepository challanRepository;

    private final Form16Repository form16Repository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16ChallanResponse createChallan(
            Long form16Id,
            Form16ChallanRequest request) {

        validateForm16Id(form16Id);

        validateRequest(request);

        Form16 form16 =
                form16Repository
                        .findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // CREATE ENTITY
        // =====================================================

        Form16Challan challan =
                new Form16Challan();

        challan.setForm16(form16);


        // =====================================================
        // AUTOMATIC SERIAL NUMBER
        // =====================================================

        Integer nextSerial =
                getNextSerialNumber(form16Id);

        if (nextSerial > 12) {

            throw new RuntimeException(
                    "Maximum 12 challan records are allowed for Form16 id: "
                            + form16Id
            );
        }

        challan.setSerialNumber(
                nextSerial
        );


        // =====================================================
        // USER VALUES
        // =====================================================

        challan.setTaxDeposited(
                zeroIfNull(
                        request.getTaxDeposited()
                )
        );

        challan.setBsrCode(
                request.getBsrCode()
        );

        challan.setTaxDepositedDate(
                request.getTaxDepositedDate()
        );

        challan.setChallanSerialNumber(
                request.getChallanSerialNumber()
        );

        challan.setStatusOfMatchingWithOltas(
                request.getStatusOfMatchingWithOltas()
        );


        // =====================================================
        // SAVE
        // =====================================================

        Form16Challan saved =
                challanRepository.save(
                        challan
                );

        return mapToResponse(saved);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16ChallanListResponse getAllChallans(
            Long form16Id) {

        validateForm16Id(form16Id);

        // Make sure Form16 exists
        form16Repository
                .findById(form16Id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Form16 not found with id: "
                                        + form16Id
                        )
                );


        List<Form16Challan> challans =
                challanRepository
                        .findByForm16IdOrderBySerialNumberAsc(
                                form16Id
                        );


        // =====================================================
        // CALCULATE TOTAL
        // =====================================================

        BigDecimal total =
                challans.stream()
                        .map(
                                challan ->
                                        zeroIfNull(
                                                challan
                                                        .getTaxDeposited()
                                        )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );


        List<Form16ChallanResponse> responses =
                challans.stream()
                        .map(
                                this::mapToResponse
                        )
                        .toList();


        return Form16ChallanListResponse
                .builder()
                .challans(responses)
                .totalTaxDeposited(total)
                .build();
    }


    // =========================================================
    // GET SINGLE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16ChallanResponse getChallan(
            Long form16Id,
            Long challanId) {

        validateForm16Id(form16Id);

        if (challanId == null || challanId <= 0) {

            throw new IllegalArgumentException(
                    "Challan ID must be greater than zero."
            );
        }


        Form16Challan challan =
                challanRepository
                        .findByIdAndForm16Id(
                                challanId,
                                form16Id
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challan not found."
                                )
                        );


        return mapToResponse(challan);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16ChallanResponse updateChallan(
            Long form16Id,
            Long challanId,
            Form16ChallanRequest request) {

        validateForm16Id(form16Id);

        validateRequest(request);


        Form16Challan challan =
                challanRepository
                        .findByIdAndForm16Id(
                                challanId,
                                form16Id
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challan not found."
                                )
                        );


        // =====================================================
        // DO NOT CHANGE SERIAL NUMBER
        // =====================================================

        // serialNumber remains same


        // =====================================================
        // UPDATE USER VALUES
        // =====================================================

        challan.setTaxDeposited(
                zeroIfNull(
                        request.getTaxDeposited()
                )
        );

        challan.setBsrCode(
                request.getBsrCode()
        );

        challan.setTaxDepositedDate(
                request.getTaxDepositedDate()
        );

        challan.setChallanSerialNumber(
                request.getChallanSerialNumber()
        );

        challan.setStatusOfMatchingWithOltas(
                request.getStatusOfMatchingWithOltas()
        );


        Form16Challan updated =
                challanRepository.save(
                        challan
                );


        return mapToResponse(updated);
    }


    // =========================================================
    // DELETE SINGLE
    // =========================================================

    @Override
    public void deleteChallan(
            Long form16Id,
            Long challanId) {

        validateForm16Id(form16Id);

        Form16Challan challan =
                challanRepository
                        .findByIdAndForm16Id(
                                challanId,
                                form16Id
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Challan not found."
                                )
                        );


        challanRepository.delete(
                challan
        );
    }


    // =========================================================
    // DELETE ALL
    // =========================================================

    @Override
    public void deleteAllChallans(
            Long form16Id) {

        validateForm16Id(form16Id);

        challanRepository.deleteByForm16Id(
                form16Id
        );
    }


    // =========================================================
    // NEXT SERIAL NUMBER
    // =========================================================

    private Integer getNextSerialNumber(
            Long form16Id) {

        return challanRepository
                .findTopByForm16IdOrderBySerialNumberDesc(
                        form16Id
                )
                .map(
                        challan ->
                                challan
                                        .getSerialNumber()
                                        + 1
                )
                .orElse(1);
    }


    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private Form16ChallanResponse mapToResponse(
            Form16Challan challan) {

        return Form16ChallanResponse
                .builder()

                .id(
                        challan.getId()
                )

                .serialNumber(
                        challan.getSerialNumber()
                )

                .taxDeposited(
                        challan.getTaxDeposited()
                )

                .bsrCode(
                        challan.getBsrCode()
                )

                .taxDepositedDate(
                        challan.getTaxDepositedDate()
                )

                .challanSerialNumber(
                        challan.getChallanSerialNumber()
                )

                .statusOfMatchingWithOltas(
                        challan
                                .getStatusOfMatchingWithOltas()
                )

                .build();
    }


    // =========================================================
    // VALIDATE REQUEST
    // =========================================================

    private void validateRequest(
            Form16ChallanRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Challan request cannot be null."
            );
        }
    }


    // =========================================================
    // VALIDATE FORM16 ID
    // =========================================================

    private void validateForm16Id(
            Long form16Id) {

        if (form16Id == null || form16Id <= 0) {

            throw new IllegalArgumentException(
                    "Form16 ID must be greater than zero."
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