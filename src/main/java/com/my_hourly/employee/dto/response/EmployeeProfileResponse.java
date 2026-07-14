package com.my_hourly.employee.dto.response;

public class EmployeeProfileResponse {

    

    private Long id;

    
    private String employeeCode;

    
    private String profilePhoto;

    private String bloodGroup;

    private String maritalStatus;

    private String nationality;

    

    private String aadhaarNumber;

    private String panNumber;

    private String passportNumber;

   

    public EmployeeProfileResponse() {

    }

    

    public EmployeeProfileResponse(Long id,
            String employeeCode,
            String profilePhoto,
            String bloodGroup,
            String maritalStatus,
            String nationality,
            String aadhaarNumber,
            String panNumber,
            String passportNumber) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.profilePhoto = profilePhoto;
        this.bloodGroup = bloodGroup;
        this.maritalStatus = maritalStatus;
        this.nationality = nationality;
        this.aadhaarNumber = aadhaarNumber;
        this.panNumber = panNumber;
        this.passportNumber = passportNumber;
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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

}