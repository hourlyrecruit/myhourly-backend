package com.my_hourly.payroll.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.DuplicateResourceException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.payroll.dto.request.CreateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.request.UpdateSalaryTemplateRequest;
import com.my_hourly.payroll.dto.request.UpdateSalaryTemplateStatusRequest;
import com.my_hourly.payroll.dto.response.SalaryTemplateResponse;
import com.my_hourly.payroll.entity.SalaryTemplate;
import com.my_hourly.payroll.repository.SalaryTemplateRepository;
import com.my_hourly.payroll.service.SalaryTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryTemplateServiceImpl implements SalaryTemplateService {

    private final SalaryTemplateRepository salaryTemplateRepository;

    @Override
    public SalaryTemplateResponse create(CreateSalaryTemplateRequest request) {

        validateDuplicate(request.getEmployeeType());

        SalaryTemplate template = SalaryTemplate.builder()
                .employeeType(request.getEmployeeType())
                .basicSalary(request.getBasicSalary())
                .hra(request.getHra())
                .specialAllowance(request.getSpecialAllowance())
                .medicalAllowance(request.getMedicalAllowance())
                .travelAllowance(request.getTravelAllowance())
                .bonus(request.getBonus())
                .otherAllowance(request.getOtherAllowance())
                .grossSalary(calculateGrossSalary(request))
                .pf(request.getPf())
                .esi(request.getEsi())
                .professionalTax(request.getProfessionalTax())
                .incomeTax(request.getIncomeTax())
                .otherDeduction(request.getOtherDeduction())
                .active(true)
                .build();

        return mapToResponse(
                salaryTemplateRepository.save(template));
    }

    @Override
    public SalaryTemplateResponse update(
            Long id,
            UpdateSalaryTemplateRequest request) {

        SalaryTemplate template = getTemplate(id);

        template.setBasicSalary(request.getBasicSalary());
        template.setHra(request.getHra());
        template.setSpecialAllowance(request.getSpecialAllowance());
        template.setMedicalAllowance(request.getMedicalAllowance());
        template.setTravelAllowance(request.getTravelAllowance());
        template.setBonus(request.getBonus());
        template.setOtherAllowance(request.getOtherAllowance());

        template.setGrossSalary(
                calculateGrossSalary(
                        request.getBasicSalary(),
                        request.getHra(),
                        request.getSpecialAllowance(),
                        request.getMedicalAllowance(),
                        request.getTravelAllowance(),
                        request.getBonus(),
                        request.getOtherAllowance()));

        template.setPf(request.getPf());
        template.setEsi(request.getEsi());
        template.setProfessionalTax(request.getProfessionalTax());
        template.setIncomeTax(request.getIncomeTax());
        template.setOtherDeduction(request.getOtherDeduction());

        return mapToResponse(
                salaryTemplateRepository.save(template));
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryTemplateResponse getById(Long id) {
        return mapToResponse(getTemplate(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryTemplateResponse getByEmployeeType(
            EmploymentType employeeType) {

        SalaryTemplate template = salaryTemplateRepository
                .findByEmployeeTypeAndActiveTrue(employeeType)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active salary template found for employee type: "
                                        + employeeType, ErrorCode.RESOURCE_NOT_FOUND));

        return mapToResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryTemplateResponse> getAll(Boolean activeOnly) {

        List<SalaryTemplate> templates;
        if (Boolean.TRUE.equals(activeOnly)) {
            templates = salaryTemplateRepository.findByActiveTrue();
        } else {
            templates = salaryTemplateRepository.findAll();
        }

        return templates.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SalaryTemplateResponse updateStatus(Long id, UpdateSalaryTemplateStatusRequest request) {

        SalaryTemplate template = getTemplate(id);

        template.setActive(request.getActive());

        return mapToResponse(salaryTemplateRepository.save(template));
    }

    // =========================================================
    // Private Methods
    // =========================================================

    private SalaryTemplate getTemplate(Long id) {

        return salaryTemplateRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Salary Template not found with id: " + id, ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void validateDuplicate(EmploymentType employeeType) {

        if (salaryTemplateRepository.existsByEmployeeType(employeeType)) {
            throw new DuplicateResourceException(
                    "Salary Template already exists for Employee Type: "
                            + employeeType, ErrorCode.SALARY_TEMPLATE_ALREADY_PRESENT);
        }
    }

    private BigDecimal calculateGrossSalary(
            CreateSalaryTemplateRequest request) {

        return calculateGrossSalary(
                request.getBasicSalary(),
                request.getHra(),
                request.getSpecialAllowance(),
                request.getMedicalAllowance(),
                request.getTravelAllowance(),
                request.getBonus(),
                request.getOtherAllowance());
    }

    private BigDecimal calculateGrossSalary(
            BigDecimal basic,
            BigDecimal hra,
            BigDecimal special,
            BigDecimal medical,
            BigDecimal travel,
            BigDecimal bonus,
            BigDecimal other) {

        return basic
                .add(hra)
                .add(special)
                .add(medical)
                .add(travel)
                .add(bonus)
                .add(other);
    }

    private SalaryTemplateResponse mapToResponse(
            SalaryTemplate template) {

        return SalaryTemplateResponse.builder()
                .id(template.getId())
                .employeeType(template.getEmployeeType())
                .basicSalary(template.getBasicSalary())
                .hra(template.getHra())
                .specialAllowance(template.getSpecialAllowance())
                .medicalAllowance(template.getMedicalAllowance())
                .travelAllowance(template.getTravelAllowance())
                .bonus(template.getBonus())
                .otherAllowance(template.getOtherAllowance())
                .grossSalary(template.getGrossSalary())
                .pf(template.getPf())
                .esi(template.getEsi())
                .professionalTax(template.getProfessionalTax())
                .incomeTax(template.getIncomeTax())
                .otherDeduction(template.getOtherDeduction())
                .active(template.getActive())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
