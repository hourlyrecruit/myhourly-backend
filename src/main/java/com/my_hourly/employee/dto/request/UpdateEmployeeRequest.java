package com.my_hourly.employee.dto.request;

import java.time.LocalDate;

import com.my_hourly.employee.enums.EmploymentType;

public class UpdateEmployeeRequest {

    // =====================================================
    // Basic Employee Details
    // =====================================================

    private String firstName;

    private String lastName;

    private String officeEmail;

    // =====================================================
    // Employment Details
    // =====================================================

    private String departmentName;

    private String designationName;

    private EmploymentType employmentType;

    private String reportingManagerCode;

    private LocalDate dateOfJoining;

    // =====================================================
    // Employee Status
    // =====================================================

    private Boolean active;

    // =====================================================
    // Default Constructor
    // =====================================================

    public UpdateEmployeeRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public UpdateEmployeeRequest(String firstName,
                                 String lastName,
                                 String officeEmail,
                                 String departmentName,
                                 String designationName,
                                 EmploymentType employmentType,
                                 String reportingManagerCode,
                                 LocalDate dateOfJoining,
                                 Boolean active) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.officeEmail = officeEmail;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.employmentType = employmentType;
        this.reportingManagerCode = reportingManagerCode;
        this.dateOfJoining = dateOfJoining;
        this.active = active;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOfficeEmail() {
        return officeEmail;
    }

    public void setOfficeEmail(String officeEmail) {
        this.officeEmail = officeEmail;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public String getReportingManagerCode() {
        return reportingManagerCode;
    }

    public void setReportingManagerCode(String reportingManagerCode) {
        this.reportingManagerCode = reportingManagerCode;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}