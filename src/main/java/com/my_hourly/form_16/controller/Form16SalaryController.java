package com.my_hourly.form_16.serviceImpl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.dto.Form16SalaryResponse;
import com.my_hourly.form_16.entity.Form16;
import com.my_hourly.form_16.entity.Form16Salary;
import com.my_hourly.form_16.repository.Form16Repository;
import com.my_hourly.form_16.repository.Form16SalaryRepository;
import com.my_hourly.form_16.service.Form16SalaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16SalaryServiceImpl implements Form16SalaryService {

    private final Form16SalaryRepository form16SalaryRepository;
    private final Form16Repository form16Repository;

    // CREATE
    @Override
    public Form16SalaryResponse createSalary(
            Long form16Id,
            Form16SalaryRequest request) {

        validateRequest(request);

        Form16 form16 = form16Repository.findById(form16Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Form16 not found with ID: " + form16Id,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        if (form16SalaryRepository.existsByForm16Id(form16Id)) {
            throw new IllegalArgumentException(
                    "Salary details already exist for Form16 ID: "
                            + form16Id
            );
        }

        Form16Salary salary = new Form16Salary();
        salary.setForm16(form16);

        setSalaryValues(salary, request);
        calculateGrossSalary(salary);

        Form16Salary saved = form16SalaryRepository.save(salary);

        return mapToResponse(saved);
    }

    // GET
    @Override
    @Transactional(readOnly = true)
    public Form16SalaryResponse getSalaryByForm16Id(Long form16Id) {

        Form16Salary salary = form16SalaryRepository
                .findByForm16Id(form16Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary details not found for Form16 ID: "
                                + form16Id,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        return mapToResponse(salary);
    }

    // UPDATE
    @Override
    public Form16SalaryResponse updateSalary(
            Long form16Id,
            Form16SalaryRequest request) {

        validateRequest(request);

        Form16Salary salary = form16SalaryRepository
                .findByForm16Id(form16Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary details not found for Form16 ID: "
                                + form16Id,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        setSalaryValues(salary, request);
        calculateGrossSalary(salary);

        Form16Salary saved = form16SalaryRepository.save(salary);

        return mapToResponse(saved);
    }

    // DELETE
    @Override
    public void deleteSalary(Long form16Id) {

        Form16Salary salary = form16SalaryRepository
                .findByForm16Id(form16Id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary details not found for Form16 ID: "
                                + form16Id,
                        ErrorCode.RESOURCE_NOT_FOUND
                ));

        form16SalaryRepository.delete(salary);
    }

    // SET REQUEST VALUES
    private void setSalaryValues(
            Form16Salary salary,
            Form16SalaryRequest request) {

        salary.setSalaryUnderSection17_1(
                zeroIfNull(request.getSalaryUnderSection17_1())
        );

        salary.setPerquisitesUnderSection17_2(
                zeroIfNull(request.getPerquisitesUnderSection17_2())
        );

        salary.setProfitsInLieuOfSalaryUnderSection17_3(
                zeroIfNull(
                        request.getProfitsInLieuOfSalaryUnderSection17_3()
                )
        );

        salary.setSalaryReceivedFromOtherEmployers(
                zeroIfNull(request.getSalaryReceivedFromOtherEmployers())
        );
    }

    // GROSS SALARY = SECTION 17(1) + 17(2) + 17(3)
    private void calculateGrossSalary(Form16Salary salary) {

        BigDecimal grossSalary =
                zeroIfNull(salary.getSalaryUnderSection17_1())
                        .add(zeroIfNull(
                                salary.getPerquisitesUnderSection17_2()
                        ))
                        .add(zeroIfNull(
                                salary.getProfitsInLieuOfSalaryUnderSection17_3()
                        ));

        salary.setGrossSalary(grossSalary);
    }

    // ENTITY TO RESPONSE DTO
    private Form16SalaryResponse mapToResponse(Form16Salary salary) {

        return Form16SalaryResponse.builder()
                .salaryUnderSection17_1(
                        zeroIfNull(salary.getSalaryUnderSection17_1())
                )
                .perquisitesUnderSection17_2(
                        zeroIfNull(salary.getPerquisitesUnderSection17_2())
                )
                .profitsInLieuOfSalaryUnderSection17_3(
                        zeroIfNull(
                                salary.getProfitsInLieuOfSalaryUnderSection17_3()
                        )
                )
                .grossSalary(
                        zeroIfNull(salary.getGrossSalary())
                )
                .salaryReceivedFromOtherEmployers(
                        zeroIfNull(
                                salary.getSalaryReceivedFromOtherEmployers()
                        )
                )
                .build();
    }

    private void validateRequest(Form16SalaryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Form16 Salary request cannot be null."
            );
        }
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
