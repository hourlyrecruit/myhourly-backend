package com.my_hourly.employee.dto.response;

import java.time.LocalDate;

public class EmployeeResponse {

    
    private Long id;

    
    private String employeeCode;

   
    private String name;

    
    private String email;

   
    private String mobileNumber;

    
    private LocalDate dateOfBirth;

   
    private String gender;

   
    private LocalDate joiningDate;

  
    private String departmentName;

   
    private String designationName;

    
    private String employmentType;

    
    private String status;

   
    public EmployeeResponse() {
    }

   
    public EmployeeResponse(Long id, String employeeCode, String name,
            String email, String mobileNumber, LocalDate dateOfBirth,
            String gender, LocalDate joiningDate,
            String departmentName, String designationName,
            String employmentType, String status) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.joiningDate = joiningDate;
        this.departmentName = departmentName;
        this.designationName = designationName;
        this.employmentType = employmentType;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
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

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}