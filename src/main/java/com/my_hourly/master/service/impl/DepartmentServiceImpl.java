package com.my_hourly.master.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateDepartmentRequest;
import com.my_hourly.master.api.request.UpdateDepartmentRequest;
import com.my_hourly.master.api.response.DepartmentResponse;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.mapper.DepartmentMapper;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    private String generateDepartmentCode() {

        Department lastDepartment = departmentRepository
                .findTopByOrderByDepartmentCodeDesc()
                .orElse(null);

        if (lastDepartment == null) {
            return "DEP0001";
        }

        int number = Integer.parseInt(
                lastDepartment.getDepartmentCode().substring(3)
        );

        return String.format("DEP%04d", number + 1);

    }

    @Override
    @Transactional
    public DepartmentResponse create(CreateDepartmentRequest request) {

        if (departmentRepository.existsByDepartmentName(
                request.getDepartmentName())) {

            throw new ValidationException(
                    "Department already exists.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        Department department = departmentMapper.toEntity(request);

        department.setDepartmentCode(generateDepartmentCode());

        Department saved = departmentRepository.save(department);

        return departmentMapper.toResponse(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return departmentMapper.toResponse(department);
    }

    @Override
    public DepartmentResponse update(
            Long id,
            UpdateDepartmentRequest request
    ) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        if (!department.getDepartmentName().equalsIgnoreCase(request.getDepartmentName())) {

            if (departmentRepository.existsByDepartmentName(
                    request.getDepartmentName()
            )) {

                throw new ValidationException(
                        "Department already exists.",
                        ErrorCode.VALIDATION_FAILED
                );
            }
        }

        departmentMapper.updateEntity(
                department,
                request
        );

        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.toResponse(updatedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAll(
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

        Page<Department> departmentPage;

        if (search == null || search.isBlank()) {
            departmentPage = departmentRepository.findAll(pageable);
        } else {
            departmentPage = departmentRepository
                    .findByDepartmentNameContainingIgnoreCase(
                            search,
                            pageable
                    );
        }

        List<DepartmentResponse> response = departmentPage.getContent()
                .stream()
                .map(departmentMapper::toResponse)
                .toList();

        return PageResponse.<DepartmentResponse>builder()
                .content(response)
                .page(departmentPage.getNumber())
                .size(departmentPage.getSize())
                .totalElements(departmentPage.getTotalElements())
                .totalPages(departmentPage.getTotalPages())
                .last(departmentPage.isLast())
                .build();
    }

    @Override
    public void changeStatus(Long id, boolean active) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        department.setActive(active);

        departmentRepository.save(department);
    }

    @Override
    public void delete(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        departmentRepository.delete(department);
    }
}
