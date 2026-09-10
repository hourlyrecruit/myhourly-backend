package com.my_hourly.form_16.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16Response {

    private Long id;

    private Long employeeId;

    private String employeeCode;

    private String employeeName;

    private String designation;

    private LocalDate dateOfJoining;

    private String certificateNo;

    private LocalDate lastUpdatedOn;

    private String employeePan;

    private String employerName;

    private String employerAddress;

    private String employerPhone;

    private String employerEmail;

    private String employeeAddress;

    private String deductorPan;

    private String deductorTan;

    private String citTdsAddress;

    private String assessmentYear;

    private LocalDate employmentFrom;

    private LocalDate employmentTo;

    private Boolean optingOutOfTaxation115BAC1A;

    private Boolean active;
}