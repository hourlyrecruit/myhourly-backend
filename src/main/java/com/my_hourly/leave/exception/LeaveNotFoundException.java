package com.my_hourly.leave.exception;

public class LeaveNotFoundException extends RuntimeException {

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveNotFoundException() {
        super();
    }

    // =====================================================
    // Constructor with Message
    // =====================================================

    public LeaveNotFoundException(String message) {
        super(message);
    }

}