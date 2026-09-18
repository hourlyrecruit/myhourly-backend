package com.my_hourly.form_16.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Form16Request {

    private String employeeAddress;

    private String assessmentYear;

    private LocalDate employmentFrom;

    private LocalDate employmentTo;

    private Boolean optingOutOfTaxation115BAC1A;
}