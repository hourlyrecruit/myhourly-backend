package com.my_hourly.leave.dto.response;

import java.time.LocalDate;

import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.enums.LeaveType;

public class LeaveResponse {

    // =====================================================
    // Primary Key
    // =====================================================

    private Long id;

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    private String employeeFirstName;

    private String employeeLastName;

    // =====================================================
    // Leave Details
    // =====================================================

    private LeaveType leaveType;

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reason;

    // =====================================================
    // Leave Status
    // =====================================================

    private LeaveStatus status;

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveResponse() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveResponse(Long id,
                         String employeeCode,
                         String employeeFirstName,
                         String employeeLastName,
                         LeaveType leaveType,
                         LocalDate fromDate,
                         LocalDate toDate,
                         String reason,
                         LeaveStatus status) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.leaveType = leaveType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.status = status;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
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

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

}