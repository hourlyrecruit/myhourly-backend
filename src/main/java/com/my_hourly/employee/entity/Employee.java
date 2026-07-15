package com.my_hourly.employee.entity;

import java.time.LocalDate;

import com.my_hourly.employee.enums.EmploymentType;
import com.my_hourly.employee.enums.Gender;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Employee Details
    // =====================================================

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "office_email", nullable = false, unique = true)
    private String officeEmail;

    @Column(name = "personal_email", unique = true)
    private String personalEmail;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    // =====================================================
    // Employment Details
    // =====================================================

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "designation_name")
    private String designationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

    // =====================================================
    // Reporting Manager
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    // =====================================================
    // Profile Photo
    // =====================================================

    @Column(name = "profile_photo")
    private String profilePhoto;

    // =====================================================
    // Authentication
    // =====================================================

    @Column(name = "password", nullable = false)
    private String password;

    // =====================================================
    // Employee Status
    // =====================================================

    @Column(name = "active")
    private Boolean active;

    // =====================================================
    // Constructors
    // =====================================================

    public Employee() {
    }

    public Employee(Long id, String employeeCode, String firstName, String lastName,
                    String officeEmail, String personalEmail, String phoneNumber,
                    Gender gender, LocalDate dateOfBirth, LocalDate dateOfJoining,
                    String departmentName, String designationName,
                    EmploymentType employmentType, Employee reportingManager,
                    String profilePhoto, String password, Boolean active) {

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
        this.reportingManager = reportingManager;
        this.profilePhoto = profilePhoto;
        this.password = password;
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

    public Employee getReportingManager() {
        return reportingManager;
    }

    public void setReportingManager(Employee reportingManager) {
        this.reportingManager = reportingManager;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}