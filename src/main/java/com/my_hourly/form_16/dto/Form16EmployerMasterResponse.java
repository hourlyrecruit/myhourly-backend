package com.my_hourly.form_16.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16EmployerMasterResponse {

    private Long id;

    private String employerName;

    private String employerAddress;

    private String employerPhone;

    private String employerEmail;

    private String deductorPan;

    private String deductorTan;

    private String citTdsAddress;

    // ADD THIS
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}