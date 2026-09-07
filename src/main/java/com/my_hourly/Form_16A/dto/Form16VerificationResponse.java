package com.my_hourly.form_16.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16VerificationResponse {

    private Long id;

    private Long form16Id;

    private String place;

    private LocalDate date;

    private String designation;

    private String fullName;

    private String signature;
}