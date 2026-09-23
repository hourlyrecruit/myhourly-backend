package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "form16_employer_master"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16EmployerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // EMPLOYER DETAILS
    // =========================================================

    @Column(
            name = "employer_name",
            nullable = false,
            length = 200
    )
    private String employerName;


    @Column(
            name = "employer_address",
            nullable = false,
            length = 2000
    )
    private String employerAddress;


    @Column(
            name = "employer_phone",
            length = 20
    )
    private String employerPhone;


    @Column(
            name = "employer_email",
            length = 150
    )
    private String employerEmail;


    // =========================================================
    // DEDUCTOR PAN
    // =========================================================

    @Column(
            name = "deductor_pan",
            nullable = false,
            length = 10
    )
    private String deductorPan;


    // =========================================================
    // DEDUCTOR TAN
    // =========================================================

    @Column(
            name = "deductor_tan",
            nullable = false,
            length = 10
    )
    private String deductorTan;


    // =========================================================
    // CIT TDS ADDRESS
    // =========================================================

    @Column(
            name = "cit_tds_address",
            nullable = false,
            length = 2000
    )
    private String citTdsAddress;


    // =========================================================
    // ACTIVE
    // =========================================================

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = false;


    // =========================================================
    // AUDIT
    // =========================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;
}
