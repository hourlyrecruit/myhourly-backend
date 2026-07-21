package com.my_hourly.employee.api.request;

import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.employee.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class UpdateEmployeeRequest {

    @NotBlank(message = "First name is required.")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number.")
    private String phoneNumber;

    @NotNull(message = "Gender is required.")
    private Gender gender;

    @NotNull(message = "Date of birth is required.")
    private LocalDate dateOfBirth;

    @NotNull(message = "Date of joining is required.")
    private LocalDate dateOfJoining;

    @NotNull(message = "Employment type is required.")
    private EmploymentType employmentType;

    @NotNull(message = "Department is required.")
    private Long departmentId;

    @NotNull(message = "Designation is required.")
    private Long designationId;

    @NotNull(message = "Job title is required.")
    private Long jobTitleId;

    private Long reportingManagerId;

}