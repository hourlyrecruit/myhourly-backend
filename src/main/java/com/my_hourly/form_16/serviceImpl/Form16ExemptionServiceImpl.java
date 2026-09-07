package com.my_hourly.form_16.serviceImpl;

import com.my_hourly.form_16.dto.Form16ExemptionRequest;
import com.my_hourly.form_16.dto.Form16ExemptionResponse;

import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16Exemption;
import com.my_hourly.form_16.entity.Form16Salary;

import com.my_hourly.form_16.repository.Form16ExemptionRepository;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16SalaryRepository;

import com.my_hourly.form_16.service.Form16ExemptionService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16ExemptionServiceImpl
        implements Form16ExemptionService {


    private final Form16ExemptionRepository
            form16ExemptionRepository;


    private final Form16Repository
            form16Repository;


    private final Form16SalaryRepository
            form16SalaryRepository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16ExemptionResponse createExemption(
            Long form16Id,
            Form16ExemptionRequest request) {


        if (form16Id == null) {

            throw new IllegalArgumentException(
                    "Form16 ID is required."
            );
        }


        if (request == null) {

            throw new IllegalArgumentException(
                    "Exemption request is required."
            );
        }


        // =====================================================
        // FIND FORM16
        // =====================================================

        Form16 form16 =
                form16Repository
                        .findById(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 not found with ID: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // DUPLICATE CHECK
        // =====================================================

        if (
                form16ExemptionRepository
                        .existsByForm16Id(form16Id)
        ) {

            throw new IllegalStateException(
                    "Exemption already exists for Form16 ID: "
                            + form16Id
            );
        }


        // =====================================================
        // GET FORM16 SALARY
        //
        // 1(d) AUTOMATIC
        // =====================================================

        Form16Salary salary =
                form16SalaryRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Salary not found for Form16 ID: "
                                                + form16Id
                                )
                        );


        BigDecimal grossSalary =
                safe(
                        salary.getGrossSalary()
                );


        // =====================================================
        // REQUEST VALUES
        // =====================================================

        BigDecimal section10_5 =
                safe(request.getSection10_5());

        BigDecimal section10_10 =
                safe(request.getSection10_10());

        BigDecimal section10_10A =
                safe(request.getSection10_10A());

        BigDecimal section10_10AA =
                safe(request.getSection10_10AA());

        BigDecimal section10_13A =
                safe(request.getSection10_13A());

        BigDecimal section10_10B =
                safe(request.getSection10_10B());

        BigDecimal otherSection10 =
                safe(request.getOtherSection10());


        // =====================================================
        // 2(i)
        //
        // 2(a)+2(b)+2(c)+2(d)+2(e)+2(f)+2(h)
        // =====================================================

        BigDecimal totalExemption =
                section10_5
                        .add(section10_10)
                        .add(section10_10A)
                        .add(section10_10AA)
                        .add(section10_13A)
                        .add(section10_10B)
                        .add(otherSection10);


        // =====================================================
        // 3
        //
        // 1(d) - 2(i)
        // =====================================================

        BigDecimal totalSalaryReceived =
                grossSalary
                        .subtract(totalExemption);


        // =====================================================
        // PREVENT NEGATIVE
        // =====================================================

        if (
                totalSalaryReceived
                        .compareTo(BigDecimal.ZERO) < 0
        ) {

            totalSalaryReceived =
                    BigDecimal.ZERO;
        }


        // =====================================================
        // CREATE
        // =====================================================

        Form16Exemption exemption =
                Form16Exemption.builder()

                        .form16(form16)

                        .section10_5(
                                section10_5
                        )

                        .section10_10(
                                section10_10
                        )

                        .section10_10A(
                                section10_10A
                        )

                        .section10_10AA(
                                section10_10AA
                        )

                        .section10_13A(
                                section10_13A
                        )

                        .section10_10B(
                                section10_10B
                        )

                        .otherSection10(
                                otherSection10
                        )

                        // 2(i)
                        .totalExemption(
                                totalExemption
                        )

                        // 3
                        .totalSalaryReceivedFromCurrentEmployer(
                                totalSalaryReceived
                        )

                        .build();


        Form16Exemption saved =
                form16ExemptionRepository.save(
                        exemption
                );


        return mapToResponse(
                saved,
                grossSalary
        );
    }


    // =========================================================
    // GET
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16ExemptionResponse
    getExemptionByForm16Id(
            Long form16Id) {


        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Exemption not found for Form16 ID: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // GET 1(d) AUTOMATIC
        // =====================================================

        Form16Salary salary =
                form16SalaryRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Salary not found for Form16 ID: "
                                                + form16Id
                                )
                        );


        BigDecimal grossSalary =
                safe(
                        salary.getGrossSalary()
                );


        return mapToResponse(
                exemption,
                grossSalary
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16ExemptionResponse updateExemption(
            Long form16Id,
            Form16ExemptionRequest request) {


        if (request == null) {

            throw new IllegalArgumentException(
                    "Exemption request is required."
            );
        }


        // =====================================================
        // FIND EXISTING
        // =====================================================

        Form16Exemption exemption =
                form16ExemptionRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Exemption not found for Form16 ID: "
                                                + form16Id
                                )
                        );


        // =====================================================
        // GET 1(d) AGAIN AUTOMATICALLY
        // =====================================================

        Form16Salary salary =
                form16SalaryRepository
                        .findByForm16Id(form16Id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Form16 Salary not found for Form16 ID: "
                                                + form16Id
                                )
                        );


        BigDecimal grossSalary =
                safe(
                        salary.getGrossSalary()
                );


        // =====================================================
        // REQUEST VALUES
        // =====================================================

        BigDecimal section10_5 =
                safe(request.getSection10_5());

        BigDecimal section10_10 =
                safe(request.getSection10_10());

        BigDecimal section10_10A =
                safe(request.getSection10_10A());

        BigDecimal section10_10AA =
                safe(request.getSection10_10AA());

        BigDecimal section10_13A =
                safe(request.getSection10_13A());

        BigDecimal section10_10B =
                safe(request.getSection10_10B());

        BigDecimal otherSection10 =
                safe(request.getOtherSection10());


        // =====================================================
        // 2(i) AUTOMATIC
        // =====================================================

        BigDecimal totalExemption =
                section10_5
                        .add(section10_10)
                        .add(section10_10A)
                        .add(section10_10AA)
                        .add(section10_13A)
                        .add(section10_10B)
                        .add(otherSection10);


        // =====================================================
        // 3 AUTOMATIC
        // =====================================================

        BigDecimal totalSalaryReceived =
                grossSalary
                        .subtract(totalExemption);


        if (
                totalSalaryReceived
                        .compareTo(BigDecimal.ZERO) < 0
        ) {

            totalSalaryReceived =
                    BigDecimal.ZERO;
        }


        // =====================================================
        // UPDATE
        // =====================================================

        exemption.setSection10_5(
                section10_5
        );

        exemption.setSection10_10(
                section10_10
        );

        exemption.setSection10_10A(
                section10_10A
        );

        exemption.setSection10_10AA(
                section10_10AA
        );

        exemption.setSection10_13A(
                section10_13A
        );

        exemption.setSection10_10B(
                section10_10B
        );

        exemption.setOtherSection10(
                otherSection10
        );


        // =====================================================
        // AUTOMATIC 2(i)
        // =====================================================

        exemption.setTotalExemption(
                totalExemption
        );


        // =====================================================
        // AUTOMATIC 3
        // =====================================================

        exemption.setTotalSalaryReceivedFromCurrentEmployer(
                totalSalaryReceived
        );


        Form16Exemption updated =
                form16ExemptionRepository.save(
                        exemption
                );


        return mapToResponse(
                updated,
                grossSalary
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteExemption(
            Long form16Id) {


        if (
                !form16ExemptionRepository
                        .existsByForm16Id(form16Id)
        ) {

            throw new RuntimeException(
                    "Exemption not found for Form16 ID: "
                            + form16Id
            );
        }


        form16ExemptionRepository
                .deleteByForm16Id(
                        form16Id
                );
    }


    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private Form16ExemptionResponse mapToResponse(
            Form16Exemption exemption,
            BigDecimal grossSalary) {


        return Form16ExemptionResponse.builder()

                .id(
                        exemption.getId()
                )

                .form16Id(
                        exemption
                                .getForm16()
                                .getId()
                )

                // 1(d)
                .grossSalary(
                        grossSalary
                )

                // 2(a)
                .section10_5(
                        exemption.getSection10_5()
                )

                // 2(b)
                .section10_10(
                        exemption.getSection10_10()
                )

                // 2(c)
                .section10_10A(
                        exemption.getSection10_10A()
                )

                // 2(d)
                .section10_10AA(
                        exemption.getSection10_10AA()
                )

                // 2(e)
                .section10_13A(
                        exemption.getSection10_13A()
                )

                // 2(f)
                .section10_10B(
                        exemption.getSection10_10B()
                )

                // 2(h)
                .otherSection10(
                        exemption.getOtherSection10()
                )

                // 2(i)
                .totalExemption(
                        exemption.getTotalExemption()
                )

                // 3
                .totalSalaryReceivedFromCurrentEmployer(
                        exemption
                                .getTotalSalaryReceivedFromCurrentEmployer()
                )

                .build();
    }


    // =========================================================
    // NULL → ZERO
    // =========================================================

    private BigDecimal safe(
            BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}