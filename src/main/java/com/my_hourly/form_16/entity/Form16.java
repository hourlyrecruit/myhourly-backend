package com.my_hourly.form_16.entity;

import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "form16",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_form16_employee_assessment_year",
                        columnNames = {
                                "employee_id",
                                "assessment_year"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // =========================================================
    // EMPLOYEE
    // =========================================================

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_form16_employee"
            )
    )
    private Employee employee;


    // =========================================================
    // CERTIFICATE NUMBER
    // AUTOMATIC UNIQUE 8 CHARACTERS
    // =========================================================

    @Column(
            name = "certificate_no",
            nullable = false,
            unique = true,
            length = 8
    )
    private String certificateNo;


    // =========================================================
    // LAST UPDATED ON
    // AUTOMATIC
    // =========================================================

    @Column(
            name = "last_updated_on",
            nullable = false
    )
    private LocalDate lastUpdatedOn;


    // =========================================================
    // EMPLOYER DETAILS
    // SNAPSHOT FROM EMPLOYER MASTER
    // =========================================================

    @Column(
            name = "employer_name",
            length = 200
    )
    private String employerName;

    @Column(
            name = "employer_address",
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
    // EMPLOYEE ADDRESS
    // SAVED ONLY ON FIRST FORM 16
    // =========================================================

    @Column(
            name = "employee_address",
            length = 2000
    )
    private String employeeAddress;


    // =========================================================
    // DEDUCTOR
    // FROM EMPLOYER MASTER
    // =========================================================

    @Column(
            name = "deductor_pan",
            length = 20
    )
    private String deductorPan;

    @Column(
            name = "deductor_tan",
            length = 20
    )
    private String deductorTan;


    // =========================================================
    // EMPLOYEE PAN
    // FROM PAYROLL / EMPLOYEE
    // =========================================================

    @Column(
            name = "employee_pan",
            length = 20
    )
    private String employeePan;


    // =========================================================
    // CIT TDS ADDRESS
    // =========================================================

    @Column(
            name = "cit_tds_address",
            length = 2000
    )
    private String citTdsAddress;


    // =========================================================
    // ASSESSMENT YEAR
    // AUTOMATIC
    // Example: 2027-28
    // =========================================================

    @Column(
            name = "assessment_year",
            nullable = false,
            length = 20
    )
    private String assessmentYear;


    // =========================================================
    // EMPLOYMENT PERIOD
    // AUTOMATIC
    // =========================================================

    @Column(
            name = "employment_from"
    )
    private LocalDate employmentFrom;

    @Column(
            name = "employment_to"
    )
    private LocalDate employmentTo;


    // =========================================================
    // TAX REGIME
    // =========================================================

    @Column(
            name = "opting_out_115bac_1a"
    )
    private Boolean optingOutOfTaxation115BAC1A;


    // =========================================================
    // ACTIVE / INACTIVE
    // =========================================================

    @Column(
            name = "active",
            nullable = false
    )
    @Builder.Default
    private Boolean active = false;
}