package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "form16_section16_deduction",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_section16_deduction_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Section16Deduction {

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
                    name = "fk_form16_section16_deduction_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // 3.
    // Total amount of salary received from current employer
    //
    // AUTOMATIC
    // Source:
    // Form16Exemption.totalSalaryReceivedFromCurrentEmployer
    // =========================================================

    @Column(
            name = "salary_received_from_current_employer",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal salaryReceivedFromCurrentEmployer =
            BigDecimal.ZERO;


    // =========================================================
    // 1(e).
    // Salary received from other employer(s)
    //
    // AUTOMATIC
    // Source:
    // Form16Salary.salaryReceivedFromOtherEmployers
    // =========================================================

    @Column(
            name = "salary_received_from_other_employers",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal salaryReceivedFromOtherEmployers =
            BigDecimal.ZERO;


    // =========================================================
    // 4(a).
    // Standard deduction under section 16(i)
    //
    // FRONTEND INPUT
    // =========================================================

    @Column(
            name = "standard_deduction_section_16_i",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal standardDeductionSection16I =
            BigDecimal.ZERO;


    // =========================================================
    // 4(b).
    // Entertainment allowance under section 16(ii)
    //
    // FRONTEND INPUT
    // =========================================================

    @Column(
            name = "entertainment_allowance_section_16_ii",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal entertainmentAllowanceSection16II =
            BigDecimal.ZERO;


    // =========================================================
    // 4(c).
    // Tax on employment under section 16(iii)
    //
    // FRONTEND INPUT
    // =========================================================

    @Column(
            name = "tax_on_employment_section_16_iii",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal taxOnEmploymentSection16III =
            BigDecimal.ZERO;


    // =========================================================
    // 5.
    // Total deductions under section 16
    //
    // AUTOMATIC
    //
    // 4(a) + 4(b) + 4(c)
    // =========================================================

    @Column(
            name = "total_deductions_section_16",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalDeductionsSection16 =
            BigDecimal.ZERO;


    // =========================================================
    // 6.
    // Income chargeable under the head "Salaries"
    //
    // AUTOMATIC
    //
    // 3 + 1(e) - 5
    // =========================================================

    @Column(
            name = "income_chargeable_under_salaries",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal incomeChargeableUnderSalaries =
            BigDecimal.ZERO;


    // =========================================================
    // 7(a).
    // Income / admissible loss from house property
    //
    // FRONTEND INPUT
    // =========================================================

    @Column(
            name = "income_loss_house_property",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal incomeLossHouseProperty =
            BigDecimal.ZERO;


    // =========================================================
    // 7(b).
    // Income under Other Sources
    //
    // FRONTEND INPUT
    // =========================================================

    @Column(
            name = "income_under_other_sources",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal incomeUnderOtherSources =
            BigDecimal.ZERO;


    // =========================================================
    // 8.
    // Total amount of other income
    //
    // AUTOMATIC
    //
    // 7(a) + 7(b)
    // =========================================================

    @Column(
            name = "total_other_income",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal totalOtherIncome =
            BigDecimal.ZERO;


    // =========================================================
    // 9.
    // Gross Total Income
    //
    // AUTOMATIC
    //
    // 6 + 8
    // =========================================================

    @Column(
            name = "gross_total_income",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal grossTotalIncome =
            BigDecimal.ZERO;
}