package com.my_hourly.employee.service;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse create(CreateEmployeeRequest request, MultipartFile file);

    EmployeeResponse createUserProfileByAdmin(Long userId, CreateEmployeeRequest request, MultipartFile file);

    EmployeeResponse updateUserProfileByAdmin(Long userId, UpdateEmployeeRequest request);

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


    EmployeeResponse uploadProfilePhoto(MultipartFile file, Employee employee);

    EmployeeResponse updateProfilePhoto(MultipartFile file);

    //byte[] getProfilePhoto(Long employeeId);
}
