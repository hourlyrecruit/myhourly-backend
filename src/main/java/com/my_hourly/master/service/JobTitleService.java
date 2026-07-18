package com.my_hourly.master.service;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.master.api.request.CreateJobTitleRequest;
import com.my_hourly.master.api.request.UpdateJobTitleRequest;
import com.my_hourly.master.api.response.JobTitleResponse;

public interface JobTitleService {

    JobTitleResponse create(CreateJobTitleRequest request);

    JobTitleResponse update(Long id, UpdateJobTitleRequest request);

    JobTitleResponse getById(Long id);

    PageResponse<JobTitleResponse> getAll(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection
    );

    void changeStatus(Long id, boolean active);

    void delete(Long id);

}
