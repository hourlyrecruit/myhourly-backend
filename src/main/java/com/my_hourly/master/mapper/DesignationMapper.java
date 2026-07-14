package com.my_hourly.master.mapper;

import com.my_hourly.master.api.request.CreateDesignationRequest;
import com.my_hourly.master.api.request.UpdateDesignationRequest;
import com.my_hourly.master.api.response.DesignationResponse;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import org.springframework.stereotype.Component;

@Component
public class DesignationMapper {

    public Designation toEntity(
            CreateDesignationRequest request,
            Department department
    ) {

        return Designation.builder()
                .designationName(request.getDesignationName())
                .department(department)
                .description(request.getDescription())
                .active(true)
                .build();

    }

    public void updateEntity(
            Designation designation,
            UpdateDesignationRequest request,
            Department department
    ) {

        designation.setDesignationName(request.getDesignationName());
        designation.setDepartment(department);
        designation.setDescription(request.getDescription());
        designation.setActive(request.isActive());

    }

    public DesignationResponse toResponse(
            Designation designation
    ) {

        return DesignationResponse.builder()
                .id(designation.getId())
                .designationCode(designation.getDesignationCode())
                .designationName(designation.getDesignationName())
                .departmentId(designation.getDepartment().getId())
                .departmentName(designation.getDepartment().getDepartmentName())
                .description(designation.getDescription())
                .active(designation.isActive())
                .build();

    }

}
