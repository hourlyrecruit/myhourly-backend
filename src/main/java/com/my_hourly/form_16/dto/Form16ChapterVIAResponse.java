package com.my_hourly.form_16.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Form16ChapterVIAResponse {

    private Long id;

    private Long form16Id;


    private BigDecimal section80C;

    private BigDecimal section80CCC;

    private BigDecimal section80CCD1;

    // AUTOMATIC
    private BigDecimal totalDeduction80C80CCC80CCD1;


    private BigDecimal section80CCD1B;

    private BigDecimal section80CCD2;

    private BigDecimal section80D;

    private BigDecimal section80CCH;

    private BigDecimal section80CCH2;

    private BigDecimal section80E;

    private BigDecimal amountDeductibleUnderAnyOtherProvisionChapterVIA;

    private BigDecimal section80EEA;

    private BigDecimal section80G;

    private BigDecimal section80GG;

    private BigDecimal section80TTA;

    private BigDecimal section80TTB;

    private BigDecimal otherChapterVIA;


    // AUTOMATIC
    private BigDecimal totalChapterVIA;

    // AUTOMATIC
    private BigDecimal totalTaxableIncome;
}