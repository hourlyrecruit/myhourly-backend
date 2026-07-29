package com.my_hourly.payroll.service.impl;


import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.payroll.dto.request.CreateSalaryStructureRequest;
import com.my_hourly.payroll.dto.response.SalaryStructureResponse;
import com.my_hourly.payroll.entity.SalaryStructure;
import com.my_hourly.payroll.entity.SalaryTemplate;
import com.my_hourly.payroll.enums.SalaryStructureStatus;
import com.my_hourly.payroll.repository.SalaryStructureRepository;
import com.my_hourly.payroll.repository.SalaryTemplateRepository;
import com.my_hourly.payroll.service.SalaryStructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureServiceImpl implements SalaryStructureService {

    private final EmployeeRepository employeeRepository;
    private final SalaryTemplateRepository salaryTemplateRepository;
    private final SalaryStructureRepository salaryStructureRepository;

    @Override
    public SalaryStructureResponse create(CreateSalaryStructureRequest request) {

        Employee employee = getEmployee(request.getEmployeeId());

        if (salaryStructureRepository.existsByEmployeeIdAndStatus(
                employee.getId(),
                SalaryStructureStatus.ACTIVE)) {

            throw new BadRequestException(
                    "Employee already has an active salary structure.", ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        }

        SalaryTemplate template = getSalaryTemplate(
                request.getSalaryTemplateId());

        SalaryStructure salaryStructure =
                buildSalaryStructure(employee, template, request);

        return mapToResponse(
                salaryStructureRepository.save(salaryStructure));
    }

    @Override
    public SalaryStructureResponse createRevision(
            CreateSalaryStructureRequest request) {

        Employee employee = getEmployee(request.getEmployeeId());

        SalaryStructure currentSalary =
                getActiveSalaryStructure(employee.getId());

        if (!request.getEffectiveFrom()
                .isAfter(currentSalary.getEffectiveFrom())) {

            throw new IllegalArgumentException(
                    "Effective From must be after current salary structure.");
        }

        deactivateCurrentSalaryStructure(
                currentSalary,
                request.getEffectiveFrom());

        SalaryTemplate template =
                getSalaryTemplate(request.getSalaryTemplateId());

        SalaryStructure newSalary =
                buildSalaryStructure(employee, template, request);

        return mapToResponse(
                salaryStructureRepository.save(newSalary));
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryStructureResponse getById(Long id) {

        return mapToResponse(getSalaryStructure(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SalaryStructureResponse getActiveByEmployee(Long employeeId) {

        return mapToResponse(
                getActiveSalaryStructure(employeeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryStructureResponse> getHistory(Long employeeId) {

        return salaryStructureRepository
                .findByEmployeeIdOrderByEffectiveFromDesc(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaryStructureResponse> getAllActive() {

        return salaryStructureRepository
                .findByStatus(SalaryStructureStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Employee getEmployee(Long employeeId) {

        return employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: " + employeeId, ErrorCode.RESOURCE_NOT_FOUND));
    }

    private SalaryTemplate getSalaryTemplate(Long templateId) {

        return salaryTemplateRepository.findById(templateId)
                .filter(SalaryTemplate::getActive)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active Salary Template not found with id: " + templateId, ErrorCode.RESOURCE_NOT_FOUND));
    }

    private SalaryStructure getSalaryStructure(Long id) {

        return salaryStructureRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Salary Structure not found with id: " + id, ErrorCode.RESOURCE_NOT_FOUND));
    }

    private SalaryStructure getActiveSalaryStructure(Long employeeId) {

        return salaryStructureRepository
                .findByEmployeeIdAndStatus(
                        employeeId,
                        SalaryStructureStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active Salary Structure not found.", ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void deactivateCurrentSalaryStructure(
            SalaryStructure currentSalary,
            LocalDate newEffectiveFrom) {

        currentSalary.setStatus(SalaryStructureStatus.INACTIVE);

        currentSalary.setEffectiveTo(
                newEffectiveFrom.minusDays(1));

        salaryStructureRepository.save(currentSalary);
    }

    private SalaryStructure buildSalaryStructure(
            Employee employee,
            SalaryTemplate template,
            CreateSalaryStructureRequest request) {

        return SalaryStructure.builder()
                .employee(employee)
                .salaryTemplate(template)

                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(null)

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

                .netSalary(calculateNetSalary(template))

                .status(SalaryStructureStatus.ACTIVE)

                .remarks(request.getRemarks())

                .build();
    }

    private BigDecimal calculateNetSalary(
            SalaryTemplate template) {

        return template.getGrossSalary()
                .subtract(template.getPf())
                .subtract(template.getEsi())
                .subtract(template.getProfessionalTax())
                .subtract(template.getIncomeTax())
                .subtract(template.getOtherDeduction());
    }

    private SalaryStructureResponse mapToResponse(
            SalaryStructure salaryStructure) {

        return SalaryStructureResponse.builder()

                .id(salaryStructure.getId())

                .employeeId(salaryStructure.getEmployee().getId())
                .employeeCode(salaryStructure.getEmployee().getEmployeeCode())
                .employeeName(salaryStructure.getEmployee().getFirstName() + " " + salaryStructure.getEmployee().getLastName())

                .salaryTemplateId(
                        salaryStructure.getSalaryTemplate() != null
                                ? salaryStructure.getSalaryTemplate().getId()
                                : null)

                .effectiveFrom(salaryStructure.getEffectiveFrom())
                .effectiveTo(salaryStructure.getEffectiveTo())

                .basicSalary(salaryStructure.getBasicSalary())
                .hra(salaryStructure.getHra())
                .specialAllowance(salaryStructure.getSpecialAllowance())
                .medicalAllowance(salaryStructure.getMedicalAllowance())
                .travelAllowance(salaryStructure.getTravelAllowance())
                .bonus(salaryStructure.getBonus())
                .otherAllowance(salaryStructure.getOtherAllowance())

                .grossSalary(salaryStructure.getGrossSalary())

                .pf(salaryStructure.getPf())
                .esi(salaryStructure.getEsi())
                .professionalTax(salaryStructure.getProfessionalTax())
                .incomeTax(salaryStructure.getIncomeTax())
                .otherDeduction(salaryStructure.getOtherDeduction())

                .netSalary(salaryStructure.getNetSalary())

                .status(salaryStructure.getStatus())

                .remarks(salaryStructure.getRemarks())

                .createdAt(salaryStructure.getCreatedAt())
                .updatedAt(salaryStructure.getUpdatedAt())

                .build();
    }
}