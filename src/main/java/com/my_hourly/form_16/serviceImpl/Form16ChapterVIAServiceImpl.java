package com.my_hourly.form_16.service.impl;

import com.my_hourly.form_16.dto.Form16ChapterVIARequest;
import com.my_hourly.form_16.dto.Form16ChapterVIAResponse;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16ChapterVIA;
import com.my_hourly.form_16.entity.Form16Section16Deduction;
import com.my_hourly.form_16.repository.Form16ChapterVIARepository;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16Section16DeductionRepository;
import com.my_hourly.form_16.service.Form16ChapterVIAService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16ChapterVIAServiceImpl
        implements Form16ChapterVIAService {


    private final Form16Repository form16Repository;

    private final Form16ChapterVIARepository chapterVIARepository;

    private final Form16Section16DeductionRepository section16DeductionRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16ChapterVIAResponse createChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request) {


        Form16 form16 =
                form16Repository.findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with id: "
                                                + form16Id
                                )
                        );


        if (chapterVIARepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Chapter VI-A already exists for Form16 id: "
                            + form16Id
            );
        }


        Form16ChapterVIA entity =
                new Form16ChapterVIA();

        entity.setForm16(form16);

        updateInputFields(entity, request);

        calculate(entity, form16Id);

        Form16ChapterVIA saved =
                chapterVIARepository.save(entity);


        return mapToResponse(saved);
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16ChapterVIAResponse getChapterVIAByForm16Id(
            Long form16Id) {


        Form16ChapterVIA entity =
                chapterVIARepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Chapter VI-A not found for Form16 id: "
                                                + form16Id
                                )
                        );


        return mapToResponse(entity);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16ChapterVIAResponse updateChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request) {


        Form16ChapterVIA entity =
                chapterVIARepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Chapter VI-A not found for Form16 id: "
                                                + form16Id
                                )
                        );


        updateInputFields(entity, request);

        calculate(entity, form16Id);


        Form16ChapterVIA updated =
                chapterVIARepository.save(entity);


        return mapToResponse(updated);
    }


    // =========================================================
    // AUTO SAVE
    //
    // React can call PATCH while user is typing.
    // =========================================================

    @Override
    public Form16ChapterVIAResponse autoSaveChapterVIA(
            Long form16Id,
            Form16ChapterVIARequest request) {


        Form16ChapterVIA entity =
                chapterVIARepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Chapter VI-A not found for Form16 id: "
                                                + form16Id
                                )
                        );


        updateInputFields(entity, request);

        calculate(entity, form16Id);


        Form16ChapterVIA saved =
                chapterVIARepository.save(entity);


        return mapToResponse(saved);
    }


    // =========================================================
    // SET USER INPUT FIELDS
    // =========================================================

    private void updateInputFields(
            Form16ChapterVIA entity,
            Form16ChapterVIARequest request) {


        entity.setSection80C(
                safe(request.getSection80C())
        );

        entity.setSection80CCC(
                safe(request.getSection80CCC())
        );

        entity.setSection80CCD1(
                safe(request.getSection80CCD1())
        );

        entity.setSection80CCD1B(
                safe(request.getSection80CCD1B())
        );

        entity.setSection80CCD2(
                safe(request.getSection80CCD2())
        );

        entity.setSection80D(
                safe(request.getSection80D())
        );

        entity.setSection80CCH(
                safe(request.getSection80CCH())
        );

        entity.setSection80CCH2(
                safe(request.getSection80CCH2())
        );

        entity.setSection80E(
                safe(request.getSection80E())
        );

        entity.setAmountDeductibleUnderAnyOtherProvisionChapterVIA(
                safe(
                        request
                                .getAmountDeductibleUnderAnyOtherProvisionChapterVIA()
                )
        );

        entity.setSection80EEA(
                safe(request.getSection80EEA())
        );

        entity.setSection80G(
                safe(request.getSection80G())
        );

        entity.setSection80GG(
                safe(request.getSection80GG())
        );

        entity.setSection80TTA(
                safe(request.getSection80TTA())
        );

        entity.setSection80TTB(
                safe(request.getSection80TTB())
        );

        entity.setOtherChapterVIA(
                safe(request.getOtherChapterVIA())
        );
    }


    // =========================================================
    // AUTOMATIC CALCULATION
    // =========================================================

    private void calculate(
            Form16ChapterVIA entity,
            Long form16Id) {


        // =====================================================
        // 10(d)
        // 80C + 80CCC + 80CCD(1)
        // =====================================================

        BigDecimal total80CGroup =
                safe(entity.getSection80C())
                        .add(safe(entity.getSection80CCC()))
                        .add(safe(entity.getSection80CCD1()));


        entity.setTotalDeduction80C80CCC80CCD1(
                total80CGroup
        );


        // =====================================================
        // TOTAL CHAPTER VI-A
        // =====================================================

        BigDecimal totalChapterVIA =
                total80CGroup

                        .add(safe(entity.getSection80CCD1B()))

                        .add(safe(entity.getSection80CCD2()))

                        .add(safe(entity.getSection80D()))

                        .add(safe(entity.getSection80CCH()))

                        .add(safe(entity.getSection80CCH2()))

                        .add(safe(entity.getSection80E()))

                        .add(
                                safe(
                                        entity
                                                .getAmountDeductibleUnderAnyOtherProvisionChapterVIA()
                                )
                        )

                        .add(safe(entity.getSection80EEA()))

                        .add(safe(entity.getSection80G()))

                        .add(safe(entity.getSection80GG()))

                        .add(safe(entity.getSection80TTA()))

                        .add(safe(entity.getSection80TTB()))

                        .add(safe(entity.getOtherChapterVIA()));


        entity.setTotalChapterVIA(
                totalChapterVIA
        );


        // =====================================================
        // GROSS TOTAL INCOME
        //
        // From Section 16
        // =====================================================

        Form16Section16Deduction section16 =
                section16DeductionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Section 16 deduction not found for Form16 id: "
                                                + form16Id
                                )
                        );


        BigDecimal grossTotalIncome =
                safe(
                        section16
                                .getGrossTotalIncome()
                );


        // =====================================================
        // 12.
        // TOTAL TAXABLE INCOME
        //
        // Gross Total Income - Total Chapter VI-A
        // =====================================================

        BigDecimal totalTaxableIncome =
                grossTotalIncome
                        .subtract(totalChapterVIA);


        // Prevent negative taxable income

        if (totalTaxableIncome.compareTo(BigDecimal.ZERO) < 0) {

            totalTaxableIncome =
                    BigDecimal.ZERO;
        }


        entity.setTotalTaxableIncome(
                totalTaxableIncome
        );
    }


    // =========================================================
    // RESPONSE
    // =========================================================

    private Form16ChapterVIAResponse mapToResponse(
            Form16ChapterVIA entity) {


        return Form16ChapterVIAResponse.builder()

                .id(entity.getId())

                .form16Id(
                        entity.getForm16().getId()
                )


                .section80C(
                        safe(entity.getSection80C())
                )

                .section80CCC(
                        safe(entity.getSection80CCC())
                )

                .section80CCD1(
                        safe(entity.getSection80CCD1())
                )


                // AUTOMATIC
                .totalDeduction80C80CCC80CCD1(
                        safe(
                                entity
                                        .getTotalDeduction80C80CCC80CCD1()
                        )
                )


                .section80CCD1B(
                        safe(entity.getSection80CCD1B())
                )

                .section80CCD2(
                        safe(entity.getSection80CCD2())
                )

                .section80D(
                        safe(entity.getSection80D())
                )

                .section80CCH(
                        safe(entity.getSection80CCH())
                )

                .section80CCH2(
                        safe(entity.getSection80CCH2())
                )

                .section80E(
                        safe(entity.getSection80E())
                )

                .amountDeductibleUnderAnyOtherProvisionChapterVIA(
                        safe(
                                entity
                                        .getAmountDeductibleUnderAnyOtherProvisionChapterVIA()
                        )
                )

                .section80EEA(
                        safe(entity.getSection80EEA())
                )

                .section80G(
                        safe(entity.getSection80G())
                )

                .section80GG(
                        safe(entity.getSection80GG())
                )

                .section80TTA(
                        safe(entity.getSection80TTA())
                )

                .section80TTB(
                        safe(entity.getSection80TTB())
                )

                .otherChapterVIA(
                        safe(entity.getOtherChapterVIA())
                )


                // AUTOMATIC
                .totalChapterVIA(
                        safe(entity.getTotalChapterVIA())
                )


                // AUTOMATIC
                .totalTaxableIncome(
                        safe(entity.getTotalTaxableIncome())
                )

                .build();
    }


    // =========================================================
    // NULL -> ZERO
    // =========================================================

    private BigDecimal safe(BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteChapterVIA(
            Long form16Id) {


        if (!chapterVIARepository.existsByForm16Id(form16Id)) {

            throw new RuntimeException(
                    "Chapter VI-A not found for Form16 id: "
                            + form16Id
            );
        }


        chapterVIARepository.deleteByForm16Id(
                form16Id
        );
    }
}