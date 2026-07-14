package com.my_hourly.employee.service;

import java.util.List;

import com.my_hourly.employee.dto.request.EmployeeRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;

public interface EmployeeService {

    
	
    EmployeeResponse createEmployee(EmployeeRequest employeeRequest);

        
    EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest employeeRequest);

    
    
    EmployeeResponse getEmployeeById(Long employeeId);

    
    
    List<EmployeeResponse> getAllEmployees();

    
    
    void deleteEmployee(Long employeeId);

}