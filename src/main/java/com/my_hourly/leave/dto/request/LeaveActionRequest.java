package com.my_hourly.leave.dto.request;

import com.my_hourly.leave.enums.LeaveStatus;

public class LeaveActionRequest {

    // =====================================================
    // Leave Action
    // =====================================================

    private LeaveStatus status;

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveActionRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveActionRequest(LeaveStatus status) {
        this.status = status;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

}