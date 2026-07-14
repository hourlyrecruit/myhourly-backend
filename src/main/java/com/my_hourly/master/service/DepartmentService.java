package com.my_hourly.master.service;

import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateDepartmentRequest;
import com.my_hourly.master.api.request.UpdateDepartmentRequest;
import com.my_hourly.master.api.response.DepartmentResponse;



public interface DepartmentService {

    DepartmentResponse create(CreateDepartmentRequest request);

    DepartmentResponse update(Long id, UpdateDepartmentRequest request);

    DepartmentResponse getById(Long id);

    PageResponse<DepartmentResponse> getAll(
            int page,
            int size,
            String search,
            String sortBy,
            String sortDirection
    );

    void changeStatus(Long id, boolean active);

    void delete(Long id);

}
