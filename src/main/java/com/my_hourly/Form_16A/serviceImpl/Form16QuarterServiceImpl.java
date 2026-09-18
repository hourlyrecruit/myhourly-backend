package com.my_hourly.Form_16A.serviceImpl;

import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.repository.Form16Repository;

import com.my_hourly.Form_16A.dto.Form16QuarterRequest;
import com.my_hourly.Form_16A.dto.Form16QuarterResponse;
import com.my_hourly.Form_16A.entity.Form16Quarter;
import com.my_hourly.Form_16A.repository.Form16QuarterRepository;
import com.my_hourly.Form_16A.service.Form16QuarterService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class Form16QuarterServiceImpl
        implements Form16QuarterService {


    private final Form16QuarterRepository form16QuarterRepository;

    private final Form16Repository form16Repository;


    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public Form16QuarterResponse createQuarter(
            Long form16Id,
            Form16QuarterRequest request) {


        // --------------------------------------------------------
        // 1. Find Form16
        // --------------------------------------------------------

        Form16 form16 =
                form16Repository.findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        // --------------------------------------------------------
        // 2. Check existing quarter
        // --------------------------------------------------------

        if (form16QuarterRepository
                .findByForm16Id(form16Id)
                .isPresent()) {

            throw new RuntimeException(
                    "Quarter details already exist for Form16 id: "
                            + form16Id
            );
        }


        // --------------------------------------------------------
        // 3. Generate BOOK ADJUSTMENT SERIAL NUMBER
        //
        // Employee-specific
        // --------------------------------------------------------

        Integer bookAdjustmentSerialNumber =
                form16QuarterRepository
                        .getNextBookAdjustmentSerialNumber(
                                form16Id
                        );


        /*
         * First employee:
         *
         * Form16 1 → 1
         *
         * Second employee:
         *
         * Form16 2 → 1
         *
         * Third employee:
         *
         * Form16 3 → 1
         */


        // --------------------------------------------------------
        // 4. Generate Q1-Q4 receipt numbers
        // --------------------------------------------------------

        String q1ReceiptNumber =
                generateQuarterReceiptNumber("Q1");

        String q2ReceiptNumber =
                generateQuarterReceiptNumber("Q2");

        String q3ReceiptNumber =
                generateQuarterReceiptNumber("Q3");

        String q4ReceiptNumber =
                generateQuarterReceiptNumber("Q4");


        // --------------------------------------------------------
        // 5. Generate Form 24G receipt number
        // --------------------------------------------------------

        String receiptNumberForm24G =
                generateForm24GReceiptNumber();


        // --------------------------------------------------------
        // 6. Generate DDO serial number
        // --------------------------------------------------------

        String ddoSerialNumberForm24G =
                generateDDOSerialNumber();


        // --------------------------------------------------------
        // 7. Calculate total amount paid
        // --------------------------------------------------------

        BigDecimal totalAmountPaidCredited =
                add(
                        request.getQ1AmountPaidCredited(),
                        request.getQ2AmountPaidCredited(),
                        request.getQ3AmountPaidCredited(),
                        request.getQ4AmountPaidCredited()
                );


        // --------------------------------------------------------
        // 8. Calculate total tax deducted
        // --------------------------------------------------------

        BigDecimal totalTaxDeducted =
                add(
                        request.getQ1TaxDeducted(),
                        request.getQ2TaxDeducted(),
                        request.getQ3TaxDeducted(),
                        request.getQ4TaxDeducted()
                );


        // --------------------------------------------------------
        // 9. Calculate total tax deposited
        // --------------------------------------------------------

        BigDecimal totalTaxDepositedRemitted =
                add(
                        request.getQ1TaxDepositedRemitted(),
                        request.getQ2TaxDepositedRemitted(),
                        request.getQ3TaxDepositedRemitted(),
                        request.getQ4TaxDepositedRemitted()
                );


        // --------------------------------------------------------
        // 10. Book adjustment total
        // --------------------------------------------------------

        BigDecimal totalBookAdjustmentTaxDeposited =
                request.getBookAdjustmentTaxDeposited() != null
                        ? request.getBookAdjustmentTaxDeposited()
                        : BigDecimal.ZERO;


        // --------------------------------------------------------
        // 11. Build entity
        // --------------------------------------------------------

        Form16Quarter quarter =
                Form16Quarter.builder()

                        .form16(form16)


                        // ==============================
                        // Q1
                        // ==============================

                        .q1ReceiptNumber(
                                q1ReceiptNumber
                        )

                        .q1AmountPaidCredited(
                                request.getQ1AmountPaidCredited()
                        )

                        .q1TaxDeducted(
                                request.getQ1TaxDeducted()
                        )

                        .q1TaxDepositedRemitted(
                                request.getQ1TaxDepositedRemitted()
                        )


                        // ==============================
                        // Q2
                        // ==============================

                        .q2ReceiptNumber(
                                q2ReceiptNumber
                        )

                        .q2AmountPaidCredited(
                                request.getQ2AmountPaidCredited()
                        )

                        .q2TaxDeducted(
                                request.getQ2TaxDeducted()
                        )

                        .q2TaxDepositedRemitted(
                                request.getQ2TaxDepositedRemitted()
                        )


                        // ==============================
                        // Q3
                        // ==============================

                        .q3ReceiptNumber(
                                q3ReceiptNumber
                        )

                        .q3AmountPaidCredited(
                                request.getQ3AmountPaidCredited()
                        )

                        .q3TaxDeducted(
                                request.getQ3TaxDeducted()
                        )

                        .q3TaxDepositedRemitted(
                                request.getQ3TaxDepositedRemitted()
                        )


                        // ==============================
                        // Q4
                        // ==============================

                        .q4ReceiptNumber(
                                q4ReceiptNumber
                        )

                        .q4AmountPaidCredited(
                                request.getQ4AmountPaidCredited()
                        )

                        .q4TaxDeducted(
                                request.getQ4TaxDeducted()
                        )

                        .q4TaxDepositedRemitted(
                                request.getQ4TaxDepositedRemitted()
                        )


                        // ==============================
                        // TOTALS
                        // ==============================

                        .totalAmountPaidCredited(
                                totalAmountPaidCredited
                        )

                        .totalTaxDeducted(
                                totalTaxDeducted
                        )

                        .totalTaxDepositedRemitted(
                                totalTaxDepositedRemitted
                        )


                        // ==============================
                        // BOOK ADJUSTMENT
                        // ==============================

                        /*
                         * SYSTEM GENERATED
                         */
                        .bookAdjustmentSerialNumber(
                                bookAdjustmentSerialNumber
                        )

                        .bookAdjustmentTaxDeposited(
                                request.getBookAdjustmentTaxDeposited()
                        )


                        /*
                         * SYSTEM GENERATED
                         */
                        .receiptNumberForm24G(
                                receiptNumberForm24G
                        )


                        /*
                         * SYSTEM GENERATED
                         */
                        .ddoSerialNumberForm24G(
                                ddoSerialNumberForm24G
                        )


                        /*
                         * MANUAL
                         */
                        .dateOfTransferVoucher(
                                request.getDateOfTransferVoucher()
                        )


                        .statusOfMatchingWithForm24G(
                                request.getStatusOfMatchingWithForm24G()
                        )


                        .totalBookAdjustmentTaxDeposited(
                                totalBookAdjustmentTaxDeposited
                        )


                        .build();


        // --------------------------------------------------------
        // 12. Save
        // --------------------------------------------------------

        Form16Quarter saved =
                form16QuarterRepository.save(
                        quarter
                );


        // --------------------------------------------------------
        // 13. Response
        // --------------------------------------------------------

        return mapToResponse(saved);
    }


    // ============================================================
    // GET
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Form16QuarterResponse getQuarterByForm16Id(
            Long form16Id) {


        Form16Quarter quarter =
                form16QuarterRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quarter details not found for Form16 id: "
                                                + form16Id
                                )
                        );


        return mapToResponse(quarter);
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public Form16QuarterResponse updateQuarter(
            Long form16Id,
            Form16QuarterRequest request) {


        Form16Quarter quarter =
                form16QuarterRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quarter details not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // --------------------------------------------------------
        // Calculate totals
        // --------------------------------------------------------

        BigDecimal totalAmountPaidCredited =
                add(
                        request.getQ1AmountPaidCredited(),
                        request.getQ2AmountPaidCredited(),
                        request.getQ3AmountPaidCredited(),
                        request.getQ4AmountPaidCredited()
                );


        BigDecimal totalTaxDeducted =
                add(
                        request.getQ1TaxDeducted(),
                        request.getQ2TaxDeducted(),
                        request.getQ3TaxDeducted(),
                        request.getQ4TaxDeducted()
                );


        BigDecimal totalTaxDepositedRemitted =
                add(
                        request.getQ1TaxDepositedRemitted(),
                        request.getQ2TaxDepositedRemitted(),
                        request.getQ3TaxDepositedRemitted(),
                        request.getQ4TaxDepositedRemitted()
                );


        BigDecimal totalBookAdjustmentTaxDeposited =
                request.getBookAdjustmentTaxDeposited() != null
                        ? request.getBookAdjustmentTaxDeposited()
                        : BigDecimal.ZERO;


        // --------------------------------------------------------
        // Q1
        // --------------------------------------------------------

        quarter.setQ1AmountPaidCredited(
                request.getQ1AmountPaidCredited()
        );

        quarter.setQ1TaxDeducted(
                request.getQ1TaxDeducted()
        );

        quarter.setQ1TaxDepositedRemitted(
                request.getQ1TaxDepositedRemitted()
        );


        // --------------------------------------------------------
        // Q2
        // --------------------------------------------------------

        quarter.setQ2AmountPaidCredited(
                request.getQ2AmountPaidCredited()
        );

        quarter.setQ2TaxDeducted(
                request.getQ2TaxDeducted()
        );

        quarter.setQ2TaxDepositedRemitted(
                request.getQ2TaxDepositedRemitted()
        );


        // --------------------------------------------------------
        // Q3
        // --------------------------------------------------------

        quarter.setQ3AmountPaidCredited(
                request.getQ3AmountPaidCredited()
        );

        quarter.setQ3TaxDeducted(
                request.getQ3TaxDeducted()
        );

        quarter.setQ3TaxDepositedRemitted(
                request.getQ3TaxDepositedRemitted()
        );


        // --------------------------------------------------------
        // Q4
        // --------------------------------------------------------

        quarter.setQ4AmountPaidCredited(
                request.getQ4AmountPaidCredited()
        );

        quarter.setQ4TaxDeducted(
                request.getQ4TaxDeducted()
        );

        quarter.setQ4TaxDepositedRemitted(
                request.getQ4TaxDepositedRemitted()
        );


        // --------------------------------------------------------
        // Totals
        // --------------------------------------------------------

        quarter.setTotalAmountPaidCredited(
                totalAmountPaidCredited
        );

        quarter.setTotalTaxDeducted(
                totalTaxDeducted
        );

        quarter.setTotalTaxDepositedRemitted(
                totalTaxDepositedRemitted
        );


        // --------------------------------------------------------
        // BOOK ADJUSTMENT
        // --------------------------------------------------------

        /*
         * DO NOT CHANGE:
         *
         * bookAdjustmentSerialNumber
         *
         * receiptNumberForm24G
         *
         * ddoSerialNumberForm24G
         *
         * They remain the original generated values.
         */


        quarter.setBookAdjustmentTaxDeposited(
                request.getBookAdjustmentTaxDeposited()
        );


        /*
         * MANUAL DATE
         */
        quarter.setDateOfTransferVoucher(
                request.getDateOfTransferVoucher()
        );


        quarter.setStatusOfMatchingWithForm24G(
                request.getStatusOfMatchingWithForm24G()
        );


        quarter.setTotalBookAdjustmentTaxDeposited(
                totalBookAdjustmentTaxDeposited
        );


        // --------------------------------------------------------
        // Save
        // --------------------------------------------------------

        Form16Quarter updated =
                form16QuarterRepository.save(
                        quarter
                );


        return mapToResponse(updated);
    }


    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void deleteQuarter(
            Long form16Id) {


        Form16Quarter quarter =
                form16QuarterRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quarter details not found for Form16 id: "
                                                + form16Id
                                )
                        );


        form16QuarterRepository.delete(
                quarter
        );
    }


    // ============================================================
    // Q1-Q4 RECEIPT NUMBER
    // ============================================================

    private String generateQuarterReceiptNumber(
            String quarter) {

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        return quarter + random;
    }


    // ============================================================
    // FORM 24G RECEIPT NUMBER
    // ============================================================

    private String generateForm24GReceiptNumber() {

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        return "FORM24G" + random;
    }


    // ============================================================
    // DDO SERIAL NUMBER
    // ============================================================

    private String generateDDOSerialNumber() {

        String random =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 6)
                        .toUpperCase();

        return "DDO" + random;
    }


    // ============================================================
    // BIG DECIMAL ADD
    // ============================================================

    private BigDecimal add(
            BigDecimal... values) {

        BigDecimal result =
                BigDecimal.ZERO;


        if (values == null) {
            return result;
        }


        for (BigDecimal value : values) {

            if (value != null) {

                result =
                        result.add(value);
            }
        }


        return result;
    }


    // ============================================================
    // MAP RESPONSE
    // ============================================================

    private Form16QuarterResponse mapToResponse(
            Form16Quarter quarter) {


        return Form16QuarterResponse.builder()

                .id(
                        quarter.getId()
                )


                // ==============================
                // Q1
                // ==============================

                .q1ReceiptNumber(
                        quarter.getQ1ReceiptNumber()
                )

                .q1AmountPaidCredited(
                        quarter.getQ1AmountPaidCredited()
                )

                .q1TaxDeducted(
                        quarter.getQ1TaxDeducted()
                )

                .q1TaxDepositedRemitted(
                        quarter.getQ1TaxDepositedRemitted()
                )


                // ==============================
                // Q2
                // ==============================

                .q2ReceiptNumber(
                        quarter.getQ2ReceiptNumber()
                )

                .q2AmountPaidCredited(
                        quarter.getQ2AmountPaidCredited()
                )

                .q2TaxDeducted(
                        quarter.getQ2TaxDeducted()
                )

                .q2TaxDepositedRemitted(
                        quarter.getQ2TaxDepositedRemitted()
                )


                // ==============================
                // Q3
                // ==============================

                .q3ReceiptNumber(
                        quarter.getQ3ReceiptNumber()
                )

                .q3AmountPaidCredited(
                        quarter.getQ3AmountPaidCredited()
                )

                .q3TaxDeducted(
                        quarter.getQ3TaxDeducted()
                )

                .q3TaxDepositedRemitted(
                        quarter.getQ3TaxDepositedRemitted()
                )


                // ==============================
                // Q4
                // ==============================

                .q4ReceiptNumber(
                        quarter.getQ4ReceiptNumber()
                )

                .q4AmountPaidCredited(
                        quarter.getQ4AmountPaidCredited()
                )

                .q4TaxDeducted(
                        quarter.getQ4TaxDeducted()
                )

                .q4TaxDepositedRemitted(
                        quarter.getQ4TaxDepositedRemitted()
                )


                // ==============================
                // TOTALS
                // ==============================

                .totalAmountPaidCredited(
                        quarter.getTotalAmountPaidCredited()
                )

                .totalTaxDeducted(
                        quarter.getTotalTaxDeducted()
                )

                .totalTaxDepositedRemitted(
                        quarter.getTotalTaxDepositedRemitted()
                )


                // ==============================
                // BOOK ADJUSTMENT
                // ==============================

                .bookAdjustmentSerialNumber(
                        quarter.getBookAdjustmentSerialNumber()
                )

                .bookAdjustmentTaxDeposited(
                        quarter.getBookAdjustmentTaxDeposited()
                )

                .receiptNumberForm24G(
                        quarter.getReceiptNumberForm24G()
                )

                .ddoSerialNumberForm24G(
                        quarter.getDdoSerialNumberForm24G()
                )

                .dateOfTransferVoucher(
                        quarter.getDateOfTransferVoucher()
                )

                .statusOfMatchingWithForm24G(
                        quarter.getStatusOfMatchingWithForm24G()
                )

                .totalBookAdjustmentTaxDeposited(
                        quarter.getTotalBookAdjustmentTaxDeposited()
                )


                .build();
    }
}