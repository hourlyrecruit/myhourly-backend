package com.my_hourly.Form_16A.entity;

import com.my_hourly.form_16.entity.Form16;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "form16_quarter",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_quarter_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Quarter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // FORM 16
    // =========================================================

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "form16_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_form16_quarter_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // Q1
    // =========================================================

    @Column(
            name = "q1_receipt_number",
            length = 8,
            nullable = false,
            unique = true
    )
    private String q1ReceiptNumber;


    @Column(
            name = "q1_amount_paid_credited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q1AmountPaidCredited =
            BigDecimal.ZERO;


    @Column(
            name = "q1_tax_deducted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q1TaxDeducted =
            BigDecimal.ZERO;


    @Column(
            name = "q1_tax_deposited_remitted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q1TaxDepositedRemitted =
            BigDecimal.ZERO;


    // =========================================================
    // Q2
    // =========================================================

    @Column(
            name = "q2_receipt_number",
            length = 8,
            nullable = false,
            unique = true
    )
    private String q2ReceiptNumber;


    @Column(
            name = "q2_amount_paid_credited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q2AmountPaidCredited =
            BigDecimal.ZERO;


    @Column(
            name = "q2_tax_deducted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q2TaxDeducted =
            BigDecimal.ZERO;


    @Column(
            name = "q2_tax_deposited_remitted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q2TaxDepositedRemitted =
            BigDecimal.ZERO;


    // =========================================================
    // Q3
    // =========================================================

    @Column(
            name = "q3_receipt_number",
            length = 8,
            nullable = false,
            unique = true
    )
    private String q3ReceiptNumber;


    @Column(
            name = "q3_amount_paid_credited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q3AmountPaidCredited =
            BigDecimal.ZERO;


    @Column(
            name = "q3_tax_deducted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q3TaxDeducted =
            BigDecimal.ZERO;


    @Column(
            name = "q3_tax_deposited_remitted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q3TaxDepositedRemitted =
            BigDecimal.ZERO;


    // =========================================================
    // Q4
    // =========================================================

    @Column(
            name = "q4_receipt_number",
            length = 8,
            nullable = false,
            unique = true
    )
    private String q4ReceiptNumber;


    @Column(
            name = "q4_amount_paid_credited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q4AmountPaidCredited =
            BigDecimal.ZERO;


    @Column(
            name = "q4_tax_deducted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q4TaxDeducted =
            BigDecimal.ZERO;


    @Column(
            name = "q4_tax_deposited_remitted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal q4TaxDepositedRemitted =
            BigDecimal.ZERO;


    // =========================================================
    // TOTAL QUARTERLY DETAILS
    // =========================================================

    @Column(
            name = "total_amount_paid_credited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalAmountPaidCredited =
            BigDecimal.ZERO;


    @Column(
            name = "total_tax_deducted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalTaxDeducted =
            BigDecimal.ZERO;


    @Column(
            name = "total_tax_deposited_remitted",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalTaxDepositedRemitted =
            BigDecimal.ZERO;


    // =========================================================
    // BOOK ADJUSTMENT
    // =========================================================

    @Column(
            name = "book_adjustment_serial_number"
    )
    private Integer bookAdjustmentSerialNumber;


    @Column(
            name = "book_adjustment_tax_deposited",
            precision = 15,
            scale = 2
    )
    @Builder.Default
    private BigDecimal bookAdjustmentTaxDeposited =
            BigDecimal.ZERO;


    @Column(
            name = "receipt_number_form_24g",
            length = 100
    )
    private String receiptNumberForm24G;


    @Column(
            name = "ddo_serial_number_form_24g",
            length = 100
    )
    private String ddoSerialNumberForm24G;


    @Column(
            name = "date_of_transfer_voucher"
    )
    private LocalDate dateOfTransferVoucher;


    @Column(
            name = "status_of_matching_with_form_24g",
            length = 100
    )
    private String statusOfMatchingWithForm24G;


    // =========================================================
    // TOTAL BOOK ADJUSTMENT
    // =========================================================

    @Column(
            name = "total_book_adjustment_tax_deposited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalBookAdjustmentTaxDeposited =
            BigDecimal.ZERO;
}