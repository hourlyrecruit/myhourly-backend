package com.my_hourly.payroll.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.payroll.dto.request.CreatePayrollRequest;
import com.my_hourly.payroll.dto.request.UpdateDraftPayrollRequest;
import com.my_hourly.payroll.dto.response.FailedPayroll;
import com.my_hourly.payroll.dto.response.PayrollResponse;
import com.my_hourly.payroll.dto.response.PayrollSummaryResponse;
import com.my_hourly.payroll.entity.EmployeePaymentDetails;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.entity.SalaryStructure;
import com.my_hourly.payroll.enums.PayrollHistoryAction;
import com.my_hourly.payroll.enums.PayrollStatus;
import com.my_hourly.payroll.enums.SalaryStructureStatus;
import com.my_hourly.payroll.repository.EmployeePaymentDetailsRepository;
import com.my_hourly.payroll.repository.PayrollRepository;
import com.my_hourly.payroll.repository.SalaryStructureRepository;
import com.my_hourly.payroll.service.PayrollHistoryService;
import com.my_hourly.payroll.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final EmployeePaymentDetailsRepository paymentDetailsRepository;
    private final PayrollHistoryService payrollHistoryService;

    /* =========================================================
       Generate Payroll
       ========================================================= */

    @Override
    public PayrollSummaryResponse generatePayroll(CreatePayrollRequest request) {

        List<Employee> employees = getEmployees(request);

        boolean saveAsDraft = Boolean.TRUE.equals(request.getSaveAsDraft());

        PayrollSummaryResponse summary = PayrollSummaryResponse.builder()
                .payrollMonth(request.getPayrollMonth())
                .totalEmployees(employees.size())
                .generated(0)
                .failed(0)
                .build();

        for (Employee employee : employees) {

            try {

                // Validation chain (Section 11 of Design Document)
                validateEmployee(employee, request.getPayrollMonth());

                validateNoDuplicateActivePayroll(employee, request.getPayrollMonth());

                SalaryStructure salaryStructure =
                        getActiveSalaryStructure(employee);

                EmployeePaymentDetails paymentDetails =
                        getPaymentDetails(employee);

                Payroll payroll = buildPayroll(
                        employee,
                        salaryStructure,
                        paymentDetails,
                        request,
                        saveAsDraft);

                payrollRepository.save(payroll);

                payrollHistoryService.saveHistory(
                        payroll,
                        saveAsDraft ? PayrollHistoryAction.UPDATED : PayrollHistoryAction.GENERATED,
                        "Payroll " + (saveAsDraft ? "saved as draft" : "generated")
                                + " for month " + request.getPayrollMonth());

                summary.getGeneratedPayrolls()
                        .add(payroll.getPayrollNumber());

                summary.setGenerated(summary.getGenerated() + 1);

            } catch (Exception ex) {

                summary.getFailedEmployees().add(
                        FailedPayroll.builder()
                                .employeeId(employee.getId())
                                .employeeCode(employee.getEmployeeCode())
                                .employeeName(employee.getFirstName()
                                        + (employee.getLastName() != null ? " " + employee.getLastName() : ""))
                                .reason(ex.getMessage())
                                .build()
                );

                summary.setFailed(summary.getFailed() + 1);
            }
        }

        return summary;
    }

    /* =========================================================
       Read Operations
       ========================================================= */

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getById(Long payrollId) {
        return mapToResponse(getPayroll(payrollId));
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getByPayrollNumber(String payrollNumber) {

        Payroll payroll = payrollRepository
                .findByPayrollNumber(payrollNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payroll not found : " + payrollNumber, ErrorCode.RESOURCE_NOT_FOUND));

        return mapToResponse(payroll);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponse> getByEmployee(Long employeeId) {

        return payrollRepository
                .findByEmployeeIdOrderByPayrollMonthDescVersionDesc(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponse> getByPayrollMonth(LocalDate payrollMonth) {

        return payrollRepository
                .findByPayrollMonth(payrollMonth)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollResponse> getByStatus(PayrollStatus status) {

        return payrollRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* =========================================================
       Lifecycle Transitions
       ========================================================= */

    @Override
    public PayrollResponse updateDraft(Long payrollId, UpdateDraftPayrollRequest request) {

        Payroll payroll = getPayroll(payrollId);

        if (payroll.getStatus() != PayrollStatus.DRAFT) {
            throw new BadRequestException(
                    "Only DRAFT payrolls can be edited. Current status: " + payroll.getStatus(),
                    ErrorCode.BAD_REQUEST);
        }

        // Recalculate attendance
        int totalWorkingDays = request.getTotalWorkingDays() != null
                ? request.getTotalWorkingDays() : payroll.getTotalWorkingDays();
        int workedDays = request.getWorkedDays() != null
                ? request.getWorkedDays() : payroll.getWorkedDays();
        int lopDays = request.getLopDays() != null
                ? request.getLopDays() : payroll.getLopDays();

        // Validate attendance constraint: workedDays + lopDays <= totalWorkingDays
        if (workedDays + lopDays > totalWorkingDays) {
            throw new BadRequestException(
                    "Worked days + LOP days cannot exceed total working days.", ErrorCode.BAD_REQUEST);
        }

        // Update earnings
        if (request.getBasicSalary() != null) payroll.setBasicSalary(request.getBasicSalary());
        if (request.getHra() != null) payroll.setHra(request.getHra());
        if (request.getSpecialAllowance() != null) payroll.setSpecialAllowance(request.getSpecialAllowance());
        if (request.getMedicalAllowance() != null) payroll.setMedicalAllowance(request.getMedicalAllowance());
        if (request.getTravelAllowance() != null) payroll.setTravelAllowance(request.getTravelAllowance());
        if (request.getBonus() != null) payroll.setBonus(request.getBonus());
        if (request.getOtherAllowance() != null) payroll.setOtherAllowance(request.getOtherAllowance());

        // Recalculate gross
        BigDecimal grossSalary = calculateGross(
                safe(payroll.getBasicSalary()),
                safe(payroll.getHra()),
                safe(payroll.getSpecialAllowance()),
                safe(payroll.getMedicalAllowance()),
                safe(payroll.getTravelAllowance()),
                safe(payroll.getBonus()),
                safe(payroll.getOtherAllowance()));

        payroll.setGrossSalary(grossSalary);

        // Update deductions
        if (request.getPf() != null) payroll.setPf(request.getPf());
        if (request.getEsi() != null) payroll.setEsi(request.getEsi());
        if (request.getProfessionalTax() != null) payroll.setProfessionalTax(request.getProfessionalTax());
        if (request.getIncomeTax() != null) payroll.setIncomeTax(request.getIncomeTax());
        if (request.getOtherDeduction() != null) payroll.setOtherDeduction(request.getOtherDeduction());

        // Recalculate LOP
        BigDecimal lopAmount = calculateLop(grossSalary, totalWorkingDays, lopDays);

        payroll.setLopAmount(lopAmount);
        payroll.setTotalWorkingDays(totalWorkingDays);
        payroll.setWorkedDays(workedDays);
        payroll.setLopDays(lopDays);
        payroll.setPayableDays(workedDays);

        // Recalculate deduction & net payable
        BigDecimal totalDeduction = calculateTotalDeduction(payroll);
        BigDecimal netPayable = grossSalary.subtract(totalDeduction);

        validateSalaryConstraints(grossSalary, totalDeduction, netPayable);

        payroll.setTotalDeduction(totalDeduction);
        payroll.setNetPayable(netPayable);

        if (request.getRemarks() != null) payroll.setRemarks(request.getRemarks());

        payrollRepository.save(payroll);

        payrollHistoryService.saveHistory(
                payroll,
                PayrollHistoryAction.UPDATED,
                "Draft payroll updated.");

        return mapToResponse(payroll);
    }

    @Override
    public PayrollResponse approve(Long payrollId) {

        Payroll payroll = getPayroll(payrollId);

        if (payroll.getStatus() != PayrollStatus.GENERATED) {
            throw new BadRequestException(
                    "Only GENERATED payroll can be approved. Current status: " + payroll.getStatus(),
                    ErrorCode.BAD_REQUEST);
        }

        payroll.setStatus(PayrollStatus.APPROVED);
        payroll.setApprovedDate(LocalDate.now());
        // payroll.setApprovedBy(SecurityUtils.getCurrentEmployee()); // TODO: wire in security context

        payrollRepository.save(payroll);

        payrollHistoryService.saveHistory(payroll, PayrollHistoryAction.APPROVED, "Payroll approved.");

        return mapToResponse(payroll);
    }

    @Override
    public PayrollResponse markAsPaid(Long payrollId, String paymentReference) {

        Payroll payroll = getPayroll(payrollId);

        if (payroll.getStatus() != PayrollStatus.APPROVED) {
            throw new BadRequestException(
                    "Payroll must be APPROVED before marking as PAID. Current status: " + payroll.getStatus(),
                    ErrorCode.BAD_REQUEST);
        }

        payroll.setStatus(PayrollStatus.PAID);
        payroll.setPaymentDate(LocalDate.now());
        payroll.setPaymentReference(paymentReference);

        payrollRepository.save(payroll);

        payrollHistoryService.saveHistory(
                payroll,
                PayrollHistoryAction.PAID,
                "Payroll marked as PAID. Reference: " + paymentReference);

        return mapToResponse(payroll);
    }

    @Override
    public PayrollResponse cancel(Long payrollId) {

        Payroll payroll = getPayroll(payrollId);

        if (payroll.getStatus() == PayrollStatus.APPROVED
                || payroll.getStatus() == PayrollStatus.PAID) {
            throw new BadRequestException(
                    "Approved or Paid payrolls cannot be cancelled.", ErrorCode.BAD_REQUEST);
        }

        if (payroll.getStatus() == PayrollStatus.CANCELLED) {
            throw new BadRequestException(
                    "Payroll is already cancelled.", ErrorCode.BAD_REQUEST);
        }

        payroll.setStatus(PayrollStatus.CANCELLED);
        payroll.setActive(false);

        payrollRepository.save(payroll);

        payrollHistoryService.saveHistory(payroll, PayrollHistoryAction.CANCELLED, "Payroll cancelled.");

        return mapToResponse(payroll);
    }

    @Override
    public PayrollResponse regenerate(Long payrollId) {

        Payroll oldPayroll = getPayroll(payrollId);

        if (oldPayroll.getStatus() == PayrollStatus.PAID) {
            throw new BadRequestException(
                    "Paid payrolls cannot be regenerated.", ErrorCode.BAD_REQUEST);
        }

        if (!Boolean.TRUE.equals(oldPayroll.getActive())) {
            throw new BadRequestException(
                    "Only the active payroll version can be regenerated.", ErrorCode.BAD_REQUEST);
        }

        // Mark old version as superseded
        oldPayroll.setStatus(PayrollStatus.SUPERSEDED);
        oldPayroll.setActive(false);
        payrollRepository.save(oldPayroll);

        // Create new version (carry over snapshots)
        Payroll newPayroll = Payroll.builder()
                .payrollNumber(generatePayrollNumber(oldPayroll.getPayrollMonth()))
                .version(oldPayroll.getVersion() + 1)
                .active(true)

                .employee(oldPayroll.getEmployee())
                .salaryStructure(oldPayroll.getSalaryStructure())
                .payrollMonth(oldPayroll.getPayrollMonth())

                // Employee snapshot
                .employeeName(oldPayroll.getEmployeeName())
                .employeeCode(oldPayroll.getEmployeeCode())
                .departmentName(oldPayroll.getDepartmentName())
                .designationName(oldPayroll.getDesignationName())

                // Payment snapshot
                .panNumber(oldPayroll.getPanNumber())
                .bankName(oldPayroll.getBankName())
                .accountNumber(oldPayroll.getAccountNumber())
                .ifscCode(oldPayroll.getIfscCode())

                // Attendance Snapshot
                .totalWorkingDays(oldPayroll.getTotalWorkingDays())
                .workedDays(oldPayroll.getWorkedDays())
                .lopDays(oldPayroll.getLopDays())
                .payableDays(oldPayroll.getPayableDays())

                // Salary Snapshot
                .basicSalary(oldPayroll.getBasicSalary())
                .hra(oldPayroll.getHra())
                .specialAllowance(oldPayroll.getSpecialAllowance())
                .medicalAllowance(oldPayroll.getMedicalAllowance())
                .travelAllowance(oldPayroll.getTravelAllowance())
                .bonus(oldPayroll.getBonus())
                .otherAllowance(oldPayroll.getOtherAllowance())
                .grossSalary(oldPayroll.getGrossSalary())

                // Deductions
                .lopAmount(oldPayroll.getLopAmount())
                .pf(oldPayroll.getPf())
                .esi(oldPayroll.getEsi())
                .professionalTax(oldPayroll.getProfessionalTax())
                .incomeTax(oldPayroll.getIncomeTax())
                .otherDeduction(oldPayroll.getOtherDeduction())
                .totalDeduction(oldPayroll.getTotalDeduction())

                // Final
                .netPayable(oldPayroll.getNetPayable())
                .status(PayrollStatus.GENERATED)
                .remarks(oldPayroll.getRemarks())
                .build();

        payrollRepository.save(newPayroll);

        payrollHistoryService.saveHistory(
                oldPayroll,
                PayrollHistoryAction.SUPERSEDED,
                "Superseded by version " + newPayroll.getVersion());

        payrollHistoryService.saveHistory(
                newPayroll,
                PayrollHistoryAction.REGENERATED,
                "Regenerated from version " + oldPayroll.getVersion());

        return mapToResponse(newPayroll);
    }

    /* =========================================================
       Private Helpers
       ========================================================= */

    private List<Employee> getEmployees(CreatePayrollRequest request) {

        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            return employeeRepository.findByActiveTrue();
        }

        return employeeRepository.findAllById(request.getEmployeeIds());
    }

    /**
     * Validates per Section 11 of Design Document.
     */
    private void validateEmployee(Employee employee, LocalDate payrollMonth) {

        if (!employee.isActive()) {
            throw new BadRequestException(
                    "Employee " + employee.getEmployeeCode() + " is not active.", ErrorCode.BAD_REQUEST);
        }

        if (employee.getDateOfJoining() != null
                && employee.getDateOfJoining().isAfter(payrollMonth)) {
            throw new BadRequestException(
                    "Employee " + employee.getEmployeeCode()
                            + " joined after the payroll month.", ErrorCode.BAD_REQUEST);
        }
    }

    private void validateNoDuplicateActivePayroll(Employee employee, LocalDate payrollMonth) {

        boolean exists = payrollRepository.existsByEmployeeIdAndPayrollMonthAndActiveTrue(
                employee.getId(), payrollMonth);

        if (exists) {
            throw new BadRequestException(
                    "Active payroll already exists for employee "
                            + employee.getEmployeeCode()
                            + " for month " + payrollMonth,
                    ErrorCode.PAYROLL_ALREADY_PROCESSED);
        }
    }

    private SalaryStructure getActiveSalaryStructure(Employee employee) {

        return salaryStructureRepository
                .findByEmployeeIdAndStatus(employee.getId(), SalaryStructureStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active salary structure not found for employee: "
                                        + employee.getEmployeeCode(),
                                ErrorCode.RESOURCE_NOT_FOUND));
    }

    private EmployeePaymentDetails getPaymentDetails(Employee employee) {

        return paymentDetailsRepository.findByEmployeeId(employee.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment details not found for employee: "
                                        + employee.getEmployeeCode(),
                                ErrorCode.RESOURCE_NOT_FOUND));
    }

    private Payroll buildPayroll(
            Employee employee,
            SalaryStructure salaryStructure,
            EmployeePaymentDetails paymentDetails,
            CreatePayrollRequest request,
            boolean saveAsDraft) {

        // Default attendance values (TODO: wire with attendance/leave module)
        int totalWorkingDays = 22;
        int workedDays = 22;
        int lopDays = 0;

        // Step 1: Gross Salary (Design Doc Section 5)
        BigDecimal grossSalary = calculateGross(
                safe(salaryStructure.getBasicSalary()),
                safe(salaryStructure.getHra()),
                safe(salaryStructure.getSpecialAllowance()),
                safe(salaryStructure.getMedicalAllowance()),
                safe(salaryStructure.getTravelAllowance()),
                safe(salaryStructure.getBonus()),
                safe(salaryStructure.getOtherAllowance()));

        // Step 2: LOP Deduction
        BigDecimal lopAmount = calculateLop(grossSalary, totalWorkingDays, lopDays);

        // Step 3: Total Deduction
        BigDecimal totalDeduction = safe(salaryStructure.getPf())
                .add(safe(salaryStructure.getEsi()))
                .add(safe(salaryStructure.getProfessionalTax()))
                .add(safe(salaryStructure.getIncomeTax()))
                .add(safe(salaryStructure.getOtherDeduction()))
                .add(lopAmount);

        // Step 4: Net Payable
        BigDecimal netPayable = grossSalary.subtract(totalDeduction);

        validateSalaryConstraints(grossSalary, totalDeduction, netPayable);

        // Employee snapshot fields
        String employeeName = employee.getFirstName()
                + (employee.getLastName() != null ? " " + employee.getLastName() : "");
        String departmentName = employee.getDepartment() != null
                ? employee.getDepartment().getDepartmentName() : null;
        String designationName = employee.getDesignation() != null
                ? employee.getDesignation().getDesignationName() : null;

        return Payroll.builder()
                .payrollNumber(generatePayrollNumber(request.getPayrollMonth()))
                .version(1)
                .active(true)

                .employee(employee)
                .salaryStructure(salaryStructure)
                .payrollMonth(request.getPayrollMonth())

                // Employee snapshot
                .employeeName(employeeName)
                .employeeCode(employee.getEmployeeCode())
                .departmentName(departmentName)
                .designationName(designationName)

                // Payment snapshot
                .panNumber(paymentDetails.getPanNumber())
                .bankName(paymentDetails.getBankName())
                .accountNumber(paymentDetails.getAccountNumber())
                .ifscCode(paymentDetails.getIfscCode())

                // Attendance Snapshot
                .totalWorkingDays(totalWorkingDays)
                .workedDays(workedDays)
                .lopDays(lopDays)
                .payableDays(workedDays)

                // Earnings Snapshot
                .basicSalary(salaryStructure.getBasicSalary())
                .hra(salaryStructure.getHra())
                .specialAllowance(salaryStructure.getSpecialAllowance())
                .medicalAllowance(salaryStructure.getMedicalAllowance())
                .travelAllowance(salaryStructure.getTravelAllowance())
                .bonus(salaryStructure.getBonus())
                .otherAllowance(salaryStructure.getOtherAllowance())
                .grossSalary(grossSalary)

                // Deductions
                .lopAmount(lopAmount)
                .pf(salaryStructure.getPf())
                .esi(salaryStructure.getEsi())
                .professionalTax(salaryStructure.getProfessionalTax())
                .incomeTax(salaryStructure.getIncomeTax())
                .otherDeduction(salaryStructure.getOtherDeduction())
                .totalDeduction(totalDeduction)

                // Final
                .netPayable(netPayable)

                .status(saveAsDraft ? PayrollStatus.DRAFT : PayrollStatus.GENERATED)

                .remarks(request.getRemarks())
                .build();
    }

    /**
     * Step 1: Gross Salary = Basic + HRA + Special + Medical + Travel + Bonus + Other
     */
    private BigDecimal calculateGross(
            BigDecimal basic, BigDecimal hra, BigDecimal special,
            BigDecimal medical, BigDecimal travel, BigDecimal bonus, BigDecimal other) {

        return basic.add(hra).add(special).add(medical).add(travel).add(bonus).add(other);
    }

    /**
     * Step 2: LOP Deduction = (Gross / Total Working Days) * LOP Days
     */
    private BigDecimal calculateLop(BigDecimal grossSalary, int totalWorkingDays, int lopDays) {

        if (lopDays <= 0 || totalWorkingDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal perDaySalary = grossSalary.divide(
                BigDecimal.valueOf(totalWorkingDays), 2, RoundingMode.HALF_UP);

        return perDaySalary.multiply(BigDecimal.valueOf(lopDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Step 3: Total Deduction for draft recalculation (uses payroll's current fields).
     */
    private BigDecimal calculateTotalDeduction(Payroll payroll) {

        return safe(payroll.getPf())
                .add(safe(payroll.getEsi()))
                .add(safe(payroll.getProfessionalTax()))
                .add(safe(payroll.getIncomeTax()))
                .add(safe(payroll.getOtherDeduction()))
                .add(safe(payroll.getLopAmount()));
    }

    /**
     * Section 11: Salary validation constraints
     */
    private void validateSalaryConstraints(
            BigDecimal grossSalary, BigDecimal totalDeduction, BigDecimal netPayable) {

        if (grossSalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Gross Salary must be greater than 0.", ErrorCode.BAD_REQUEST);
        }

        if (totalDeduction.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Total Deduction cannot be negative.", ErrorCode.BAD_REQUEST);
        }

        if (totalDeduction.compareTo(grossSalary) > 0) {
            throw new BadRequestException(
                    "Total Deduction cannot exceed Gross Salary.", ErrorCode.BAD_REQUEST);
        }

        if (netPayable.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Net Payable cannot be negative.", ErrorCode.BAD_REQUEST);
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String generatePayrollNumber(LocalDate payrollMonth) {

        String month = payrollMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));

        Optional<Payroll> latestPayroll =
                payrollRepository.findFirstByPayrollMonthOrderByIdDesc(payrollMonth);

        int nextSequence = 1;

        if (latestPayroll.isPresent()) {
            String payrollNumber = latestPayroll.get().getPayrollNumber();
            String[] parts = payrollNumber.split("-");
            if (parts.length == 3) {
                try {
                    nextSequence = Integer.parseInt(parts[2]) + 1;
                } catch (NumberFormatException ignored) {
                    nextSequence = 1;
                }
            }
        }

        return String.format("PR-%s-%04d", month, nextSequence);
    }

    private Payroll getPayroll(Long payrollId) {

        return payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payroll not found with id: " + payrollId,
                                ErrorCode.RESOURCE_NOT_FOUND));
    }

    private void validatePayrollGeneration(Employee employee, LocalDate payrollMonth) {

        boolean exists = payrollRepository.existsByEmployeeIdAndPayrollMonthAndStatusNot(
                employee.getId(), payrollMonth, PayrollStatus.SUPERSEDED);

        if (exists) {
            throw new BadRequestException(
                    "Payroll already generated for employee: " + employee.getEmployeeCode(),
                    ErrorCode.PAYROLL_ALREADY_PROCESSED);
        }
    }

    /* =========================================================
       Response Mapping
       ========================================================= */

    private PayrollResponse mapToResponse(Payroll payroll) {

        return PayrollResponse.builder()
                .id(payroll.getId())
                .payrollNumber(payroll.getPayrollNumber())
                .version(payroll.getVersion())
                .active(payroll.getActive())

                .employeeId(payroll.getEmployee().getId())
                .employeeCode(payroll.getEmployeeCode())
                .employeeName(payroll.getEmployeeName())
                .departmentName(payroll.getDepartmentName())
                .designationName(payroll.getDesignationName())

                .panNumber(payroll.getPanNumber())
                .bankName(payroll.getBankName())
                .accountNumber(payroll.getAccountNumber())
                .ifscCode(payroll.getIfscCode())

                .payrollMonth(payroll.getPayrollMonth())
                .status(payroll.getStatus())

                .totalWorkingDays(payroll.getTotalWorkingDays())
                .workedDays(payroll.getWorkedDays())
                .lopDays(payroll.getLopDays())
                .payableDays(payroll.getPayableDays())

                .basicSalary(payroll.getBasicSalary())
                .hra(payroll.getHra())
                .specialAllowance(payroll.getSpecialAllowance())
                .medicalAllowance(payroll.getMedicalAllowance())
                .travelAllowance(payroll.getTravelAllowance())
                .bonus(payroll.getBonus())
                .otherAllowance(payroll.getOtherAllowance())
                .grossSalary(payroll.getGrossSalary())

                .lopAmount(payroll.getLopAmount())
                .pf(payroll.getPf())
                .esi(payroll.getEsi())
                .professionalTax(payroll.getProfessionalTax())
                .incomeTax(payroll.getIncomeTax())
                .otherDeduction(payroll.getOtherDeduction())
                .totalDeduction(payroll.getTotalDeduction())

                .netPayable(payroll.getNetPayable())

                .approvedBy(
                        payroll.getApprovedBy() != null
                                ? payroll.getApprovedBy().getFirstName()
                                + (payroll.getApprovedBy().getLastName() != null
                                ? " " + payroll.getApprovedBy().getLastName() : "")
                                : null)
                .approvedDate(payroll.getApprovedDate())

                .paymentDate(payroll.getPaymentDate())
                .paymentReference(payroll.getPaymentReference())

                .remarks(payroll.getRemarks())

                .createdAt(payroll.getCreatedAt())
                .updatedAt(payroll.getUpdatedAt())

                .build();
    }
}
