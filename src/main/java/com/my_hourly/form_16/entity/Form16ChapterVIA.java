package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "form16_chapter_via",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_chapter_via_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16ChapterVIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "form16_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_form16_chapter_via_form16"
            )
    )
    private Form16 form16;

    @Column(name = "section_80c", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80C = BigDecimal.ZERO;

    @Column(name = "section_80ccc", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCC = BigDecimal.ZERO;

    @Column(name = "section_80ccd_1", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCD1 = BigDecimal.ZERO;

    // AUTOMATIC
    @Column(
            name = "total_deduction_80c_80ccc_80ccd1",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalDeduction80C80CCC80CCD1 = BigDecimal.ZERO;

    @Column(name = "section_80ccd_1b", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCD1B = BigDecimal.ZERO;

    @Column(name = "section_80ccd_2", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCD2 = BigDecimal.ZERO;

    @Column(name = "section_80d", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80D = BigDecimal.ZERO;

    @Column(name = "section_80cch", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCH = BigDecimal.ZERO;

    @Column(name = "section_80cch2", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80CCH2 = BigDecimal.ZERO;

    @Column(name = "section_80e", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80E = BigDecimal.ZERO;

    @Column(
            name = "amount_deductible_under_any_other_provision_chapter_via",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal amountDeductibleUnderAnyOtherProvisionChapterVIA =
            BigDecimal.ZERO;

    @Column(name = "section_80eea", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80EEA = BigDecimal.ZERO;

    @Column(name = "section_80g", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80G = BigDecimal.ZERO;

    @Column(name = "section_80gg", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80GG = BigDecimal.ZERO;

    @Column(name = "section_80tta", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80TTA = BigDecimal.ZERO;

    @Column(name = "section_80ttb", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal section80TTB = BigDecimal.ZERO;

    @Column(name = "other_chapter_via", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal otherChapterVIA = BigDecimal.ZERO;

    // AUTOMATIC
    @Column(
            name = "total_chapter_via",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalChapterVIA = BigDecimal.ZERO;

    // AUTOMATIC
    @Column(
            name = "total_taxable_income",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalTaxableIncome = BigDecimal.ZERO;
}