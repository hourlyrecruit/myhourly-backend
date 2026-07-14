package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.EmployeeRequest;
import com.my_hourly.employee.dto.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;

@Service
public class EmployeeserviceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee Code Already Exists");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email Already Exists");
        }

        if (employeeRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new RuntimeException("Mobile Number Already Exists");
        }

        Employee employee = new Employee();

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setMobileNumber(request.getMobileNumber());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setDepartmentName(request.getDepartmentName());
        employee.setDesignationName(request.getDesignationName());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setStatus(request.getStatus());

        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    

    @Override
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setMobileNumber(request.getMobileNumber());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setDepartmentName(request.getDepartmentName());
        employee.setDesignationName(request.getDesignationName());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setStatus(request.getStatus());

        Employee updatedEmployee = employeeRepository.save(employee);

        return mapToResponse(updatedEmployee);
    }

    

    @Override
    public EmployeeResponse getEmployeeById(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        return mapToResponse(employee);
    }

   

    @Override
    public List<EmployeeResponse> getAllEmployees() {

        List<Employee> employeeList = employeeRepository.findAll();

        List<EmployeeResponse> responseList = new ArrayList<>();

        for (Employee employee : employeeList) {

            responseList.add(mapToResponse(employee));

        }

        return responseList;
    }

   

    @Override
    public void deleteEmployee(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        employeeRepository.delete(employee);

    }

    

    private EmployeeResponse mapToResponse(Employee employee) {

        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setMobileNumber(employee.getMobileNumber());
        response.setDateOfBirth(employee.getDateOfBirth());
        response.setGender(employee.getGender());
        response.setJoiningDate(employee.getJoiningDate());
        response.setDepartmentName(employee.getDepartmentName());
        response.setDesignationName(employee.getDesignationName());
        response.setEmploymentType(employee.getEmploymentType());
        response.setStatus(employee.getStatus());

        return response;
    }

}