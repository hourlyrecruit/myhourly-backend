package com.my_hourly.attendance;

import com.my_hourly.attendance.api.request.CreateRegularizationDetailRequest;
import com.my_hourly.attendance.api.request.CreateRegularizationRequest;
import com.my_hourly.attendance.api.request.RegularizationDetailActionRequest;
import com.my_hourly.attendance.api.request.UpdateRegularizationRequest;
import com.my_hourly.attendance.api.response.RegularizationResponse;
import com.my_hourly.attendance.entity.*;
import com.my_hourly.attendance.repository.AttendanceRegularizationDetailRepository;
import com.my_hourly.attendance.repository.AttendanceRegularizationRepository;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.mapper.AttendanceRegularizationMapper;
import com.my_hourly.attendance.service.RegularizationValidator;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.exception.ValidationException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceRegularizationServiceTest {

    @Mock
    private AttendanceRegularizationRepository regularizationRepository;

    @Mock
    private AttendanceRegularizationDetailRepository detailRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private RegularizationValidator regularizationValidator;

    @Mock
    private AttendanceRegularizationMapper mapper;

    @InjectMocks
    private com.my_hourly.attendance.service.impl.AttendanceRegularizationServiceImpl regularizationService;

    private Employee employee;
    private Employee manager;
    private Attendance attendance;
    private AttendanceRegularization regularization;
    private AttendanceRegularizationDetail detail;

    @BeforeEach
    void setUp() {
        manager = Employee.builder()
                .firstName("Manager")
                .lastName("Test")
                .build();
        manager.setId(2L);

        employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .reportingManager(manager)
                .build();
        employee.setId(1L);

        attendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(LocalDate.of(2026, 8, 5))
                .attendanceStatus(AttendanceStatus.LATE)
                .checkInTime(LocalDateTime.of(2026, 8, 5, 9, 45))
                .checkOutTime(LocalDateTime.of(2026, 8, 5, 18, 30))
                .build();
        attendance.setId(101L);

        regularization = AttendanceRegularization.builder()
                .employee(employee)
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .status(RegularizationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .details(new ArrayList<>())
                .build();
        regularization.setId(1L);

        detail = AttendanceRegularizationDetail.builder()
                .regularization(regularization)
                .attendance(attendance)
                .originalStatus(AttendanceStatus.LATE)
                .originalCheckIn(attendance.getCheckInTime())
                .originalCheckOut(attendance.getCheckOutTime())
                .requestedStatus(AttendanceStatus.PRESENT)
                .status(RegularizationDetailStatus.PENDING)
                .build();
        detail.setId(10L);

        regularization.getDetails().add(detail);
    }

    /* =================== Request Creation Tests =================== */

    @Test
    @DisplayName("Employee can create regularization request for their own attendance")
    void createRegularization_Success() {
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));
        when(regularizationRepository.existsActiveRegularizationForAttendance(101L)).thenReturn(false);
        when(regularizationRepository.save(any(AttendanceRegularization.class))).thenReturn(regularization);
        when(detailRepository.save(any(AttendanceRegularizationDetail.class))).thenReturn(detail);
        when(regularizationRepository.findById(anyLong())).thenReturn(Optional.of(regularization));

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(101L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        when(mapper.toResponse(any())).thenReturn(RegularizationResponse.builder().id(1L).build());

        RegularizationResponse response = regularizationService.createRegularization(request);

        assertNotNull(response);
        verify(regularizationValidator).validateStatusTransition(AttendanceStatus.LATE, AttendanceStatus.PRESENT);
        verify(detailRepository).save(any(AttendanceRegularizationDetail.class));
    }

    @Test
    @DisplayName("Employee cannot regularize another employee's attendance")
    void createRegularization_DifferentEmployee() {
        Employee otherEmployee = Employee.builder()
                .firstName("Jane")
                .reportingManager(manager)
                .build();
        otherEmployee.setId(3L);

        Attendance otherAttendance = Attendance.builder()
                .employee(otherEmployee)
                .attendanceDate(LocalDate.of(2026, 8, 5))
                .attendanceStatus(AttendanceStatus.LATE)
                .build();
        otherAttendance.setId(102L);

        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any())).thenReturn(regularization);
        when(attendanceRepository.findById(102L)).thenReturn(Optional.of(otherAttendance));

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(102L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertEquals(ErrorCode.ATTENDANCE_BELONGS_TO_ANOTHER_EMPLOYEE, exception.getErrorCode());
    }

    @Test
    @DisplayName("Attendance date must be within the requested date range")
    void createRegularization_DateOutOfRange() {
        Attendance futureAttendance = Attendance.builder()
                .employee(employee)
                .attendanceDate(LocalDate.of(2026, 9, 15))
                .attendanceStatus(AttendanceStatus.LATE)
                .build();
        futureAttendance.setId(103L);

        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any())).thenReturn(regularization);
        when(attendanceRepository.findById(103L)).thenReturn(Optional.of(futureAttendance));

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(103L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertEquals(ErrorCode.ATTENDANCE_DATE_OUT_OF_RANGE, exception.getErrorCode());
    }

    @Test
    @DisplayName("Attendance must exist for regularization")
    void createRegularization_AttendanceNotFound() {
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any())).thenReturn(regularization);
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(999L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertEquals(ErrorCode.ATTENDANCE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Duplicate active regularization for same attendance is rejected")
    void createRegularization_DuplicateActive() {
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any())).thenReturn(regularization);
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));
        when(regularizationRepository.existsActiveRegularizationForAttendance(101L)).thenReturn(true);

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(101L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertEquals(ErrorCode.DUPLICATE_ACTIVE_REGULARIZATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Invalid status transition is rejected")
    void createRegularization_InvalidTransition() {
        attendance.setAttendanceStatus(AttendanceStatus.HOLIDAY);

        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any())).thenReturn(regularization);
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));
        doThrow(new ValidationException("Invalid transition", ErrorCode.INVALID_STATUS_TRANSITION))
                .when(regularizationValidator).validateStatusTransition(AttendanceStatus.HOLIDAY, AttendanceStatus.PRESENT);

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Attendance correction")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(101L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, exception.getErrorCode());
    }

    /* =================== Update Tests =================== */

    @Test
    @DisplayName("Employee can update their own PENDING regularization request")
    void updateRegularization_Success() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));
        when(regularizationRepository
                .existsActiveRegularizationForAttendanceExcluding(101L, 1L))
                .thenReturn(false);
        when(regularizationRepository.save(any(AttendanceRegularization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any())).thenReturn(RegularizationResponse.builder().id(1L).build());

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Corrected reason")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(101L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        RegularizationResponse response =
                regularizationService.updateRegularization(1L, request);

        assertNotNull(response);
        assertEquals("Corrected reason", regularization.getReason());
        assertEquals(RegularizationStatus.PENDING, regularization.getStatus());
        assertEquals(1, regularization.getDetails().size());
        assertNotSame(detail, regularization.getDetails().get(0));
        assertEquals(
                RegularizationDetailStatus.PENDING,
                regularization.getDetails().get(0).getStatus()
        );
        verify(regularizationValidator)
                .validateStatusTransition(AttendanceStatus.LATE, AttendanceStatus.PRESENT);
        verify(detailRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Non-PENDING regularization request cannot be updated")
    void updateRegularization_NotPending() {
        regularization.setStatus(RegularizationStatus.APPROVED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Corrected reason")
                .details(List.of())
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.updateRegularization(1L, request)
        );
        assertEquals(ErrorCode.REGULARIZATION_NOT_PENDING, exception.getErrorCode());
        assertEquals(RegularizationStatus.APPROVED, regularization.getStatus());
    }

    @Test
    @DisplayName("Cancelled regularization request cannot be updated")
    void updateRegularization_Cancelled() {
        regularization.setStatus(RegularizationStatus.CANCELLED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Corrected reason")
                .details(List.of())
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.updateRegularization(1L, request)
        );
        assertEquals(ErrorCode.REGULARIZATION_NOT_PENDING, exception.getErrorCode());
    }

    @Test
    @DisplayName("Employee cannot update another employee's regularization request")
    void updateRegularization_NotOwner() {
        Employee otherEmployee = Employee.builder()
                .firstName("Jane")
                .reportingManager(manager)
                .build();
        otherEmployee.setId(3L);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(otherEmployee);

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 1))
                .toDate(LocalDate.of(2026, 8, 31))
                .reason("Corrected reason")
                .details(List.of())
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.updateRegularization(1L, request)
        );
        assertEquals(ErrorCode.NOT_ALLOWED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Update rejects a from date after the to date")
    void updateRegularization_InvalidDateRange() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 31))
                .toDate(LocalDate.of(2026, 8, 1))
                .reason("Invalid range")
                .details(List.of())
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.updateRegularization(1L, request)
        );
        assertEquals(ErrorCode.VALIDATION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("From date cannot be after to date"));
    }

    @Test
    @DisplayName("Update rejects details outside the new date range")
    void updateRegularization_DetailOutsideNewRange() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));

        UpdateRegularizationRequest request = UpdateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 9, 1))
                .toDate(LocalDate.of(2026, 9, 30))
                .reason("Moved range")
                .details(List.of(
                        CreateRegularizationDetailRequest.builder()
                                .attendanceId(101L)
                                .requestedStatus(AttendanceStatus.PRESENT)
                                .build()
                ))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.updateRegularization(1L, request)
        );
        assertEquals(ErrorCode.ATTENDANCE_DATE_OUT_OF_RANGE, exception.getErrorCode());
        assertEquals(LocalDate.of(2026, 8, 1), regularization.getFromDate());
    }

    /* =================== Cancel Tests =================== */

    @Test
    @DisplayName("Employee can cancel their own PENDING regularization request")
    void cancelRegularization_Success() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(regularizationRepository.save(any(AttendanceRegularization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any())).thenReturn(
                RegularizationResponse.builder()
                        .id(1L)
                        .status(RegularizationStatus.CANCELLED)
                        .build()
        );

        RegularizationResponse response = regularizationService.cancelRegularization(1L);

        assertNotNull(response);
        assertEquals(RegularizationStatus.CANCELLED, regularization.getStatus());
        verify(regularizationRepository).save(regularization);
    }

    @Test
    @DisplayName("Non-PENDING regularization request cannot be cancelled")
    void cancelRegularization_NotPending() {
        regularization.setStatus(RegularizationStatus.PARTIALLY_APPROVED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.cancelRegularization(1L)
        );
        assertEquals(ErrorCode.REGULARIZATION_NOT_PENDING, exception.getErrorCode());
        assertEquals(RegularizationStatus.PARTIALLY_APPROVED, regularization.getStatus());
    }

    @Test
    @DisplayName("Approved regularization request cannot be cancelled")
    void cancelRegularization_Approved() {
        regularization.setStatus(RegularizationStatus.APPROVED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.cancelRegularization(1L)
        );
        assertEquals(ErrorCode.REGULARIZATION_NOT_PENDING, exception.getErrorCode());
    }

    @Test
    @DisplayName("Employee cannot cancel another employee's regularization request")
    void cancelRegularization_NotOwner() {
        Employee otherEmployee = Employee.builder()
                .firstName("Jane")
                .reportingManager(manager)
                .build();
        otherEmployee.setId(3L);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(otherEmployee);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.cancelRegularization(1L)
        );
        assertEquals(ErrorCode.NOT_ALLOWED, exception.getErrorCode());
        assertEquals(RegularizationStatus.PENDING, regularization.getStatus());
    }

    /* =================== Approval Tests =================== */

    @Test
    @DisplayName("Manager can approve subordinate's pending detail")
    void approveDetail_Success() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.save(any())).thenReturn(attendance);
        when(detailRepository.save(any())).thenReturn(detail);
        when(detailRepository.countByRegularizationId(1L)).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.APPROVED))).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REJECTED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REVERTED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.PENDING))).thenReturn(0L);
        when(regularizationRepository.save(any())).thenReturn(regularization);

        RegularizationDetailActionRequest request = RegularizationDetailActionRequest.builder()
                .status(RegularizationDetailStatus.APPROVED)
                .approvedStatus(AttendanceStatus.PRESENT)
                .remarks("Verified")
                .build();

        when(mapper.toResponse(any())).thenReturn(RegularizationResponse.builder().id(1L).build());

        regularizationService.approveDetail(1L, 10L, request);

        verify(attendanceRepository).save(attendance);
        assertEquals(AttendanceStatus.PRESENT, attendance.getAttendanceStatus());
        assertEquals(RegularizationDetailStatus.APPROVED, detail.getStatus());
    }

    @Test
    @DisplayName("Manager cannot approve another manager's employee request")
    void approveDetail_WrongManager() {
        Employee wrongManager = Employee.builder()
                .firstName("Wrong")
                .build();
        wrongManager.setId(99L);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(wrongManager);

        RegularizationDetailActionRequest request = RegularizationDetailActionRequest.builder()
                .status(RegularizationDetailStatus.APPROVED)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.approveDetail(1L, 10L, request)
        );
        assertEquals(ErrorCode.MANAGER_NOT_AUTHORIZED, exception.getErrorCode());
    }

    @Test
    @DisplayName("Already approved detail cannot be approved again")
    void approveDetail_AlreadyApproved() {
        detail.setStatus(RegularizationDetailStatus.APPROVED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));

        RegularizationDetailActionRequest request = RegularizationDetailActionRequest.builder()
                .status(RegularizationDetailStatus.APPROVED)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.approveDetail(1L, 10L, request)
        );
        assertEquals(ErrorCode.DETAIL_NOT_PENDING, exception.getErrorCode());
    }

    @Test
    @DisplayName("Attendance changes after request should be detected on approval")
    void approveDetail_AttendanceStateChanged() {
        Attendance changedAttendance = Attendance.builder()
                .employee(employee)
                .attendanceStatus(AttendanceStatus.ABSENT)
                .build();
        changedAttendance.setId(101L);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));
        when(attendanceRepository.findById(101L)).thenReturn(Optional.of(changedAttendance));

        RegularizationDetailActionRequest request = RegularizationDetailActionRequest.builder()
                .status(RegularizationDetailStatus.APPROVED)
                .approvedStatus(AttendanceStatus.PRESENT)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.approveDetail(1L, 10L, request)
        );
        assertEquals(ErrorCode.REGULARIZATION_DETAIL_STATE_CHANGED, exception.getErrorCode());
    }

    /* =================== Rejection Tests =================== */

    @Test
    @DisplayName("Manager can reject pending detail")
    void rejectDetail_Success() {
        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));
        when(detailRepository.save(any())).thenReturn(detail);
        when(detailRepository.countByRegularizationId(1L)).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.APPROVED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REJECTED))).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REVERTED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.PENDING))).thenReturn(0L);
        when(regularizationRepository.save(any())).thenReturn(regularization);

        RegularizationDetailActionRequest request = RegularizationDetailActionRequest.builder()
                .status(RegularizationDetailStatus.REJECTED)
                .remarks("Insufficient justification")
                .build();

        when(mapper.toResponse(any())).thenReturn(RegularizationResponse.builder().id(1L).build());

        regularizationService.rejectDetail(1L, 10L, request);

        assertEquals(RegularizationDetailStatus.REJECTED, detail.getStatus());
        assertEquals("Insufficient justification", detail.getRemarks());
        verify(attendanceRepository, never()).save(any());
    }

    /* =================== Revert Tests =================== */

    @Test
    @DisplayName("Revert restores original attendance status")
    void revertDetail_Success() {
        detail.setStatus(RegularizationDetailStatus.APPROVED);
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));
        when(regularizationRepository.existsActiveRegularizationForAttendance(101L)).thenReturn(false);
        when(attendanceRepository.save(any())).thenReturn(attendance);
        when(detailRepository.save(any())).thenReturn(detail);
        when(detailRepository.countByRegularizationId(1L)).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.APPROVED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REJECTED))).thenReturn(0L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.REVERTED))).thenReturn(1L);
        when(detailRepository.countByRegularizationIdAndStatus(eq(1L), eq(RegularizationDetailStatus.PENDING))).thenReturn(0L);
        when(regularizationRepository.save(any())).thenReturn(regularization);

        regularizationService.revertDetail(1L, 10L);

        assertEquals(AttendanceStatus.LATE, attendance.getAttendanceStatus());
        assertEquals(RegularizationDetailStatus.REVERTED, detail.getStatus());
    }

    @Test
    @DisplayName("Cannot revert if another active regularization exists")
    void revertDetail_ActiveRegularizationExists() {
        detail.setStatus(RegularizationDetailStatus.APPROVED);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));
        when(regularizationRepository.existsActiveRegularizationForAttendance(101L)).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.revertDetail(1L, 10L)
        );
        assertEquals(ErrorCode.REGULARIZATION_HAS_ACTIVE_REGULARIZATION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Only APPROVED details can be reverted")
    void revertDetail_NotApproved() {
        detail.setStatus(RegularizationDetailStatus.PENDING);

        when(regularizationRepository.findById(1L)).thenReturn(Optional.of(regularization));
        when(detailRepository.findById(10L)).thenReturn(Optional.of(detail));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.revertDetail(1L, 10L)
        );
        assertEquals(ErrorCode.CANNOT_REVERT, exception.getErrorCode());
    }

    /* =================== Validator Tests =================== */

    @Test
    @DisplayName("Status transition LATE -> PRESENT is allowed")
    void validateTransition_LateToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(AttendanceStatus.LATE, AttendanceStatus.PRESENT));
    }

    @Test
    @DisplayName("Status transition HOLIDAY -> PRESENT is not allowed")
    void validateTransition_HolidayToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateStatusTransition(AttendanceStatus.HOLIDAY, AttendanceStatus.PRESENT)
        );
        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Status transition WEEKEND -> PRESENT is not allowed")
    void validateTransition_WeekendToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateStatusTransition(AttendanceStatus.WEEKEND, AttendanceStatus.PRESENT)
        );
        assertEquals(ErrorCode.INVALID_STATUS_TRANSITION, exception.getErrorCode());
    }

    @Test
    @DisplayName("Status transition ABSENT -> PRESENT is allowed")
    void validateTransition_AbsentToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(AttendanceStatus.ABSENT, AttendanceStatus.PRESENT));
    }

    @Test
    @DisplayName("Status transition HALF_DAY -> PRESENT is allowed")
    void validateTransition_HalfDayToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(AttendanceStatus.HALF_DAY, AttendanceStatus.PRESENT));
    }

    @Test
    @DisplayName("Status transition MISSED_CHECKOUT -> PRESENT is allowed")
    void validateTransition_MissedCheckoutToPresent() {
        RegularizationValidator validator = new RegularizationValidator();
        assertDoesNotThrow(() ->
                validator.validateStatusTransition(AttendanceStatus.MISSED_CHECKOUT, AttendanceStatus.PRESENT));
    }

    @Test
    @DisplayName("Date range validation: from date after to date")
    void createRegularization_InvalidDateRange() {
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        CreateRegularizationRequest request = CreateRegularizationRequest.builder()
                .fromDate(LocalDate.of(2026, 8, 31))
                .toDate(LocalDate.of(2026, 8, 1))
                .reason("Invalid range")
                .details(List.of())
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> regularizationService.createRegularization(request)
        );
        assertTrue(exception.getMessage().contains("From date cannot be after to date"));
    }
}
