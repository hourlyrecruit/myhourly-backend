package com.my_hourly.payroll.pdf;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.payroll.entity.Payroll;
import com.my_hourly.payroll.enums.PayrollStatus;
import com.my_hourly.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayslipPdfServiceImpl implements PayslipPdfService {

    private final PayrollRepository payrollRepository;
    private final PayslipGenerator payslipGenerator;

    @Override
    public byte[] generatePayslip(Long payrollId) {

        // 1. Validate payroll ID
        if (payrollId == null) {
            throw new BadRequestException(
                    "Payroll ID is required to generate payslip.",
                    ErrorCode.BAD_REQUEST
            );
        }

        // 2. Find payroll
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payroll not found with id: " + payrollId,
                                ErrorCode.RESOURCE_NOT_FOUND
                        )
                );

        // 3. Payslip can only be generated for APPROVED or PAID payroll
        if (payroll.getStatus() != PayrollStatus.APPROVED
                && payroll.getStatus() != PayrollStatus.PAID) {

            throw new BadRequestException(
                    "Payslip can only be generated for APPROVED or PAID payrolls. "
                            + "Current status: " + payroll.getStatus(),
                    ErrorCode.BAD_REQUEST
            );
        }

        // 4. Only active payroll version can generate payslip
        if (!Boolean.TRUE.equals(payroll.getActive())) {
            throw new BadRequestException(
                    "Payslip cannot be generated for a superseded or cancelled payroll version.",
                    ErrorCode.BAD_REQUEST
            );
        }

        // 5. Generate PDF
        return payslipGenerator.generate(payroll);
    }
}