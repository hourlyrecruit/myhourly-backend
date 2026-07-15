package com.my_hourly.employee.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {

    // =====================================================
    // Default Constructor
    // =====================================================

    public EmployeeAlreadyExistsException() {
        super();
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }

}