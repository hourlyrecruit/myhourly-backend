package com.my_hourly.employee.service;

import com.my_hourly.common.response.PageResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request);

    EmployeeResponse update(UpdateEmployeeRequest request);

    EmployeeResponse getById(Long id);

    PageResponse<EmployeeResponse> getAll(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection
    );

    void changeStatus(Long id, boolean active);

    //void delete(Long id);

    List<EmployeeDropdownResponse> getDropdown();

    EmployeeResponse getMyProfile();

    Employee getCurrentEmployee();
}
