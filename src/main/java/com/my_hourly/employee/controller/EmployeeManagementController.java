package com.my_hourly.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.EmployeeProfileRequest;
import com.my_hourly.employee.dto.request.EmployeeRequest;
import com.my_hourly.employee.dto.response.EmployeeProfileResponse;
import com.my_hourly.employee.dto.response.EmployeeResponse;
import com.my_hourly.employee.service.EmployeeProfileService;
import com.my_hourly.employee.service.EmployeeService;


import com.my_hourly.employee.dto.request.EmployeeContactRequest;
import com.my_hourly.employee.dto.response.EmployeeContactResponse;
import com.my_hourly.employee.service.EmployeeContactService;



@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeManagementController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeProfileService employeeProfileService;
    
    @Autowired
    private EmployeeContactService employeeContactService;

    

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody EmployeeRequest request) {

        return new ResponseEntity<>(
                employeeService.createEmployee(request),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(employeeId));
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long employeeId,
            @RequestBody EmployeeRequest request) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(employeeId, request));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long employeeId) {

        employeeService.deleteEmployee(employeeId);

        return ResponseEntity.ok("Employee Deleted Successfully");
    }

    
    @PostMapping("/profile")
    public ResponseEntity<EmployeeProfileResponse> createEmployeeProfile(
            @RequestBody EmployeeProfileRequest request) {

        return new ResponseEntity<>(
                employeeProfileService.createEmployeeProfile(request),
                HttpStatus.CREATED);
    }

    
    
    @GetMapping("/profile")
    public ResponseEntity<List<EmployeeProfileResponse>> getAllEmployeeProfiles() {

        return ResponseEntity.ok(
                employeeProfileService.getAllEmployeeProfiles());
    }

   
    
    @GetMapping("/profile/{employeeCode}")
    public ResponseEntity<EmployeeProfileResponse> getEmployeeProfileByEmployeeCode(
            @PathVariable String employeeCode) {

        return ResponseEntity.ok(
                employeeProfileService.getEmployeeProfileByEmployeeCode(employeeCode));
    }

    
    
    @PutMapping("/profile/{employeeCode}")
    public ResponseEntity<EmployeeProfileResponse> updateEmployeeProfile(
            @PathVariable String employeeCode,
            @RequestBody EmployeeProfileRequest request) {

        return ResponseEntity.ok(
                employeeProfileService.updateEmployeeProfile(employeeCode, request));
    }

    
    @DeleteMapping("/profile/{employeeCode}")
    public ResponseEntity<String> deleteEmployeeProfile(
            @PathVariable String employeeCode) {

        employeeProfileService.deleteEmployeeProfile(employeeCode);

        return ResponseEntity.ok("Employee Profile Deleted Successfully");
    }

 
    
 @PostMapping("/contact")
 public ResponseEntity<EmployeeContactResponse> createEmployeeContact(
         @RequestBody EmployeeContactRequest request) {

     return new ResponseEntity<>(
             employeeContactService.createEmployeeContact(request),
             HttpStatus.CREATED);
 }

 
 
 
 @GetMapping("/contact")
 public ResponseEntity<List<EmployeeContactResponse>> getAllEmployeeContacts() {

     return ResponseEntity.ok(
             employeeContactService.getAllEmployeeContacts());
 }

 
 
 
 @GetMapping("/contact/{employeeCode}")
 public ResponseEntity<EmployeeContactResponse> getEmployeeContactByEmployeeCode(
         @PathVariable String employeeCode) {

     return ResponseEntity.ok(
             employeeContactService.getEmployeeContactByEmployeeCode(employeeCode));
 }

 
 
 @PutMapping("/contact/{employeeCode}")
 public ResponseEntity<EmployeeContactResponse> updateEmployeeContact(
         @PathVariable String employeeCode,
         @RequestBody EmployeeContactRequest request) {

     return ResponseEntity.ok(
             employeeContactService.updateEmployeeContact(employeeCode, request));
 }

 
 @DeleteMapping("/contact/{employeeCode}")
 public ResponseEntity<String> deleteEmployeeContact(
         @PathVariable String employeeCode) {

     employeeContactService.deleteEmployeeContact(employeeCode);

     return ResponseEntity.ok("Employee Contact Deleted Successfully");
 }
    
    
    
    
}