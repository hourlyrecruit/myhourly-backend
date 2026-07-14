package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.EmployeeContactRequest;
import com.my_hourly.employee.dto.response.EmployeeContactResponse;

public interface EmployeeContactService {

    

    EmployeeContactResponse createEmployeeContact(EmployeeContactRequest request);

    

    EmployeeContactResponse updateEmployeeContact(
            String employeeCode,
            EmployeeContactRequest request);

    

    EmployeeContactResponse getEmployeeContactByEmployeeCode(
            String employeeCode);

    

    List<EmployeeContactResponse> getAllEmployeeContacts();

    

    void deleteEmployeeContact(String employeeCode);

}