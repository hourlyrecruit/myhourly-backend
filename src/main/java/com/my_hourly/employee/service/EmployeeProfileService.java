package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.EmployeeProfileRequest;
import com.my_hourly.employee.dto.response.EmployeeProfileResponse;

public interface EmployeeProfileService {

    
    EmployeeProfileResponse createEmployeeProfile(EmployeeProfileRequest request);

        EmployeeProfileResponse updateEmployeeProfile(
            String employeeCode,
            EmployeeProfileRequest request);

    
        
    EmployeeProfileResponse getEmployeeProfileByEmployeeCode(
            String employeeCode);

    
    List<EmployeeProfileResponse> getAllEmployeeProfiles();

    
    
    void deleteEmployeeProfile(String employeeCode);

}