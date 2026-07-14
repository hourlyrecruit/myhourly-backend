package com.my_hourly.organization.mapper;

import com.my_hourly.organization.api.request.CreateDepartmentRequest;
import com.my_hourly.organization.api.request.UpdateDepartmentRequest;
import com.my_hourly.organization.api.response.DepartmentResponse;
import com.my_hourly.organization.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(CreateDepartmentRequest request) {

        return Department.builder()
                .departmentName(request.getDepartmentName())
                .description(request.getDescription())
                .active(true)
                .build();
    }

    public void updateEntity(
            Department department,
            UpdateDepartmentRequest request
            ) {

        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());
        department.setActive(request.isActive());

    }

    public DepartmentResponse toResponse(Department department) {

        return DepartmentResponse.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .active(department.isActive())
                .build();

    }

}
