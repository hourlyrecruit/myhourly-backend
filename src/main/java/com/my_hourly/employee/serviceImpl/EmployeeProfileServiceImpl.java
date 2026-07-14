package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.EmployeeProfileRequest;
import com.my_hourly.employee.dto.response.EmployeeProfileResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.entity.EmployeeProfile;
import com.my_hourly.employee.repository.EmployeeProfileRepository;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeProfileService;

@Service
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Override
    public EmployeeProfileResponse createEmployeeProfile(EmployeeProfileRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        if (employeeProfileRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee Profile Already Exists");
        }

        EmployeeProfile profile = new EmployeeProfile();

        profile.setEmployee(employee);
        profile.setEmployeeCode(request.getEmployeeCode());
        profile.setProfilePhoto(request.getProfilePhoto());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setMaritalStatus(request.getMaritalStatus());
        profile.setNationality(request.getNationality());
        profile.setAadhaarNumber(request.getAadhaarNumber());
        profile.setPanNumber(request.getPanNumber());
        profile.setPassportNumber(request.getPassportNumber());

        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }



    @Override
    public EmployeeProfileResponse updateEmployeeProfile(
            String employeeCode,
            EmployeeProfileRequest request) {

        EmployeeProfile profile = employeeProfileRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Profile Not Found"));

        profile.setProfilePhoto(request.getProfilePhoto());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setMaritalStatus(request.getMaritalStatus());
        profile.setNationality(request.getNationality());
        profile.setAadhaarNumber(request.getAadhaarNumber());
        profile.setPanNumber(request.getPanNumber());
        profile.setPassportNumber(request.getPassportNumber());

        EmployeeProfile updatedProfile = employeeProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    

    @Override
    public EmployeeProfileResponse getEmployeeProfileByEmployeeCode(
            String employeeCode) {

        EmployeeProfile profile = employeeProfileRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Profile Not Found"));

        return mapToResponse(profile);
    }

    
    @Override
    public List<EmployeeProfileResponse> getAllEmployeeProfiles() {

        List<EmployeeProfile> profiles = employeeProfileRepository.findAll();

        List<EmployeeProfileResponse> responseList = new ArrayList<>();

        for (EmployeeProfile profile : profiles) {
            responseList.add(mapToResponse(profile));
        }

        return responseList;
    }

    

    @Override
    public void deleteEmployeeProfile(String employeeCode) {

        EmployeeProfile profile = employeeProfileRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Profile Not Found"));

        employeeProfileRepository.delete(profile);
    }

    

    private EmployeeProfileResponse mapToResponse(EmployeeProfile profile) {

        EmployeeProfileResponse response = new EmployeeProfileResponse();

        response.setId(profile.getId());
        response.setEmployeeCode(profile.getEmployeeCode());
        response.setProfilePhoto(profile.getProfilePhoto());
        response.setBloodGroup(profile.getBloodGroup());
        response.setMaritalStatus(profile.getMaritalStatus());
        response.setNationality(profile.getNationality());
        response.setAadhaarNumber(profile.getAadhaarNumber());
        response.setPanNumber(profile.getPanNumber());
        response.setPassportNumber(profile.getPassportNumber());

        return response;
    }
}