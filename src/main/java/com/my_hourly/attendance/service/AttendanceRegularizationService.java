package com.my_hourly.attendance.service;

import com.my_hourly.attendance.api.request.CreateRegularizationRequest;
import com.my_hourly.attendance.api.request.RegularizationDetailActionRequest;
import com.my_hourly.attendance.api.response.RegularizationResponse;

import java.util.List;

public interface AttendanceRegularizationService {

    /**
     * Employee creates a regularization request.
     */
    RegularizationResponse createRegularization(CreateRegularizationRequest request);

    /**
     * Employee views their own regularization requests.
     */
    List<RegularizationResponse> getMyRegularizations();

    /**
     * Manager views pending regularization requests for their subordinates.
     */
    List<RegularizationResponse> getPendingRegularizationsForManager();

    /**
     * Manager/Employee views a specific regularization by ID.
     */
    RegularizationResponse getRegularizationById(Long id);

    /**
     * Manager approves an individual regularization detail.
     */
    RegularizationResponse approveDetail(
            Long regularizationId,
            Long detailId,
            RegularizationDetailActionRequest request
    );

    /**
     * Manager rejects an individual regularization detail.
     */
    RegularizationResponse rejectDetail(
            Long regularizationId,
            Long detailId,
            RegularizationDetailActionRequest request
    );

    /**
     * HR/Admin reverts an approved regularization detail.
     */
    void revertDetail(
            Long regularizationId,
            Long detailId
    );

    /**
     * Get all regularization requests for a manager's subordinates (not just pending).
     */
    List<RegularizationResponse> getAllRegularizationsForManager();
}
