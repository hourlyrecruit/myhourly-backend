package com.my_hourly.lookup.service.impl;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.lookup.api.response.LookupResponse;
import com.my_hourly.lookup.api.response.ReportingManagerLookupResponse;
import com.my_hourly.lookup.mapper.LookupMapper;
import com.my_hourly.lookup.service.LookupService;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LookupServiceImpl implements LookupService {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleRepository jobTitleRepository;
    private final EmployeeRepository employeeRepository;
    private final LookupMapper lookupMapper;

    @Override
    public List<LookupResponse> getDepartments() {

        return departmentRepository.findByActiveTrueOrderByDepartmentNameAsc()
                .stream()
                .map(lookupMapper::toResponse)
                .toList();
    }

    @Override
    public List<LookupResponse> getDesignations(Long departmentId) {

        return designationRepository
                .findByDepartmentIdAndActiveTrueOrderByDesignationNameAsc(departmentId)
                .stream()
                .map(lookupMapper::toResponse)
                .toList();
    }

    @Override
    public List<LookupResponse> getJobTitles(Long designationId) {

        return jobTitleRepository
                .findByDesignationIdAndActiveTrueOrderByJobTitleAsc(designationId)
                .stream()
                .map(lookupMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReportingManagerLookupResponse> getReportingManagers() {

        User user = SecurityUtils.getCurrentUser();
        return employeeRepository.findByActiveTrueAndRoleNameOrderByFirstNameAsc(user.getRole())
                .stream()
                .map(lookupMapper::toResponse)
                .toList();
    }
}