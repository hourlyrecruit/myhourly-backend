package com.my_hourly.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.EmployeeContactRequest;
import com.my_hourly.employee.dto.response.EmployeeContactResponse;
import com.my_hourly.employee.service.EmployeeContactService;



@RestController
@RequestMapping("/api/employee-contacts")
@CrossOrigin(origins = "*")
public class EmployeeContactController {

    @Autowired
    private EmployeeContactService employeeContactService;
    


    @PostMapping
    public ResponseEntity<EmployeeContactResponse> createEmployeeContact(
            @RequestBody EmployeeContactRequest request) {

        EmployeeContactResponse response =
                employeeContactService.createEmployeeContact(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    

    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeContactResponse> updateEmployeeContact(
            @PathVariable String employeeCode,
            @RequestBody EmployeeContactRequest request) {

        EmployeeContactResponse response =
                employeeContactService.updateEmployeeContact(employeeCode, request);

        return ResponseEntity.ok(response);
    }

    

    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeContactResponse> getEmployeeContactByEmployeeCode(
            @PathVariable String employeeCode) {

        EmployeeContactResponse response =
                employeeContactService.getEmployeeContactByEmployeeCode(employeeCode);

        return ResponseEntity.ok(response);
    }

    

    @GetMapping
    public ResponseEntity<List<EmployeeContactResponse>> getAllEmployeeContacts() {

        List<EmployeeContactResponse> response =
                employeeContactService.getAllEmployeeContacts();

        return ResponseEntity.ok(response);
    }

    

    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<String> deleteEmployeeContact(
            @PathVariable String employeeCode) {

        employeeContactService.deleteEmployeeContact(employeeCode);

        return ResponseEntity.ok("Employee Contact Deleted Successfully");
    }

}