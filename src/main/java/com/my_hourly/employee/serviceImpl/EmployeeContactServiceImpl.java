package com.my_hourly.employee.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.my_hourly.employee.dto.request.EmployeeContactRequest;
import com.my_hourly.employee.dto.response.EmployeeContactResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.entity.EmployeeContact;
import com.my_hourly.employee.repository.EmployeeContactRepository;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeContactService;

@Service
public class EmployeeContactServiceImpl implements EmployeeContactService {

    @Autowired
    private EmployeeContactRepository employeeContactRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    

    @Override
    public EmployeeContactResponse createEmployeeContact(EmployeeContactRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        if (employeeContactRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new RuntimeException("Employee Contact Already Exists");
        }

        EmployeeContact contact = new EmployeeContact();

        contact.setEmployee(employee);
        contact.setEmployeeCode(request.getEmployeeCode());
        contact.setMobileNumber(request.getMobileNumber());
        contact.setOfficeEmail(request.getOfficeEmail());
        contact.setPersonalEmail(request.getPersonalEmail());

        EmployeeContact savedContact = employeeContactRepository.save(contact);

        return mapToResponse(savedContact);
    }

    
    @Override
    public EmployeeContactResponse updateEmployeeContact(
            String employeeCode,
            EmployeeContactRequest request) {

        EmployeeContact contact = employeeContactRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Contact Not Found"));

        contact.setMobileNumber(request.getMobileNumber());
        contact.setOfficeEmail(request.getOfficeEmail());
        contact.setPersonalEmail(request.getPersonalEmail());

        EmployeeContact updatedContact = employeeContactRepository.save(contact);

        return mapToResponse(updatedContact);
    }

    
    @Override
    public EmployeeContactResponse getEmployeeContactByEmployeeCode(
            String employeeCode) {

        EmployeeContact contact = employeeContactRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Contact Not Found"));

        return mapToResponse(contact);
    }

    
    @Override
    public List<EmployeeContactResponse> getAllEmployeeContacts() {

        List<EmployeeContact> contacts = employeeContactRepository.findAll();

        List<EmployeeContactResponse> responseList = new ArrayList<>();

        for (EmployeeContact contact : contacts) {
            responseList.add(mapToResponse(contact));
        }

        return responseList;
    }

    
    @Override
    public void deleteEmployeeContact(String employeeCode) {

        EmployeeContact contact = employeeContactRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee Contact Not Found"));

        employeeContactRepository.delete(contact);
    }

    

    private EmployeeContactResponse mapToResponse(EmployeeContact contact) {

        EmployeeContactResponse response = new EmployeeContactResponse();

        response.setId(contact.getId());
        response.setEmployeeCode(contact.getEmployeeCode());
        response.setMobileNumber(contact.getMobileNumber());
        response.setOfficeEmail(contact.getOfficeEmail());
        response.setPersonalEmail(contact.getPersonalEmail());

        return response;
    }
}