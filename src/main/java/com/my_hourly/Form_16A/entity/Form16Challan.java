package com.my_hourly.Form_16A.entity;

import com.my_hourly.form_16.entity.Form16;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "form16_challan",
        indexes = {
                @Index(
                        name = "idx_form16_challan_form16",
                        columnList = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Challan {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // FORM 16
    // =========================================================

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "form16_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_form16_challan_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // SL. NO.
    //
    // Automatically generated:
    // 1, 2, 3, 4 ... 12
    // =========================================================

    @Column(
            name = "serial_number",
            nullable = false
    )
    private Integer serialNumber;


    // =========================================================
    // TAX DEPOSITED IN RESPECT OF THE DEDUCTEE
    // (Rs.)
    // =========================================================

    @Column(
            name = "tax_deposited",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxDeposited =
            BigDecimal.ZERO;


    // =========================================================
    // BSR CODE OF THE BANK BRANCH
    // =========================================================

    @Column(
            name = "bsr_code",
            length = 50
    )
    private String bsrCode;


    // =========================================================
    // DATE ON WHICH TAX DEPOSITED
    // =========================================================

    @Column(
            name = "tax_deposited_date"
    )
    private LocalDate taxDepositedDate;


    // =========================================================
    // CHALLAN SERIAL NUMBER
    // =========================================================

    @Column(
            name = "challan_serial_number",
            length = 100
    )
    private String challanSerialNumber;


    // =========================================================
    // STATUS OF MATCHING WITH OLTAS
    // =========================================================

    @Column(
            name = "status_of_matching_with_oltas",
            length = 20
    )
    private String statusOfMatchingWithOltas;
}