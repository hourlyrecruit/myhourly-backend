package com.my_hourly.lookup.mapper;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.lookup.api.response.LookupResponse;
import com.my_hourly.lookup.api.response.ReportingManagerLookupResponse;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import org.springframework.stereotype.Component;

@Component
public class LookupMapper {

    public LookupResponse toResponse(Department department) {

        return LookupResponse.builder()
                .id(department.getId())
                .name(department.getDepartmentName())
                .build();
    }

    public LookupResponse toResponse(Designation designation) {

        return LookupResponse.builder()
                .id(designation.getId())
                .name(designation.getDesignationName())
                .build();
    }

    public LookupResponse toResponse(JobTitle jobTitle) {

        return LookupResponse.builder()
                .id(jobTitle.getId())
                .name(jobTitle.getJobTitle())
                .build();
    }

    public ReportingManagerLookupResponse toResponse(Employee employee) {

        return ReportingManagerLookupResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .name(employee.getFirstName() + " " +
                        (employee.getLastName() == null ? "" : employee.getLastName()))
                .build();
    }
}
