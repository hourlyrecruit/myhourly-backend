package com.my_hourly.employee.service.impl;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.employee.api.request.CreateEmployeeRequest;
import com.my_hourly.employee.api.request.UpdateEmployeeRequest;
import com.my_hourly.employee.api.response.EmployeeDropdownResponse;
import com.my_hourly.employee.api.response.EmployeeResponse;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.mapper.EmployeeMapper;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;

import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleRepository jobTitleRepository;
    private final EmployeeMapper employeeMapper;
    private final LeaveBalanceService leaveBalanceService;
    private final UserRepository userRepository;

    private String generateEmployeeCode() {

        Optional<Employee> lastEmployee =
                employeeRepository.findTopByOrderByEmployeeCodeDesc();

        if (lastEmployee.isEmpty()) {
            return "EMP0001";
        }

        String lastCode = lastEmployee.get().getEmployeeCode();

        int number = Integer.parseInt(lastCode.substring(3));

        return String.format("EMP%04d", number + 1);
    }

    private Department getDepartment(Long departmentId) {

        return departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private Designation getDesignation(Long designationId) {

        return designationRepository.findById(designationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private JobTitle getJobTitle(Long jobTitleId) {

        return jobTitleRepository.findById(jobTitleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job title not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private Employee getReportingManager(Long reportingManagerId) {

        if (reportingManagerId == null || reportingManagerId == 0) {
            return null;
        }

        return employeeRepository.findById(reportingManagerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reporting manager not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private void validateHierarchy(
            Long departmentId,
            Long designationId,
            Long jobTitleId
    ) {

        if (!designationRepository.existsByIdAndDepartmentId(
                designationId,
                departmentId)) {

            throw new ValidationException(
                    "Selected designation does not belong to the selected department.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        if (!jobTitleRepository.existsByIdAndDesignationId(
                jobTitleId,
                designationId)) {

            throw new ValidationException(
                    "Selected job title does not belong to the selected designation.",
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }

    @Override
    public EmployeeResponse create(CreateEmployeeRequest request, MultipartFile file) {

        User user = SecurityUtils.getCurrentUser();

        if (employeeRepository.existsByEmail(user.getEmail())) {

            throw new ValidationException(
                    "User Profile already exists. You can update it",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        validateHierarchy(
                request.getDepartmentId(),
                request.getDesignationId(),
                request.getJobTitleId()
        );

        Department department =
                getDepartment(request.getDepartmentId());

        Designation designation =
                getDesignation(request.getDesignationId());

        JobTitle jobTitle =
                getJobTitle(request.getJobTitleId());

        Employee reportingManager =
                getReportingManager(request.getReportingManagerId());



        Employee employee = employeeMapper.toEntity(
                request,
                user,
                department,
                designation,
                jobTitle,
                reportingManager
        );

        employee.setEmployeeCode(generateEmployeeCode());

        Employee savedEmployee =
                employeeRepository.save(employee);

        //leaveBalanceService.initializeEmployeeLeaveBalance(savedEmployee);

        if (file == null || file.isEmpty()) {
            return employeeMapper.toResponse(savedEmployee);
        }else {
            return uploadProfilePhoto(file);
        }

    }




    //===================================================================================================


    @Override
    public EmployeeResponse createUserProfileByAdmin(Long userId, CreateEmployeeRequest request, MultipartFile file) {

       // User user = SecurityUtils.getCurrentUser();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId, ErrorCode.USER_NOT_FOUND));

        if (employeeRepository.existsByEmail(user.getEmail())) {

            throw new ValidationException(
                    "User Profile already exists. You can update it",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        validateHierarchy(
                request.getDepartmentId(),
                request.getDesignationId(),
                request.getJobTitleId()
        );

        Department department =
                getDepartment(request.getDepartmentId());

        Designation designation =
                getDesignation(request.getDesignationId());

        JobTitle jobTitle =
                getJobTitle(request.getJobTitleId());

        Employee reportingManager =
                getReportingManager(request.getReportingManagerId());



        Employee employee = employeeMapper.toEntity(
                request,
                user,
                department,
                designation,
                jobTitle,
                reportingManager
        );

        employee.setEmployeeCode(generateEmployeeCode());

        Employee savedEmployee =
                employeeRepository.save(employee);

        //leaveBalanceService.initializeEmployeeLeaveBalance(savedEmployee);

        if (file == null || file.isEmpty()) {
            return employeeMapper.toResponse(savedEmployee);
        }else {
            return uploadProfilePhoto(file);
        }

    }


    @Override
    public EmployeeResponse updateUserProfileByAdmin(Long userId, UpdateEmployeeRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id :" + userId, ErrorCode.USER_NOT_FOUND));
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not create for user: " + user.getUsername(), ErrorCode.RESOURCE_NOT_FOUND));


        validateHierarchy(
                request.getDepartmentId(),
                request.getDesignationId(),
                request.getJobTitleId()
        );

        Department department = getDepartment(request.getDepartmentId());

        Designation designation = getDesignation(request.getDesignationId());

        JobTitle jobTitle = getJobTitle(request.getJobTitleId());

        Employee reportingManager =
                getReportingManager(request.getReportingManagerId());

        employeeMapper.updateEntity(
                employee,
                request,
                department,
                designation,
                jobTitle,
                reportingManager
        );

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }


    //============================================================================================


    @Override
    public EmployeeResponse update(UpdateEmployeeRequest request) {

        Employee employee = getCurrentEmployee();

//        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
//                && employeeRepository.existsByEmail(request.getEmail())) {
//
//            throw new ValidationException(
//                    "Email already exists.",
//                    ErrorCode.VALIDATION_FAILED
//            );
//        }

        validateHierarchy(
                request.getDepartmentId(),
                request.getDesignationId(),
                request.getJobTitleId()
        );

        Department department = getDepartment(request.getDepartmentId());

        Designation designation = getDesignation(request.getDesignationId());

        JobTitle jobTitle = getJobTitle(request.getJobTitleId());

        Employee reportingManager =
                getReportingManager(request.getReportingManagerId());

        employeeMapper.updateEntity(
                employee,
                request,
                department,
                designation,
                jobTitle,
                reportingManager
        );

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> getAll(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection
    ) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> employees;

        if (search == null || search.isBlank()) {

            employees = employeeRepository.findAll(pageable);

        } else {

            employees = employeeRepository
                    .findByEmployeeCodeContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            search,
                            search,
                            search,
                            search,
                            pageable
                    );
        }

        List<EmployeeResponse> responses = employees.getContent()
                .stream()
                .map(employeeMapper::toResponse)
                .toList();

        return PageResponse.<EmployeeResponse>builder()
                .content(responses)
                .page(employees.getNumber())
                .size(employees.getSize())
                .totalElements(employees.getTotalElements())
                .totalPages(employees.getTotalPages())
                .last(employees.isLast())
                .build();
    }

    @Override
    public void changeStatus(Long id, boolean active) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        employee.setActive(active);

        employeeRepository.save(employee);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDropdownResponse> getDropdown() {

        return employeeRepository.findByActiveTrueOrderByFirstNameAsc()
                .stream()
                .map(employeeMapper::toDropdownResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getMyProfile() {

        User user = SecurityUtils.getCurrentUser();

        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee Profile not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getCurrentEmployee() {

        User user = SecurityUtils.getCurrentUser();

        return employeeRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee Profile not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }


    @Override
    public EmployeeResponse uploadProfilePhoto(
            MultipartFile file
    ) {

       // MultipartFileValidator.validate(file);
        Employee employee =  getCurrentEmployee();

        validateProfilePhoto(file);

        try {

            // Replace old image with new one
            employee.setProfilePhoto(file.getBytes());
            employee.setProfilePhotoName(file.getOriginalFilename());
            employee.setProfilePhotoType(file.getContentType());

        } catch (IOException e) {

            throw new ValidationException(
                    "Unable to upload profile photo.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }


    private void validateProfilePhoto(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new ValidationException(
                    "Profile photo is required.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        List<String> allowedTypes = List.of(
                "image/jpeg",
                "image/jpg",
                "image/png"
        );

        if (!allowedTypes.contains(file.getContentType())) {

            throw new ValidationException(
                    "Only JPG, JPEG and PNG images are allowed.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        long maxSize = 2 * 1024 * 1024;

        if (file.getSize() > maxSize) {

            throw new ValidationException(
                    "Profile photo size cannot exceed 2 MB.",
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }

}

