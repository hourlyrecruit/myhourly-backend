package com.my_hourly.master.mapper;


import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.api.request.UpdateJobTitleRequest;
import com.my_hourly.master.api.response.JobTitleResponse;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import org.springframework.stereotype.Component;

@Component
public class JobTitleMapper {

    public JobTitle toEntity(
            CreateJobTitleRequest request,
            Designation designation
    ) {

        return JobTitle.builder()
                .jobTitle(request.getJobTitle())
                .designation(designation)
                .active(true)
                .build();

    }

    public void updateEntity(
            JobTitle jobTitle,
            UpdateJobTitleRequest request,
            Designation designation
    ) {

        jobTitle.setJobTitle(request.getJobTitle());
        jobTitle.setDesignation(designation);
        jobTitle.setActive(request.isActive());

    }

    public JobTitleResponse toResponse(JobTitle jobTitle) {

        return JobTitleResponse.builder()
                .id(jobTitle.getId())
                .jobTitleCode(jobTitle.getJobTitleCode())
                .jobTitle(jobTitle.getJobTitle())
                .designationId(jobTitle.getDesignation().getId())
                .designationName(jobTitle.getDesignation().getDesignationName())
                .active(jobTitle.isActive())
                .build();

    }

}