package com.my_hourly.employee.dto.request;

import com.my_hourly.employee.enums.EmploymentType;

public class CreateEmployeeRequest {

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

    // =====================================================
    // Reporting Manager
    // =====================================================

    private String reportingManagerCode;

    // =====================================================
    // Dummy Password
    // =====================================================

    private String password;

    // =====================================================
    // Default Constructor
    // =====================================================

    public CreateEmployeeRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public CreateEmployeeRequest(
            String firstName,
            String lastName,
            String officeEmail,
            String departmentName,
            String designationName,
            EmploymentType employmentType,
            String reportingManagerCode,
            String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.officeEmail = officeEmail;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.employmentType = employmentType;
        this.reportingManagerCode = reportingManagerCode;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}