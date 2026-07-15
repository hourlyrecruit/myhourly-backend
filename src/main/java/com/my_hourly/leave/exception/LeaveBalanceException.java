package com.my_hourly.leave.exception;

public class LeaveBalanceException extends RuntimeException {

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveBalanceException() {
        super();
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveBalanceException(String message) {
        super(message);
    }

}