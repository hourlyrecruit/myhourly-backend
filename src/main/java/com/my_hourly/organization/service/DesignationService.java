package com.my_hourly.organization.service;

import com.my_hourly.common.response.PageResponse;
import com.my_hourly.organization.api.request.CreateDesignationRequest;
import com.my_hourly.organization.api.request.UpdateDesignationRequest;
import com.my_hourly.organization.api.response.DesignationResponse;



public interface DesignationService {

    DesignationResponse create(CreateDesignationRequest request);

    DesignationResponse update(Long id, UpdateDesignationRequest request);

    DesignationResponse getById(Long id);

    PageResponse<DesignationResponse> getAll(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection
    );

    void changeStatus(Long id, boolean active);

    void delete(Long id);

}
