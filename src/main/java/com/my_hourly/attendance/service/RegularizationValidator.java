package com.my_hourly.attendance.service;

import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RegularizationValidator {

    /**
     * Allowed status transitions. Each key maps to the set of statuses
     * that the attendance can be corrected to.
     */
    private static final Map<AttendanceStatus, Set<AttendanceStatus>> ALLOWED_TRANSITIONS = Map.of(
            AttendanceStatus.LATE, Set.of(AttendanceStatus.PRESENT),
            AttendanceStatus.HALF_DAY, Set.of(AttendanceStatus.PRESENT),
            AttendanceStatus.ABSENT, Set.of(AttendanceStatus.PRESENT),
            AttendanceStatus.MISSED_CHECKOUT, Set.of(AttendanceStatus.PRESENT)
    );

    /**
     * Validates that the requested status transition is allowed.
     *
     * @param currentStatus the current attendance status
     * @param requestedStatus the requested new status
     */
    public void validateStatusTransition(
            AttendanceStatus currentStatus,
            AttendanceStatus requestedStatus
    ) {
        if (currentStatus == null || requestedStatus == null) {
            throw new ValidationException(
                    "Current status and requested status must be provided.",
                    ErrorCode.INVALID_STATUS_TRANSITION
            );
        }

        Set<AttendanceStatus> allowed = ALLOWED_TRANSITIONS.get(currentStatus);

        if (allowed == null || !allowed.contains(requestedStatus)) {
            throw new ValidationException(
                    String.format(
                            "Status transition from %s to %s is not allowed.",
                            currentStatus, requestedStatus
                    ),
                    ErrorCode.INVALID_STATUS_TRANSITION
            );
        }
    }

    /**
     * Validates that the approval status is valid.
     */
    public void validateApprovedStatus(AttendanceStatus approvedStatus) {
        if (approvedStatus == null) {
            throw new ValidationException(
                    "Approved status must be provided.",
                    ErrorCode.VALIDATION_FAILED
            );
        }
    }
}
