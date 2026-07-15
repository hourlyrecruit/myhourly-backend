package com.my_hourly.employee.api.response;

import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.employee.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private Long userId;

    private String email;

    private String phoneNumber;

    private Gender gender;

    private LocalDate dateOfBirth;

    private LocalDate dateOfJoining;

    private EmploymentType employmentType;

    private Long departmentId;

    private String departmentName;

    private Long designationId;

    private String designationName;

    private Long jobTitleId;

    private String jobTitle;

    private Long reportingManagerId;

    private String reportingManagerName;

    private String profilePhoto;

    private boolean active;

}