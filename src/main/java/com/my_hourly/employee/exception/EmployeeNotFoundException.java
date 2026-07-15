package com.my_hourly.employee.exception;

public class EmployeeNotFoundException extends RuntimeException {

    // =====================================================
    // Default Constructor
    // =====================================================

    public EmployeeNotFoundException() {
        super();
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public EmployeeNotFoundException(String message) {
        super(message);
    }

}