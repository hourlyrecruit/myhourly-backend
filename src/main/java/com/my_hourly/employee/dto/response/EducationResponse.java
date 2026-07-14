package com.my_hourly.employee.dto.response;

public class EducationResponse {

    

    private Long id;

    

    private String employeeCode;

    
   

    private String qualification;

    private String specialization;

    private String collegeName;

    private String universityName;

    private Double percentage;

    private Double cgpa;

    private Integer passingYear;

    
    public EducationResponse() {

    }

    

    public EducationResponse(Long id,
                             String employeeCode,
                             String qualification,
                             String specialization,
                             String collegeName,
                             String universityName,
                             Double percentage,
                             Double cgpa,
                             Integer passingYear) {

        this.id = id;
        this.employeeCode = employeeCode;
        this.qualification = qualification;
        this.specialization = specialization;
        this.collegeName = collegeName;
        this.universityName = universityName;
        this.percentage = percentage;
        this.cgpa = cgpa;
        this.passingYear = passingYear;
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

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String universityName) {
        this.universityName = universityName;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getPassingYear() {
        return passingYear;
    }

    public void setPassingYear(Integer passingYear) {
        this.passingYear = passingYear;
    }

}