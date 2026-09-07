package com.my_hourly.Form_16A.serviceImpl;

import com.my_hourly.Form_16A.dto.Form16QuarterRequest;
import com.my_hourly.Form_16A.dto.Form16QuarterResponse;
import com.my_hourly.Form_16A.entity.Form16Quarter;
import com.my_hourly.Form_16A.repository.Form16QuarterRepository;
import com.my_hourly.Form_16A.service.Form16QuarterService;

import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.repository.Form16Repository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16QuarterServiceImpl
        implements Form16QuarterService {

    private final Form16QuarterRepository form16QuarterRepository;

    private final Form16Repository form16Repository;

    private static final String RECEIPT_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom secureRandom =
            new SecureRandom();


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16QuarterResponse createQuarter(
            Long form16Id,
            Form16QuarterRequest request) {

        validateForm16Id(form16Id);

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
        // Check duplicate
        // -----------------------------------------------------

        if (form16QuarterRepository
                .existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Quarter details already exist for Form16 id: "
                            + form16Id
            );
        }


        // -----------------------------------------------------
        // Create entity
        // -----------------------------------------------------

        Form16Quarter quarter =
                new Form16Quarter();

        quarter.setForm16(form16);


        // =====================================================
        // AUTOMATIC Q1-Q4 RECEIPT NUMBERS
        // =====================================================

        quarter.setQ1ReceiptNumber(
                generateUniqueReceiptNumber()
        );

        quarter.setQ2ReceiptNumber(
                generateUniqueReceiptNumber()
        );

        quarter.setQ3ReceiptNumber(
                generateUniqueReceiptNumber()
        );

        quarter.setQ4ReceiptNumber(
                generateUniqueReceiptNumber()
        );


        // =====================================================
        // SET QUARTER VALUES
        // =====================================================

        setQuarterValues(
                quarter,
                request
        );


        // =====================================================
        // BOOK ADJUSTMENT
        // =====================================================

        handleBookAdjustment(
                quarter,
                request
        );


        // =====================================================
        // CALCULATE TOTALS
        // =====================================================

        calculateTotals(
                quarter
        );


        // =====================================================
        // SAVE
        // =====================================================

        Form16Quarter saved =
                form16QuarterRepository.save(
                        quarter
                );


        return mapToResponse(saved);
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16QuarterResponse getQuarterByForm16Id(
            Long form16Id) {

        validateForm16Id(form16Id);

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


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16QuarterResponse updateQuarter(
            Long form16Id,
            Form16QuarterRequest request) {

        validateForm16Id(form16Id);

        validateRequest(request);


        // -----------------------------------------------------
        // Find existing record
        // -----------------------------------------------------

        Form16Quarter quarter =
                form16QuarterRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quarter details not found for Form16 id: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // UPDATE Q1-Q4 VALUES
        // =====================================================

        setQuarterValues(
                quarter,
                request
        );


        // =====================================================
        // BOOK ADJUSTMENT
        //
        // Existing automatic values are preserved.
        // =====================================================

        updateBookAdjustment(
                quarter,
                request
        );


        // =====================================================
        // RECALCULATE TOTALS
        // =====================================================

        calculateTotals(
                quarter
        );


        // =====================================================
        // SAVE
        // =====================================================

        Form16Quarter updated =
                form16QuarterRepository.save(
                        quarter
                );


        return mapToResponse(updated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteQuarter(
            Long form16Id) {

        validateForm16Id(form16Id);

        if (!form16QuarterRepository
                .existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Quarter details not found for Form16 id: "
                            + form16Id
            );
        }

        form16QuarterRepository
                .deleteByForm16Id(form16Id);
    }


    // =========================================================
    // SET QUARTER VALUES
    // =========================================================

    private void setQuarterValues(
            Form16Quarter quarter,
            Form16QuarterRequest request) {

        // =====================================================
        // Q1
        // =====================================================

        quarter.setQ1AmountPaidCredited(
                zeroIfNull(
                        request.getQ1AmountPaidCredited()
                )
        );

        quarter.setQ1TaxDeducted(
                zeroIfNull(
                        request.getQ1TaxDeducted()
                )
        );

        quarter.setQ1TaxDepositedRemitted(
                zeroIfNull(
                        request.getQ1TaxDepositedRemitted()
                )
        );


        // =====================================================
        // Q2
        // =====================================================

        quarter.setQ2AmountPaidCredited(
                zeroIfNull(
                        request.getQ2AmountPaidCredited()
                )
        );

        quarter.setQ2TaxDeducted(
                zeroIfNull(
                        request.getQ2TaxDeducted()
                )
        );

        quarter.setQ2TaxDepositedRemitted(
                zeroIfNull(
                        request.getQ2TaxDepositedRemitted()
                )
        );


        // =====================================================
        // Q3
        // =====================================================

        quarter.setQ3AmountPaidCredited(
                zeroIfNull(
                        request.getQ3AmountPaidCredited()
                )
        );

        quarter.setQ3TaxDeducted(
                zeroIfNull(
                        request.getQ3TaxDeducted()
                )
        );

        quarter.setQ3TaxDepositedRemitted(
                zeroIfNull(
                        request.getQ3TaxDepositedRemitted()
                )
        );


        // =====================================================
        // Q4
        // =====================================================

        quarter.setQ4AmountPaidCredited(
                zeroIfNull(
                        request.getQ4AmountPaidCredited()
                )
        );

        quarter.setQ4TaxDeducted(
                zeroIfNull(
                        request.getQ4TaxDeducted()
                )
        );

        quarter.setQ4TaxDepositedRemitted(
                zeroIfNull(
                        request.getQ4TaxDepositedRemitted()
                )
        );
    }


    // =========================================================
    // CREATE BOOK ADJUSTMENT
    // =========================================================

    private void handleBookAdjustment(
            Form16Quarter quarter,
            Form16QuarterRequest request) {

        boolean bookAdjustmentUsed =
                request.getBookAdjustmentTaxDeposited() != null
                        ||
                        request.getStatusOfMatchingWithForm24G() != null
                        ||
                        !isBlank(
                                request.getStatusOfMatchingWithForm24G()
                        );


        if (!bookAdjustmentUsed) {

            quarter.setBookAdjustmentSerialNumber(null);

            quarter.setBookAdjustmentTaxDeposited(null);

            quarter.setReceiptNumberForm24G("");

            quarter.setDdoSerialNumberForm24G("");

            quarter.setDateOfTransferVoucher(null);

            quarter.setStatusOfMatchingWithForm24G("");

            quarter.setTotalBookAdjustmentTaxDeposited(null);

            return;
        }


        // =====================================================
        // AUTOMATIC SERIAL NUMBER
        // =====================================================

        Integer serialNumber =
                generateBookAdjustmentSerialNumber();

        quarter.setBookAdjustmentSerialNumber(
                serialNumber
        );


        // =====================================================
        // AUTOMATIC FORM 24G RECEIPT NUMBER
        // =====================================================

        quarter.setReceiptNumberForm24G(
                String.format(
                        "FORM24G%03d",
                        serialNumber
                )
        );


        // =====================================================
        // AUTOMATIC DDO SERIAL NUMBER
        // =====================================================

        quarter.setDdoSerialNumberForm24G(
                String.format(
                        "DDO%03d",
                        serialNumber
                )
        );


        // =====================================================
        // AUTOMATIC TRANSFER DATE
        // =====================================================

        quarter.setDateOfTransferVoucher(
                LocalDate.now()
        );


        // =====================================================
        // USER PROVIDED VALUE
        // =====================================================

        quarter.setBookAdjustmentTaxDeposited(
                request.getBookAdjustmentTaxDeposited()
        );

        quarter.setStatusOfMatchingWithForm24G(
                request.getStatusOfMatchingWithForm24G()
        );
    }


    // =========================================================
    // UPDATE BOOK ADJUSTMENT
    // =========================================================

    private void updateBookAdjustment(
            Form16Quarter quarter,
            Form16QuarterRequest request) {

        boolean bookAdjustmentUsed =
                request.getBookAdjustmentTaxDeposited() != null
                        ||
                        !isBlank(
                                request.getStatusOfMatchingWithForm24G()
                        );


        if (!bookAdjustmentUsed) {

            quarter.setBookAdjustmentSerialNumber(null);

            quarter.setBookAdjustmentTaxDeposited(null);

            quarter.setReceiptNumberForm24G("");

            quarter.setDdoSerialNumberForm24G("");

            quarter.setDateOfTransferVoucher(null);

            quarter.setStatusOfMatchingWithForm24G("");

            quarter.setTotalBookAdjustmentTaxDeposited(null);

            return;
        }


        // =====================================================
        // IF FIRST TIME USING BOOK ADJUSTMENT
        // =====================================================

        if (quarter.getBookAdjustmentSerialNumber() == null) {

            Integer serialNumber =
                    generateBookAdjustmentSerialNumber();

            quarter.setBookAdjustmentSerialNumber(
                    serialNumber
            );

            quarter.setReceiptNumberForm24G(
                    String.format(
                            "FORM24G%03d",
                            serialNumber
                    )
            );

            quarter.setDdoSerialNumberForm24G(
                    String.format(
                            "DDO%03d",
                            serialNumber
                    )
            );

            quarter.setDateOfTransferVoucher(
                    LocalDate.now()
            );
        }


        // =====================================================
        // UPDATE ONLY USER VALUES
        // =====================================================

        quarter.setBookAdjustmentTaxDeposited(
                request.getBookAdjustmentTaxDeposited()
        );

        quarter.setStatusOfMatchingWithForm24G(
                request.getStatusOfMatchingWithForm24G()
        );
    }


    // =========================================================
    // CALCULATE TOTALS
    // =========================================================

    private void calculateTotals(
            Form16Quarter quarter) {

        // =====================================================
        // TOTAL AMOUNT PAID / CREDITED
        // =====================================================

        BigDecimal totalAmountPaidCredited =
                zeroIfNull(
                        quarter.getQ1AmountPaidCredited()
                )
                        .add(
                                zeroIfNull(
                                        quarter.getQ2AmountPaidCredited()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ3AmountPaidCredited()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ4AmountPaidCredited()
                                )
                        );

        quarter.setTotalAmountPaidCredited(
                totalAmountPaidCredited
        );


        // =====================================================
        // TOTAL TAX DEDUCTED
        // =====================================================

        BigDecimal totalTaxDeducted =
                zeroIfNull(
                        quarter.getQ1TaxDeducted()
                )
                        .add(
                                zeroIfNull(
                                        quarter.getQ2TaxDeducted()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ3TaxDeducted()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ4TaxDeducted()
                                )
                        );

        quarter.setTotalTaxDeducted(
                totalTaxDeducted
        );


        // =====================================================
        // TOTAL TAX DEPOSITED / REMITTED
        // =====================================================

        BigDecimal totalTaxDepositedRemitted =
                zeroIfNull(
                        quarter.getQ1TaxDepositedRemitted()
                )
                        .add(
                                zeroIfNull(
                                        quarter.getQ2TaxDepositedRemitted()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ3TaxDepositedRemitted()
                                )
                        )
                        .add(
                                zeroIfNull(
                                        quarter.getQ4TaxDepositedRemitted()
                                )
                        );

        quarter.setTotalTaxDepositedRemitted(
                totalTaxDepositedRemitted
        );


        // =====================================================
        // TOTAL BOOK ADJUSTMENT
        // =====================================================

        if (quarter.getBookAdjustmentTaxDeposited() != null) {

            quarter.setTotalBookAdjustmentTaxDeposited(
                    quarter.getBookAdjustmentTaxDeposited()
            );

        } else {

            quarter.setTotalBookAdjustmentTaxDeposited(
                    null
            );
        }
    }


    // =========================================================
    // GENERATE BOOK ADJUSTMENT SERIAL NUMBER
    // =========================================================

    private Integer generateBookAdjustmentSerialNumber() {

        Integer maxSerial =
                form16QuarterRepository
                        .findMaxBookAdjustmentSerialNumber();

        if (maxSerial == null) {
            return 1;
        }

        return maxSerial + 1;
    }


    // =========================================================
    // GENERATE UNIQUE Q1-Q4 RECEIPT NUMBER
    // =========================================================

    private String generateUniqueReceiptNumber() {

        String receiptNumber;

        do {

            StringBuilder builder =
                    new StringBuilder(8);

            for (int i = 0; i < 8; i++) {

                int index =
                        secureRandom.nextInt(
                                RECEIPT_CHARACTERS.length()
                        );

                builder.append(
                        RECEIPT_CHARACTERS.charAt(index)
                );
            }

            receiptNumber =
                    builder.toString();

        } while (
                form16QuarterRepository
                        .existsByQ1ReceiptNumber(
                                receiptNumber
                        )
                        ||
                        form16QuarterRepository
                                .existsByQ2ReceiptNumber(
                                        receiptNumber
                                )
                        ||
                        form16QuarterRepository
                                .existsByQ3ReceiptNumber(
                                        receiptNumber
                                )
                        ||
                        form16QuarterRepository
                                .existsByQ4ReceiptNumber(
                                        receiptNumber
                                )
        );

        return receiptNumber;
    }


    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private Form16QuarterResponse mapToResponse(
            Form16Quarter quarter) {

        return Form16QuarterResponse.builder()

                .id(
                        quarter.getId()
                )

                // =================================================
                // Q1
                // =================================================

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


                // =================================================
                // Q2
                // =================================================

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


                // =================================================
                // Q3
                // =================================================

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


                // =================================================
                // Q4
                // =================================================

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


                // =================================================
                // TOTAL
                // =================================================

                .totalAmountPaidCredited(
                        quarter.getTotalAmountPaidCredited()
                )

                .totalTaxDeducted(
                        quarter.getTotalTaxDeducted()
                )

                .totalTaxDepositedRemitted(
                        quarter.getTotalTaxDepositedRemitted()
                )


                // =================================================
                // BOOK ADJUSTMENT
                // =================================================

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


    // =========================================================
    // VALIDATE REQUEST
    // =========================================================

    private void validateRequest(
            Form16QuarterRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Form16 Quarter request cannot be null."
            );
        }
    }


    // =========================================================
    // VALIDATE FORM16 ID
    // =========================================================

    private void validateForm16Id(
            Long form16Id) {

        if (form16Id == null) {

            throw new IllegalArgumentException(
                    "Form16 ID cannot be null."
            );
        }

        if (form16Id <= 0) {

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


    // =========================================================
    // CHECK BLANK STRING
    // =========================================================

    private boolean isBlank(
            String value) {

        return value == null
                || value.trim().isEmpty();
    }
}