package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.EmployeeAddressRequest;
import com.my_hourly.employee.dto.response.EmployeeAddressResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.entity.EmployeeAddress;
import com.my_hourly.employee.repository.EmployeeAddressRepository;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeAddressService;

@Service
public class EmployeeAddressServiceImpl implements EmployeeAddressService {

    @Autowired
    private EmployeeAddressRepository employeeAddressRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    

    @Override
    public EmployeeAddressResponse createEmployeeAddress(EmployeeAddressRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        if (employeeAddressRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee Address Already Exists");
        }

        EmployeeAddress address = new EmployeeAddress();

        address.setEmployee(employee);
        address.setEmployeeCode(request.getEmployeeCode());
        address.setCurrentAddress(request.getCurrentAddress());
        address.setPermanentAddress(request.getPermanentAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPincode(request.getPincode());

        EmployeeAddress savedAddress = employeeAddressRepository.save(address);

        return mapToResponse(savedAddress);
    }

    
    @Override
    public EmployeeAddressResponse updateEmployeeAddress(
            String employeeCode,
            EmployeeAddressRequest request) {

        EmployeeAddress address = employeeAddressRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Address Not Found"));

        address.setCurrentAddress(request.getCurrentAddress());
        address.setPermanentAddress(request.getPermanentAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setCountry(request.getCountry());
        address.setPincode(request.getPincode());

        EmployeeAddress updatedAddress = employeeAddressRepository.save(address);

        return mapToResponse(updatedAddress);
    }

    
    @Override
    public EmployeeAddressResponse getEmployeeAddressByEmployeeCode(
            String employeeCode) {

        EmployeeAddress address = employeeAddressRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Address Not Found"));

        return mapToResponse(address);
    }

   

    @Override
    public List<EmployeeAddressResponse> getAllEmployeeAddresses() {

        List<EmployeeAddress> addresses = employeeAddressRepository.findAll();

        List<EmployeeAddressResponse> responseList = new ArrayList<>();

        for (EmployeeAddress address : addresses) {

            responseList.add(mapToResponse(address));
        }

        return responseList;
    }

   

    @Override
    public void deleteEmployeeAddress(String employeeCode) {

        EmployeeAddress address = employeeAddressRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Address Not Found"));

        employeeAddressRepository.delete(address);
    }

    

    private EmployeeAddressResponse mapToResponse(EmployeeAddress address) {

        EmployeeAddressResponse response = new EmployeeAddressResponse();

        response.setId(address.getId());
        response.setEmployeeCode(address.getEmployeeCode());
        response.setCurrentAddress(address.getCurrentAddress());
        response.setPermanentAddress(address.getPermanentAddress());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPincode(address.getPincode());

        return response;
    }
}