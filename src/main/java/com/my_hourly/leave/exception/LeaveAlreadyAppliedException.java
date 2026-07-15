package com.my_hourly.leave.exception;

public class LeaveAlreadyAppliedException extends RuntimeException {

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveAlreadyAppliedException() {
        super();
    }

    // =====================================================
    // Constructor with Message
    // =====================================================

    public LeaveAlreadyAppliedException(String message) {
        super(message);
    }

}