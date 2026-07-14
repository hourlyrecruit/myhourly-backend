package com.my_hourly.master.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.api.request.UpdateJobTitleRequest;
import com.my_hourly.master.api.response.JobTitleResponse;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import com.my_hourly.master.mapper.JobTitleMapper;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.master.service.JobTitleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobTitleServiceImpl implements JobTitleService {

    private final JobTitleRepository jobTitleRepository;
    private final DesignationRepository designationRepository;
    private final JobTitleMapper jobTitleMapper;

    private String generateJobTitleCode() {

        JobTitle lastJobTitle =
                jobTitleRepository.findTopByOrderByJobTitleCodeDesc();

        if (lastJobTitle == null) {
            return "JT0001";
        }

        int number = Integer.parseInt(
                lastJobTitle.getJobTitleCode().substring(2)
        );

        return String.format("JT%04d", number + 1);
    }

    @Override
    public JobTitleResponse create(CreateJobTitleRequest request) {

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        if (jobTitleRepository.existsByJobTitleAndDesignationId(
                request.getJobTitle(),
                request.getDesignationId())) {

            throw new ValidationException(
                    "Job title already exists.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        JobTitle jobTitle =
                jobTitleMapper.toEntity(request, designation);

        jobTitle.setJobTitleCode(generateJobTitleCode());

        return jobTitleMapper.toResponse(
                jobTitleRepository.save(jobTitle)
        );
    }

    @Override
    public JobTitleResponse update(Long id, UpdateJobTitleRequest request) {

        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job title not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Designation not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        jobTitleMapper.updateEntity(
                jobTitle,
                request,
                designation
        );

        return jobTitleMapper.toResponse(
                jobTitleRepository.save(jobTitle)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JobTitleResponse getById(Long id) {

        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job title not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        return jobTitleMapper.toResponse(jobTitle);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<JobTitleResponse> getAll(
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

        Page<JobTitle> jobTitles;

        if (search == null || search.isBlank()) {
            jobTitles = jobTitleRepository.findAll(pageable);
        } else {
            jobTitles = jobTitleRepository.findByJobTitleContainingIgnoreCase(
                    search,
                    pageable
            );
        }

        List<JobTitleResponse> responses = jobTitles.getContent()
                .stream()
                .map(jobTitleMapper::toResponse)
                .toList();

        return PageResponse.<JobTitleResponse>builder()
                .content(responses)
                .page(jobTitles.getNumber())
                .size(jobTitles.getSize())
                .totalElements(jobTitles.getTotalElements())
                .totalPages(jobTitles.getTotalPages())
                .last(jobTitles.isLast())
                .build();
    }

    @Override
    public void changeStatus(Long id, boolean active) {

        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job title not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        jobTitle.setActive(active);

        jobTitleRepository.save(jobTitle);
    }

    @Override
    public void delete(Long id) {

        JobTitle jobTitle = jobTitleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job title not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));

        jobTitleRepository.delete(jobTitle);
    }
}
