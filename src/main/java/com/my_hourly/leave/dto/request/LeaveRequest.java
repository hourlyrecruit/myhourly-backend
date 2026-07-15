package com.my_hourly.leave.dto.request;

import java.time.LocalDate;

import com.my_hourly.leave.enums.LeaveType;

public class LeaveRequest {

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    // =====================================================
    // Leave Details
    // =====================================================

    private LeaveType leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveRequest(String employeeCode,
                        LeaveType leaveType,
                        LocalDate fromDate,
                        LocalDate toDate,
                        String reason) {
        this.employeeCode = employeeCode;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}