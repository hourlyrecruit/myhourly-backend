package com.my_hourly.master.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateDesignationRequest;
import com.my_hourly.master.api.request.UpdateDesignationRequest;
import com.my_hourly.master.api.response.DesignationResponse;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.mapper.DesignationMapper;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.service.DesignationService;
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
@Transactional
public class DesignationServiceImpl implements DesignationService {

    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationMapper designationMapper;

    private String generateDesignationCode() {

        Designation lastDesignation = designationRepository
                .findTopByOrderByDesignationCodeDesc()
                .orElse(null);

        if (lastDesignation == null) {
            return "DES0001";
        }

        int number = Integer.parseInt(
                lastDesignation.getDesignationCode().substring(3)
        );

        return String.format("DES%04d", number + 1);
    }

    @Override
    public DesignationResponse create(CreateDesignationRequest request) {

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        if (designationRepository.existsByDesignationNameAndDepartment(
                request.getDesignationName(),
                department
        )) {

            throw new ValidationException(
                    "Designation already exists in this department.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        Designation designation =
                designationMapper.toEntity(request, department);

        designation.setDesignationCode(generateDesignationCode());

        Designation savedDesignation =
                designationRepository.save(designation);

        return designationMapper.toResponse(savedDesignation);
    }

    @Override
    @Transactional(readOnly = true)
    public DesignationResponse getById(Long id) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return designationMapper.toResponse(designation);
    }


    @Override
    public DesignationResponse update(
            Long id,
            UpdateDesignationRequest request
    ) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        if (!designation.getDesignationName().equalsIgnoreCase(request.getDesignationName())
                || !designation.getDepartment().getId().equals(department.getId())) {

            if (designationRepository.existsByDesignationNameAndDepartment(
                    request.getDesignationName(),
                    department
            )) {

                throw new ValidationException(
                        "Designation already exists in this department.",
                        ErrorCode.VALIDATION_FAILED
                );
            }
        }

        designationMapper.updateEntity(
                designation,
                request,
                department
        );

        Designation updatedDesignation =
                designationRepository.save(designation);

        return designationMapper.toResponse(updatedDesignation);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DesignationResponse> getAll(
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

        Page<Designation> designationPage;

        if (search == null || search.isBlank()) {

            designationPage = designationRepository.findAll(pageable);

        } else {

            designationPage = designationRepository
                    .findByDesignationNameContainingIgnoreCase(
                            search,
                            pageable
                    );

        }

        List<DesignationResponse> responses = designationPage.getContent()
                .stream()
                .map(designationMapper::toResponse)
                .toList();

        return PageResponse.<DesignationResponse>builder()
                .content(responses)
                .page(designationPage.getNumber())
                .size(designationPage.getSize())
                .totalElements(designationPage.getTotalElements())
                .totalPages(designationPage.getTotalPages())
                .last(designationPage.isLast())
                .build();
    }

    @Override
    public void changeStatus(Long id, boolean active) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        designation.setActive(active);

        designationRepository.save(designation);
    }

    @Override
    public void delete(Long id) {

        Designation designation = designationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        designationRepository.delete(designation);
    }

}
