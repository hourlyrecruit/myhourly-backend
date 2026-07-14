package com.my_hourly.employee.dto.response;

public class EmployeeAddressResponse {

    // =====================================
    // Primary Key
    // =====================================

    private Long id;

    // =====================================
    // Employee Details
    // =====================================

    private String employeeCode;

    // =====================================
    // Address Details
    // =====================================

    private String currentAddress;

    private String permanentAddress;

    private String city;

    private String state;

    private String country;

    private String pincode;

    // =====================================
    // Default Constructor
    // =====================================

    public EmployeeAddressResponse() {

    }

    // =====================================
    // Parameterized Constructor
    // =====================================

    public EmployeeAddressResponse(Long id,
                                   String employeeCode,
                                   String currentAddress,
                                   String permanentAddress,
                                   String city,
                                   String state,
                                   String country,
                                   String pincode) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.currentAddress = currentAddress;
        this.permanentAddress = permanentAddress;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }

    // =====================================
    // Getters & Setters
    // =====================================

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

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}