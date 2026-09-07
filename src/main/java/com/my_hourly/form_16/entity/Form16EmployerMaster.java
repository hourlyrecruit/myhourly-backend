package com.my_hourly.form_16.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "form16_employer_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16EmployerMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employer_name", nullable = false, length = 200)
    private String employerName;

    @Column(name = "employer_address", nullable = false, length = 2000)
    private String employerAddress;

    @Column(name = "employer_phone", length = 20)
    private String employerPhone;

    @Column(name = "employer_email", length = 150)
    private String employerEmail;

    @Column(name = "deductor_pan", nullable = false, length = 20)
    private String deductorPan;

    @Column(name = "deductor_tan", nullable = false, length = 20)
    private String deductorTan;

    @Column(name = "cit_tds_address", nullable = false, length = 2000)
    private String citTdsAddress;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}