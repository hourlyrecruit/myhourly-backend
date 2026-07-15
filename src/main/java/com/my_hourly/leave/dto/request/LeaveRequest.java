package com.my_hourly.leave.dto.request;

import java.time.LocalDate;

public class LeaveRequest {

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    // =====================================================
    // Leave Details
    // =====================================================

    private LocalDate fromDate;

    private LocalDate toDate;

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveRequest(String employeeCode,
                        LocalDate fromDate,
                        LocalDate toDate) {

        this.employeeCode = employeeCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

}