package com.my_hourly.employee.dto.response;

import java.time.LocalDate;

import com.my_hourly.employee.enums.EmploymentType;
import com.my_hourly.employee.enums.Gender;

public class EmployeeResponse {

    // =====================================================
    // Primary Key
    // =====================================================

    private Long id;

    // =====================================================
    // Employee Details
    // =====================================================

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String officeEmail;

    private String personalEmail;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    private LocalDate dateOfJoining;

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

    private String reportingManagerName;

    // =====================================================
    // Profile Photo
    // =====================================================

    private String profilePhoto;

    // =====================================================
    // Employee Status
    // =====================================================

    private Boolean active;

    // =====================================================
    // Default Constructor
    // =====================================================

    public EmployeeResponse() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public EmployeeResponse(
            Long id,
            String employeeCode,
            String firstName,
            String lastName,
            String officeEmail,
            String personalEmail,
            String phoneNumber,
            Gender gender,
            LocalDate dateOfBirth,
            LocalDate dateOfJoining,
            String departmentName,
            String designationName,
            EmploymentType employmentType,
            String reportingManagerCode,
            String reportingManagerName,
            String profilePhoto,
            Boolean active) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.officeEmail = officeEmail;
        this.personalEmail = personalEmail;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.dateOfJoining = dateOfJoining;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.employmentType = employmentType;
        this.reportingManagerCode = reportingManagerCode;
        this.reportingManagerName = reportingManagerName;
        this.profilePhoto = profilePhoto;
        this.active = active;
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

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
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

    public String getReportingManagerName() {
        return reportingManagerName;
    }

    public void setReportingManagerName(String reportingManagerName) {
        this.reportingManagerName = reportingManagerName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}