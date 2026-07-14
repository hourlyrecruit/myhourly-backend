package com.my_hourly.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.my_hourly.employee.dto.request.EmployeeAddressRequest;
import com.my_hourly.employee.dto.response.EmployeeAddressResponse;
import com.my_hourly.employee.service.EmployeeAddressService;

@RestController
@RequestMapping("/api/employee-addresses")
@CrossOrigin(origins = "*")
public class EmployeeAddressController {

    @Autowired
    private EmployeeAddressService employeeAddressService;

    
    @PostMapping
    public ResponseEntity<EmployeeAddressResponse> createEmployeeAddress(
            @RequestBody EmployeeAddressRequest request) {

        EmployeeAddressResponse response =
                employeeAddressService.createEmployeeAddress(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    

    @GetMapping
    public ResponseEntity<List<EmployeeAddressResponse>> getAllEmployeeAddresses() {

        List<EmployeeAddressResponse> response =
                employeeAddressService.getAllEmployeeAddresses();

        return ResponseEntity.ok(response);
    }

  

    @GetMapping("/{employeeCode}")
    public ResponseEntity<EmployeeAddressResponse> getEmployeeAddressByEmployeeCode(
            @PathVariable String employeeCode) {

        EmployeeAddressResponse response =
                employeeAddressService.getEmployeeAddressByEmployeeCode(employeeCode);

        return ResponseEntity.ok(response);
    }

    

    @PutMapping("/{employeeCode}")
    public ResponseEntity<EmployeeAddressResponse> updateEmployeeAddress(
            @PathVariable String employeeCode,
            @RequestBody EmployeeAddressRequest request) {

        EmployeeAddressResponse response =
                employeeAddressService.updateEmployeeAddress(employeeCode, request);

        return ResponseEntity.ok(response);
    }

    

    @DeleteMapping("/{employeeCode}")
    public ResponseEntity<String> deleteEmployeeAddress(
            @PathVariable String employeeCode) {

        employeeAddressService.deleteEmployeeAddress(employeeCode);

        return ResponseEntity.ok("Employee Address Deleted Successfully");
    }

}