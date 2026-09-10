package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "form16_last_fields",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_form16_last_fields_form16",
                columnNames = "form16_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16LastFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "form16_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_form16_last_fields_form16"
            )
    )
    private Form16 form16;

    // =========================================================
    // 13. Tax on total income
    // =========================================================

    @Column(
            name = "tax_on_total_income",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxOnTotalIncome = BigDecimal.ZERO;


    // =========================================================
    // 14. Rebate under Section 87A
    // =========================================================

    @Column(
            name = "rebate_under_section_87a",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal rebateUnderSection87A = BigDecimal.ZERO;


    // =========================================================
    // 15. Surcharge
    // =========================================================

    @Column(
            name = "surcharge",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal surcharge = BigDecimal.ZERO;


    // =========================================================
    // 16. Health and Education Cess
    // =========================================================

    @Column(
            name = "health_and_education_cess",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal healthAndEducationCess = BigDecimal.ZERO;


    // =========================================================
    // 17. Tax Payable
    // CALCULATED
    // 13 + 15 + 16 - 14
    // =========================================================

    @Column(
            name = "tax_payable",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxPayable = BigDecimal.ZERO;


    // =========================================================
    // 18. Relief under Section 89
    // =========================================================

    @Column(
            name = "relief_under_section_89",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal reliefUnderSection89 = BigDecimal.ZERO;


    // =========================================================
    // 19. Tax deducted at source - Form 12BAA
    // =========================================================

    @Column(
            name = "tax_deducted_at_source_form_12baa",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxDeductedAtSourceForm12BAA = BigDecimal.ZERO;


    // =========================================================
    // 20. Tax collected at source - Form 12BAA
    // =========================================================

    @Column(
            name = "tax_collected_at_source_form_12baa",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxCollectedAtSourceForm12BAA = BigDecimal.ZERO;


    // =========================================================
    // 21. Net Tax Payable
    // CALCULATED
    // 17 - 18 - 19 - 20
    // =========================================================

    @Column(
            name = "net_tax_payable",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal netTaxPayable = BigDecimal.ZERO;
}