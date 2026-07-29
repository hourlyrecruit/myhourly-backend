package com.my_hourly.payroll.pdf;

public interface PayslipPdfService {

    byte[] generatePayslip(Long payrollId);

}
