package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "form16_verification",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_verification_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Verification {

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
                    name = "fk_form16_verification_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // PLACE
    // Example: Bangalore
    // =========================================================

    @Column(
            name = "place",
            length = 200
    )
    private String place;


    // =========================================================
    // DATE
    // Example: 2026-08-28
    // =========================================================

    @Column(
            name = "verification_date"
    )
    private LocalDate date;


    // =========================================================
    // DESIGNATION
    // Example: DIRECTOR
    // =========================================================

    @Column(
            name = "designation",
            length = 200
    )
    private String designation;


    // =========================================================
    // FULL NAME
    // Example: PREETHI VASANTHKUMAR SHAH
    // =========================================================

    @Column(
            name = "full_name",
            length = 300
    )
    private String fullName;


    // =========================================================
    // SIGNATURE
    //
    // TEXT ONLY
    //
    // Example:
    // PREETHI V SHAH
    // Authorized Signatory
    // PREETHI VASANTHKUMAR SHAH
    // =========================================================

    @Column(
            name = "signature",
            length = 500
    )
    private String signature;
}