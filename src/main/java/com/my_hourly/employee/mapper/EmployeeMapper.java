package com.my_hourly.employee.mapper;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(
            CreateEmployeeRequest request,
            User user,
            Department department,
            Designation designation,
            JobTitle jobTitle,
            Employee reportingManager
    ) {

        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .user(user)
                .roleName(user.getRole())
                .email(user.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .dateOfJoining(request.getDateOfJoining())
                .employmentType(request.getEmploymentType())
                .department(department)
                .designation(designation)
                .jobTitle(jobTitle)
                .reportingManager(reportingManager)

                .active(true)
                .build();

    }

    public void updateEntity(
            Employee employee,
            UpdateEmployeeRequest request,
            Department department,
            Designation designation,
            JobTitle jobTitle,
            Employee reportingManager
    ) {

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        //employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setEmploymentType(request.getEmploymentType());
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setJobTitle(jobTitle);
        employee.setReportingManager(reportingManager);

    }

    public EmployeeResponse toResponse(Employee employee) {

        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getUser().getEmail())
                .userId(employee.getUser().getId())
                .phoneNumber(employee.getPhoneNumber())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .dateOfJoining(employee.getDateOfJoining())
                .employmentType(employee.getEmploymentType())

                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getDepartmentName())

                .designationId(employee.getDesignation().getId())
                .designationName(employee.getDesignation().getDesignationName())

                .jobTitleId(employee.getJobTitle().getId())
                .jobTitle(employee.getJobTitle().getJobTitle())

//                .reportingManagerId(
//                        employee.getReportingManager() != null
//                                ? employee.getReportingManager().getId()
//                                : null
//                )

                .reportingManagerName(
                        employee.getReportingManager() != null
                                ? employee.getReportingManager().getFirstName()
                                  + " "
                                  + employee.getReportingManager().getLastName()
                                : null
                )

                .hasProfilePhoto(employee.getProfilePhoto() != null)

                .profilePhotoUrl(
                        employee.getProfilePhoto() != null
                                ? "/api/v1/employees/" + employee.getId() + "/profile-photo"
                                : null
                )
                .active(employee.isActive())
                .build();

    }

    public EmployeeDropdownResponse toDropdownResponse(Employee employee) {

        return EmployeeDropdownResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .employeeName(
                        employee.getFirstName() +
                                (employee.getLastName() != null
                                        ? " " + employee.getLastName()
                                        : "")
                )
                .build();

    }

}
