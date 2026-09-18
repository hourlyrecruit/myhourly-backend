package com.my_hourly.form_16.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16EmployerMasterRequest {

    private String employerName;

    private String employerAddress;

    private String employerPhone;

    private String employerEmail;

    private String deductorPan;

    private String deductorTan;

    private String citTdsAddress;
}