package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "form16_salary",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_salary_form16",
                        columnNames = "form16_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Salary {

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
                    name = "fk_form16_salary_form16"
            )
    )
    private Form16 form16;


    // =========================================================
    // 1(a)
    // Salary as per provisions contained in section 17(1)
    // =========================================================

    @Column(
            name = "salary_u_s_17_1",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal salaryUnderSection17_1 =
            BigDecimal.ZERO;


    // =========================================================
    // 1(b)
    // Value of perquisites under section 17(2)
    // =========================================================

    @Column(
            name = "perquisites_u_s_17_2",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal perquisitesUnderSection17_2 =
            BigDecimal.ZERO;


    // =========================================================
    // 1(c)
    // Profits in lieu of salary under section 17(3)
    // =========================================================

    @Column(
            name = "profits_in_lieu_u_s_17_3",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal profitsInLieuOfSalaryUnderSection17_3 =
            BigDecimal.ZERO;


    // =========================================================
    // 1(d)
    // TOTAL
    //
    // Formula:
    // 17(1) + 17(2) + 17(3)
    // =========================================================

    @Column(
            name = "gross_salary",
            precision = 15,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal grossSalary =
            BigDecimal.ZERO;


    // =========================================================
    // 1(e)
    // Reported total amount of salary received
    // from other employer(s)
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
}