package com.my_hourly.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.EducationRequest;
import com.my_hourly.employee.dto.response.EducationResponse;
import com.my_hourly.employee.service.EducationService;

@RestController
@RequestMapping("/api/educations")
@CrossOrigin(origins = "*")
public class EducationController {

    @Autowired
    private EducationService educationService;

    

    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(
            @RequestBody EducationRequest request) {

        EducationResponse response =
                educationService.createEducation(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    

    @GetMapping("/{employeeCode}")
    public ResponseEntity<EducationResponse> getEducationByEmployeeCode(
            @PathVariable String employeeCode) {

        EducationResponse response =
                educationService.getEducationByEmployeeCode(employeeCode);

        return ResponseEntity.ok(response);
    }

   

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducations() {

        List<EducationResponse> response =
                educationService.getAllEducations();

        return ResponseEntity.ok(response);
    }

    
    @PutMapping("/{employeeCode}")
    public ResponseEntity<EducationResponse> updateEducation(
            @PathVariable String employeeCode,
            @RequestBody EducationRequest request) {

        EducationResponse response =
                educationService.updateEducation(employeeCode, request);

        return ResponseEntity.ok(response);
    }

    

    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<String> deleteEducation(
            @PathVariable String employeeCode) {

        educationService.deleteEducation(employeeCode);

        return ResponseEntity.ok("Education Record Deleted Successfully");
    }

}