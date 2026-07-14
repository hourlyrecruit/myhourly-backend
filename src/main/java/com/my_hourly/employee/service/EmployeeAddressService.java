package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.EmployeeAddressRequest;
import com.my_hourly.employee.dto.response.EmployeeAddressResponse;

public interface EmployeeAddressService {

    

    EmployeeAddressResponse createEmployeeAddress(EmployeeAddressRequest request);

    

    EmployeeAddressResponse updateEmployeeAddress(
            String employeeCode,
            EmployeeAddressRequest request);

    

    EmployeeAddressResponse getEmployeeAddressByEmployeeCode(
            String employeeCode);

   

    List<EmployeeAddressResponse> getAllEmployeeAddresses();

    

    void deleteEmployeeAddress(String employeeCode);

}