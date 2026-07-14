package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.EducationRequest;
import com.my_hourly.employee.dto.response.EducationResponse;

public interface EducationService {

    

    EducationResponse createEducation(EducationRequest request);

    

    EducationResponse updateEducation(
            String employeeCode,
            EducationRequest request);

  

    EducationResponse getEducationByEmployeeCode(
            String employeeCode);

    

    List<EducationResponse> getAllEducations();

   

    void deleteEducation(String employeeCode);

}