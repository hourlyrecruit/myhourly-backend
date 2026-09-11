package com.my_hourly.payroll.pdf;

public class PayslipGenerationException
        extends RuntimeException {

    public PayslipGenerationException(
            String message) {

        super(message);
    }

    public PayslipGenerationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}