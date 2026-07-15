package com.my_hourly.leave.dto.response;

import com.my_hourly.employee.enums.EmploymentType;

public class LeaveBalanceResponse {

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    private String employeeFirstName;

    private String employeeLastName;

    private EmploymentType employmentType;

    // =====================================================
    // Leave Balance Details
    // =====================================================

    private Integer year;

    private Integer month;

    private Integer monthlyLeave;

    private Integer usedLeave;

    private Integer availableLeave;

    private Integer expiredLeave;

    // =====================================================
    // Default Constructor
    // =====================================================

    public LeaveBalanceResponse() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public LeaveBalanceResponse(
            String employeeCode,
            String employeeFirstName,
            String employeeLastName,
            EmploymentType employmentType,
            Integer year,
            Integer month,
            Integer monthlyLeave,
            Integer usedLeave,
            Integer availableLeave,
            Integer expiredLeave) {

        this.employeeCode = employeeCode;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.employmentType = employmentType;
        this.year = year;
        this.month = month;
        this.monthlyLeave = monthlyLeave;
        this.usedLeave = usedLeave;
        this.availableLeave = availableLeave;
        this.expiredLeave = expiredLeave;
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

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getMonthlyLeave() {
        return monthlyLeave;
    }

    public void setMonthlyLeave(Integer monthlyLeave) {
        this.monthlyLeave = monthlyLeave;
    }

    public Integer getUsedLeave() {
        return usedLeave;
    }

    public void setUsedLeave(Integer usedLeave) {
        this.usedLeave = usedLeave;
    }

    public Integer getAvailableLeave() {
        return availableLeave;
    }

    public void setAvailableLeave(Integer availableLeave) {
        this.availableLeave = availableLeave;
    }

    public Integer getExpiredLeave() {
        return expiredLeave;
    }

    public void setExpiredLeave(Integer expiredLeave) {
        this.expiredLeave = expiredLeave;
    }

}