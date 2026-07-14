package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.EducationRequest;
import com.my_hourly.employee.dto.response.EducationResponse;
import com.my_hourly.employee.entity.Education;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EducationRepository;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EducationService;

@Service
public class EducationServiceImpl implements EducationService {

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

        @Override
    public EducationResponse createEducation(EducationRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        if (educationRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Education Record Already Exists");
        }

        Education education = new Education();

        education.setEmployee(employee);
        education.setEmployeeCode(request.getEmployeeCode());
        education.setQualification(request.getQualification());
        education.setSpecialization(request.getSpecialization());
        education.setCollegeName(request.getCollegeName());
        education.setUniversityName(request.getUniversityName());
        education.setPercentage(request.getPercentage());
        education.setCgpa(request.getCgpa());
        education.setPassingYear(request.getPassingYear());

        Education savedEducation = educationRepository.save(education);

        return mapToResponse(savedEducation);
    }

   

    @Override
    public EducationResponse updateEducation(
            String employeeCode,
            EducationRequest request) {

        Education education = educationRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Education Record Not Found"));

        education.setQualification(request.getQualification());
        education.setSpecialization(request.getSpecialization());
        education.setCollegeName(request.getCollegeName());
        education.setUniversityName(request.getUniversityName());
        education.setPercentage(request.getPercentage());
        education.setCgpa(request.getCgpa());
        education.setPassingYear(request.getPassingYear());

        Education updatedEducation = educationRepository.save(education);

        return mapToResponse(updatedEducation);
    }

    
    @Override
    public EducationResponse getEducationByEmployeeCode(String employeeCode) {

        Education education = educationRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Education Record Not Found"));

        return mapToResponse(education);
    }

   

    @Override
    public List<EducationResponse> getAllEducations() {

        List<Education> educations = educationRepository.findAll();

        List<EducationResponse> responseList = new ArrayList<>();

        for (Education education : educations) {
            responseList.add(mapToResponse(education));
        }

        return responseList;
    }

    

    @Override
    public void deleteEducation(String employeeCode) {

        Education education = educationRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Education Record Not Found"));

        educationRepository.delete(education);
    }

    
    private EducationResponse mapToResponse(Education education) {

        EducationResponse response = new EducationResponse();

        response.setId(education.getId());
        response.setEmployeeCode(education.getEmployeeCode());
        response.setQualification(education.getQualification());
        response.setSpecialization(education.getSpecialization());
        response.setCollegeName(education.getCollegeName());
        response.setUniversityName(education.getUniversityName());
        response.setPercentage(education.getPercentage());
        response.setCgpa(education.getCgpa());
        response.setPassingYear(education.getPassingYear());

        return response;
    }
}