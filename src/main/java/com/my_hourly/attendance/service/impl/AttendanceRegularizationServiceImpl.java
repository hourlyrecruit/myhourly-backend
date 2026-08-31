package com.my_hourly.attendance.service.impl;

import com.my_hourly.attendance.api.request.CreateRegularizationDetailRequest;
import com.my_hourly.attendance.api.request.CreateRegularizationRequest;
import com.my_hourly.attendance.api.request.RegularizationDetailActionRequest;
import com.my_hourly.attendance.api.response.RegularizationResponse;
import com.my_hourly.attendance.entity.*;
import com.my_hourly.attendance.mapper.AttendanceRegularizationMapper;
import com.my_hourly.attendance.repository.AttendanceRegularizationDetailRepository;
import com.my_hourly.attendance.repository.AttendanceRegularizationRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceRegularizationService;
import com.my_hourly.attendance.service.RegularizationValidator;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceRegularizationServiceImpl implements AttendanceRegularizationService {

    private final AttendanceRegularizationRepository regularizationRepository;
    private final AttendanceRegularizationDetailRepository detailRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;
    private final RegularizationValidator regularizationValidator;
    private final AttendanceRegularizationMapper mapper;

    @Override
    @Transactional
    public RegularizationResponse createRegularization(CreateRegularizationRequest request) {

        Employee employee = employeeService.getCurrentEmployee();

        // Validate date range
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new ValidationException(
                    "From date cannot be after to date.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        // Create parent regularization
        AttendanceRegularization regularization = AttendanceRegularization.builder()
                .employee(employee)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .reason(request.getReason())
                .status(RegularizationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        regularization = regularizationRepository.save(regularization);

        // Process each detail
        for (CreateRegularizationDetailRequest detailReq : request.getDetails()) {
            processDetailCreation(regularization, employee, detailReq);
        }

        regularization = regularizationRepository.findById(regularization.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Regularization not found after creation.",
                        ErrorCode.REGULARIZATION_NOT_FOUND
                ));

        log.info("Regularization {} created by employee {}", regularization.getId(), employee.getId());
        return mapper.toResponse(regularization);
    }

    private void processDetailCreation(
            AttendanceRegularization regularization,
            Employee employee,
            CreateRegularizationDetailRequest detailReq
    ) {
        // 1. Validate attendance exists
        Attendance attendance = attendanceRepository.findById(detailReq.getAttendanceId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with ID: " + detailReq.getAttendanceId(),
                        ErrorCode.ATTENDANCE_NOT_FOUND
                ));

        // 2. Validate attendance belongs to employee
        if (!attendance.getEmployee().getId().equals(employee.getId())) {
            throw new ValidationException(
                    "You cannot regularize another employee's attendance.",
                    ErrorCode.ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE
            );
        }

        // 3. Validate attendance date is within fromDate and toDate
        if (attendance.getAttendanceDate().isBefore(regularization.getFromDate())
                || attendance.getAttendanceDate().isAfter(regularization.getToDate())) {
            throw new ValidationException(
                    "Attendance date " + attendance.getAttendanceDate()
                            + " is outside the requested date range.",
                    ErrorCode.ATTENDANCE_DATE_OUT_OF_RANGE
            );
        }

        // 4. Validate status transition
        regularizationValidator.validateStatusTransition(
                attendance.getAttendanceStatus(),
                detailReq.getRequestedStatus()
        );

        // 5. Prevent duplicate active regularization for same attendance
        if (regularizationRepository.existsActiveRegularizationForAttendance(
                detailReq.getAttendanceId())) {
            throw new ValidationException(
                    "Attendance record already has an active regularization request.",
                    ErrorCode.DUPLICATE_ACTIVE_REGULARIZATION
            );
        }

        // Create detail with snapshot
        AttendanceRegularizationDetail detail = AttendanceRegularizationDetail.builder()
                .regularization(regularization)
                .attendance(attendance)
                .originalStatus(attendance.getAttendanceStatus())
                .originalCheckIn(attendance.getCheckInTime())
                .originalCheckOut(attendance.getCheckOutTime())
                .requestedStatus(detailReq.getRequestedStatus())
                .requestedCheckIn(attendance.getCheckInTime())
                .requestedCheckOut(attendance.getCheckOutTime())
                .status(RegularizationDetailStatus.PENDING)
                .build();

        detailRepository.save(detail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegularizationResponse> getMyRegularizations() {
        Employee employee = employeeService.getCurrentEmployee();
        List<AttendanceRegularization> regularizations =
                regularizationRepository.findByEmployeeOrderByCreatedAtDesc(employee);
        return regularizations.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegularizationResponse> getPendingRegularizationsForManager() {
        Employee manager = employeeService.getCurrentEmployee();
        List<AttendanceRegularization> regularizations =
                regularizationRepository.findPendingByManagerId(
                        manager.getId(),
                        RegularizationStatus.PENDING
                );
        return regularizations.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegularizationResponse> getAllRegularizationsForManager() {
        Employee manager = employeeService.getCurrentEmployee();
        List<AttendanceRegularization> regularizations =
                regularizationRepository.findAllByManagerId(manager.getId());
        return regularizations.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RegularizationResponse getRegularizationById(Long id) {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        AttendanceRegularization regularization = regularizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Regularization not found with ID: " + id,
                        ErrorCode.REGULARIZATION_NOT_FOUND
                ));
        return mapper.toResponse(regularization);
    }

    @Override
    @Transactional
    public RegularizationResponse approveDetail(
            Long regularizationId,
            Long detailId,
            RegularizationDetailActionRequest request
    ) {
        // Fetch and validate
        AttendanceRegularization regularization = getAndValidateRegularization(regularizationId);
        Employee manager = employeeService.getCurrentEmployee();

        // Verify manager authorization
        validateManagerAuthorization(regularization, manager);

        // Verify regularization is still processable
        validateRegularizationActive(regularization);

        // Find detail
        AttendanceRegularizationDetail detail = findDetail(detailId, regularizationId);

        // Verify detail is pending
        if (detail.getStatus() != RegularizationDetailStatus.PENDING) {
            throw new ValidationException(
                    "This detail is not in PENDING status. Current status: " + detail.getStatus(),
                    ErrorCode.DETAIL_NOT_PENDING
            );
        }

        // Verify attendance state hasn't changed unexpectedly
        validateAttendanceStateConsistency(detail);

        // Fetch attendance
        Attendance attendance = detail.getAttendance();

        // Set approved values
        detail.setApprovedStatus(
                request.getApprovedStatus() != null
                        ? request.getApprovedStatus()
                        : detail.getRequestedStatus()
        );
        detail.setApprovedCheckIn(attendance.getCheckInTime());
        detail.setApprovedCheckOut(attendance.getCheckOutTime());
        detail.setStatus(RegularizationDetailStatus.APPROVED);

        // Update attendance record
        attendance.setAttendanceStatus(detail.getApprovedStatus());

        // Save
        detailRepository.save(detail);
        attendanceRepository.save(attendance);

        // Recalculate parent status
        recalculateParentStatus(regularization);

        log.info("Detail {} approved by manager {} for regularization {}",
                detailId, manager.getId(), regularizationId);

        return mapper.toResponse(regularization);
    }

    @Override
    @Transactional
    public RegularizationResponse rejectDetail(
            Long regularizationId,
            Long detailId,
            RegularizationDetailActionRequest request
    ) {
        // Fetch and validate
        AttendanceRegularization regularization = getAndValidateRegularization(regularizationId);
        Employee manager = employeeService.getCurrentEmployee();

        // Verify manager authorization
        validateManagerAuthorization(regularization, manager);

        // Verify regularization is still processable
        validateRegularizationActive(regularization);

        // Find detail
        AttendanceRegularizationDetail detail = findDetail(detailId, regularizationId);

        // Verify detail is pending
        if (detail.getStatus() != RegularizationDetailStatus.PENDING) {
            throw new ValidationException(
                    "This detail is not in PENDING status. Current status: " + detail.getStatus(),
                    ErrorCode.DETAIL_NOT_PENDING
            );
        }

        // Set rejection
        detail.setStatus(RegularizationDetailStatus.REJECTED);
        detail.setRemarks(request.getRemarks());

        // Save - do NOT modify attendance for rejection
        detailRepository.save(detail);

        // Recalculate parent status
        recalculateParentStatus(regularization);

        log.info("Detail {} rejected by manager {} for regularization {}",
                detailId, manager.getId(), regularizationId);

        return mapper.toResponse(regularization);
    }

    @Override
    @Transactional
    public void revertDetail(Long regularizationId, Long detailId) {

        AttendanceRegularization regularization = getAndValidateRegularization(regularizationId);

        AttendanceRegularizationDetail detail = findDetail(detailId, regularizationId);

        // Verify detail is approved
        if (detail.getStatus() != RegularizationDetailStatus.APPROVED) {
            throw new ValidationException(
                    "Only APPROVED details can be reverted.",
                    ErrorCode.CANNOT_REVERT
            );
        }

        // Check no other active regularization for same attendance
        if (regularizationRepository.existsActiveRegularizationForAttendance(
                detail.getAttendance().getId())) {
            throw new ValidationException(
                    "Cannot revert: another active regularization exists for this attendance.",
                    ErrorCode.REGULARIZATION_HAS_ACTIVE_REGULARIZATION
            );
        }

        // Restore original attendance
        Attendance attendance = detail.getAttendance();
        attendance.setAttendanceStatus(detail.getOriginalStatus());
        attendance.setCheckInTime(detail.getOriginalCheckIn());
        attendance.setCheckOutTime(detail.getOriginalCheckOut());

        // Update detail
        detail.setStatus(RegularizationDetailStatus.REVERTED);

        // Save
        detailRepository.save(detail);
        attendanceRepository.save(attendance);

        // Recalculate parent
        recalculateParentStatus(regularization);

        log.info("Detail {} reverted for regularization {}", detailId, regularizationId);
    }

    /* ============ Private Helpers ============ */

    private AttendanceRegularization getAndValidateRegularization(Long regularizationId) {
        return regularizationRepository.findById(regularizationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Regularization not found with ID: " + regularizationId,
                        ErrorCode.REGULARIZATION_NOT_FOUND
                ));
    }

    private void validateManagerAuthorization(
            AttendanceRegularization regularization,
            Employee manager
    ) {
        Employee employee = regularization.getEmployee();
        if (employee.getReportingManager() == null
                || !employee.getReportingManager().getId().equals(manager.getId())) {
            throw new ValidationException(
                    "You are not authorized to manage this regularization request.",
                    ErrorCode.MANAGER_NOT_AUTHORIZED
            );
        }
    }

    private void validateRegularizationActive(AttendanceRegularization regularization) {
        if (regularization.getStatus() == RegularizationStatus.APPROVED
                || regularization.getStatus() == RegularizationStatus.REJECTED
                || regularization.getStatus() == RegularizationStatus.CANCELLED) {
            throw new ValidationException(
                    "This regularization request is already " + regularization.getStatus(),
                    ErrorCode.REGULARIZATION_NOT_PENDING
            );
        }
    }

    private AttendanceRegularizationDetail findDetail(Long detailId, Long regularizationId) {
        AttendanceRegularizationDetail detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Regularization detail not found with ID: " + detailId,
                        ErrorCode.REGULARIZATION_DETAIL_NOT_FOUND
                ));

        if (!detail.getRegularization().getId().equals(regularizationId)) {
            throw new ValidationException(
                    "Detail does not belong to this regularization.",
                    ErrorCode.VALIDATION_FAILED
            );
        }

        return detail;
    }

    private void validateAttendanceStateConsistency(AttendanceRegularizationDetail detail) {
        Attendance currentAttendance = attendanceRepository
                .findById(detail.getAttendance().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found.",
                        ErrorCode.ATTENDANCE_NOT_FOUND
                ));

        // If the attendance status has changed since request creation, reject
        if (!currentAttendance.getAttendanceStatus().equals(detail.getOriginalStatus())) {
            throw new ValidationException(
                    "Attendance state has changed since the regularization was requested. "
                            + "Expected: " + detail.getOriginalStatus()
                            + ", Current: " + currentAttendance.getAttendanceStatus()
                            + ". Please recreate the request.",
                    ErrorCode.REGULARIZATION_DETAIL_STATE_CHANGED
            );
        }
    }

    private void recalculateParentStatus(AttendanceRegularization regularization) {
        Long regId = regularization.getId();
        long totalDetails = detailRepository.countByRegularizationId(regId);
        long approvedCount = detailRepository.countByRegularizationIdAndStatus(
                regId, RegularizationDetailStatus.APPROVED);
        long rejectedCount = detailRepository.countByRegularizationIdAndStatus(
                regId, RegularizationDetailStatus.REJECTED);
        long revertedCount = detailRepository.countByRegularizationIdAndStatus(
                regId, RegularizationDetailStatus.REVERTED);
        long pendingCount = detailRepository.countByRegularizationIdAndStatus(
                regId, RegularizationDetailStatus.PENDING);

        // Effective non-pending count
        long decidedCount = approvedCount + rejectedCount + revertedCount;

        RegularizationStatus newStatus;

        if (pendingCount == totalDetails) {
            // All still pending
            newStatus = RegularizationStatus.PENDING;
        } else if (approvedCount == totalDetails) {
            // All approved
            newStatus = RegularizationStatus.APPROVED;
        } else if (rejectedCount == totalDetails) {
            // All rejected
            newStatus = RegularizationStatus.REJECTED;
        } else if (decidedCount == totalDetails) {
            // All decided but mixed -> partially approved
            if (approvedCount > 0) {
                newStatus = RegularizationStatus.PARTIALLY_APPROVED;
            } else {
                newStatus = RegularizationStatus.REJECTED;
            }
        } else {
            // Some pending, some decided
            if (approvedCount > 0 && (rejectedCount > 0 || revertedCount > 0)) {
                newStatus = RegularizationStatus.PARTIALLY_APPROVED;
            } else if (approvedCount > 0) {
                newStatus = RegularizationStatus.PENDING;
            } else {
                newStatus = RegularizationStatus.PENDING;
            }
        }

        regularization.setStatus(newStatus);

        if (newStatus == RegularizationStatus.APPROVED) {
            regularization.setApprovedAt(LocalDateTime.now());
            Employee manager = employeeService.getCurrentEmployee();
            regularization.setApprovedBy(manager);
        } else if (newStatus == RegularizationStatus.REJECTED && approvedCount == 0) {
            regularization.setRejectedAt(LocalDateTime.now());
            Employee manager = employeeService.getCurrentEmployee();
            regularization.setRejectedBy(manager);
        }

        regularizationRepository.save(regularization);
    }
}
