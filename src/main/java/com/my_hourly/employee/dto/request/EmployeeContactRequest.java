package com.my_hourly.employee.dto.request;

public class EmployeeContactRequest {

    
    private String employeeCode;

    
    private String mobileNumber;

    private String officeEmail;

    private String personalEmail;

    

    public EmployeeContactRequest() {

    }

    

    public EmployeeContactRequest(String employeeCode,
                                  String mobileNumber,
                                  String officeEmail,
                                  String personalEmail) {

        this.employeeCode = employeeCode;
        this.mobileNumber = mobileNumber;
        this.officeEmail = officeEmail;
        this.personalEmail = personalEmail;
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