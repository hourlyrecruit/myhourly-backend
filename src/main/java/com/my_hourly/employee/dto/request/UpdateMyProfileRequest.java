package com.my_hourly.employee.dto.request;

import java.time.LocalDate;

import com.my_hourly.employee.enums.Gender;

public class UpdateMyProfileRequest {

    // =====================================================
    // Personal Details
    // =====================================================

    private String personalEmail;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    // =====================================================
    // Profile Photo
    // =====================================================

    private String profilePhoto;

    // =====================================================
    // Default Constructor
    // =====================================================

    public UpdateMyProfileRequest() {
    }

    // =====================================================
    // Parameterized Constructor
    // =====================================================

    public UpdateMyProfileRequest(
            String personalEmail,
            String phoneNumber,
            Gender gender,
            LocalDate dateOfBirth,
            String profilePhoto) {

        this.personalEmail = personalEmail;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.profilePhoto = profilePhoto;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

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

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

}