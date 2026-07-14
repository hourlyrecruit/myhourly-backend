package com.my_hourly.employee.dto.response;

public class EmployeeContactResponse {

    
    private Long id;

    

    private String employeeCode;

   
    private String mobileNumber;

    private String officeEmail;

    private String personalEmail;

   

    public EmployeeContactResponse() {

    }

    

    public EmployeeContactResponse(Long id,
                                   String employeeCode,
                                   String mobileNumber,
                                   String officeEmail,
                                   String personalEmail) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.mobileNumber = mobileNumber;
        this.officeEmail = officeEmail;
        this.personalEmail = personalEmail;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

}