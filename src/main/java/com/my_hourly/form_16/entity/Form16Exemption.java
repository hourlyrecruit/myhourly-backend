package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "form16_exemption",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_exemption_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Exemption {

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
                    name = "fk_form16_exemption_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // 2(a)
    // Travel concession or assistance
    // under section 10(5)
    // =========================================================

    @Column(
            name = "section_10_5",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_5 = BigDecimal.ZERO;


    // =========================================================
    // 2(b)
    // Death-cum-retirement gratuity
    // under section 10(10)
    // =========================================================

    @Column(
            name = "section_10_10",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_10 = BigDecimal.ZERO;


    // =========================================================
    // 2(c)
    // Commuted value of pension
    // under section 10(10A)
    // =========================================================

    @Column(
            name = "section_10_10a",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_10A = BigDecimal.ZERO;


    // =========================================================
    // 2(d)
    // Cash equivalent of leave salary encashment
    // under section 10(10AA)
    // =========================================================

    @Column(
            name = "section_10_10aa",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_10AA = BigDecimal.ZERO;


    // =========================================================
    // 2(e)
    // House rent allowance
    // under section 10(13A)
    // =========================================================

    @Column(
            name = "section_10_13a",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_13A = BigDecimal.ZERO;


    // =========================================================
    // 2(f)
    // Amount of any other exemption
    // under section 10
    // =========================================================

    @Column(
            name = "section_10_10b",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal section10_10B = BigDecimal.ZERO;


    // =========================================================
    // 2(h)
    // Other exemption under section 10
    // =========================================================

    @Column(
            name = "other_section_10",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal otherSection10 = BigDecimal.ZERO;


    // =========================================================
    // 2(i)
    //
    // Total amount of exemption claimed under section 10
    //
    // 2(a)
    // + 2(b)
    // + 2(c)
    // + 2(d)
    // + 2(e)
    // + 2(f)
    // + 2(h)
    //
    // AUTOMATIC
    // =========================================================

    @Column(
            name = "total_exemption",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalExemption = BigDecimal.ZERO;


    // =========================================================
    // 3.
    //
    // Total amount of salary received from current employer
    //
    // 1(d) - 2(i)
    //
    // AUTOMATIC
    // =========================================================

    @Column(
            name = "total_salary_received_current_employer",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalSalaryReceivedFromCurrentEmployer =
            BigDecimal.ZERO;
}