package com.my_hourly.payroll.pdf;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.enums.PayrollStatus;
import com.my_hourly.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayslipPdfServiceImpl implements PayslipPdfService {

    private final PayrollRepository payrollRepository;
    private final EmployeeService employeeService;

    @Override
    public byte[] generatePayslip(Long payrollId) {


        // Section 10: Validate payroll exists and is approved or paid
        if (payrollId == null) {
            throw new BadRequestException(
                    "Payroll ID is required to generate payslip.",
                    ErrorCode.BAD_REQUEST);
        }
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payroll not found with id: " + payrollId,
                                ErrorCode.RESOURCE_NOT_FOUND));

        // Section 11: Payslip PDF only available for APPROVED or PAID payrolls
        if (payroll.getStatus() != PayrollStatus.APPROVED
                && payroll.getStatus() != PayrollStatus.PAID) {
            throw new BadRequestException(
                    "Payslip can only be generated for APPROVED or PAID payrolls. "
                            + "Current status: " + payroll.getStatus(),
                    ErrorCode.BAD_REQUEST);
        }

        // Payslip only for active (not superseded / cancelled) versions
        if (!Boolean.TRUE.equals(payroll.getActive())) {
            throw new BadRequestException(
                    "Payslip cannot be generated for a superseded or cancelled payroll version.",
                    ErrorCode.BAD_REQUEST);
        }

        return PayslipGenerator.generate(payroll);
    }
}
