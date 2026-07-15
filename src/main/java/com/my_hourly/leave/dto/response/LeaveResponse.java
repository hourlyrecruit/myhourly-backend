package com.my_hourly.leave.dto.response;

import java.time.LocalDate;

import com.my_hourly.leave.enums.LeaveStatus;

public class LeaveResponse {

    // =====================================================
    // Primary Key
    // =====================================================

    private Long id;

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    private String employeeName;

    // =====================================================
    // Leave Details
    // =====================================================

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer totalDays;

    // =====================================================
    // Leave Status
    // =====================================================

    private LeaveStatus status;

    // Null while pending
    private String managerRemarks;

    // =====================================================
    // Dates
    // =====================================================

    private LocalDate appliedDate;

    private LocalDate approvedDate;

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
                         String employeeName,
                         LocalDate fromDate,
                         LocalDate toDate,
                         Integer totalDays,
                         LeaveStatus status,
                         String managerRemarks,
                         LocalDate appliedDate,
                         LocalDate approvedDate) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalDays = totalDays;
        this.status = status;
        this.managerRemarks = managerRemarks;
        this.appliedDate = appliedDate;
        this.approvedDate = approvedDate;
    }

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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
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

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getManagerRemarks() {
        return managerRemarks;
    }

    public void setManagerRemarks(String managerRemarks) {
        this.managerRemarks = managerRemarks;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
}