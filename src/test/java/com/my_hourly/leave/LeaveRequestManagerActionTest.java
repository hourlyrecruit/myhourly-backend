package com.my_hourly.leave;

import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.attendance.service.AttendanceService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.request.LeaveActionRequest;
import com.my_hourly.leave.dto.PaidLopAllocation;
import com.my_hourly.leave.email.LeaveEmailService;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveRequestMonthAllocation;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.ApprovalLevel;
import com.my_hourly.leave.enums.LeaveAction;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import com.my_hourly.leave.repository.LeaveRequestMonthAllocationRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.LeaveAuthorizationService;
import com.my_hourly.leave.service.LeaveBalanceService;
import com.my_hourly.leave.service.LeavePaidLopService;
import com.my_hourly.leave.service.LeaveValidationService;
import com.my_hourly.leave.service.impl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pins the approval / rejection / cancellation rules of
 * {@code LeaveRequestServiceImpl}:
 *
 * <ul>
 *   <li>the approver comes from the authenticated security context (via
 *       {@code getCurrentEmployee}), never from the request body - the body
 *       type {@link LeaveActionRequest} only carries {@code action} and
 *       {@code reason};</li>
 *   <li>only an authorized reporting manager may act on a request;</li>
 *   <li>a request can be processed exactly once (row lock + PENDING check);</li>
 *   <li>PAID days deduct the balance, LOP days never do, and the per-month
 *       breakdown is persisted for date-accurate monthly usage;</li>
 *   <li>approved / rejected / foreign leave cannot be cancelled.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Leave approval workflow")
class LeaveRequestManagerActionTest {

    private static final long REQUEST_ID = 1L;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveRequestMonthAllocationRepository leaveRequestMonthAllocationRepository;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @Mock
    private LeaveValidationService leaveValidationService;

    @Mock
    private LeaveAuthorizationService leaveAuthorizationService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private AttendanceService attendanceService;

    @Mock
    private com.my_hourly.leave.service.LeaveApprovalService leaveApprovalService;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private LeaveEmailService leaveEmailService;

    @Mock
    private LeavePaidLopService leavePaidLopService;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private Employee manager;
    private Employee employee;
    private LeaveType paidType;
    private LeaveType unpaidType;
    private LeaveBalance balance;
    private LeaveRequest pending;

    @BeforeEach
    void setUp() {

        manager = Employee.builder().firstName("Rohit").lastName("Sharma").build();
        manager.setId(7L);

        employee = Employee.builder().firstName("Jitendra").lastName("Prajapati").build();
        employee.setId(101L);

        paidType = LeaveType.builder()
                .name("ANNUAL")
                .paid(true)
                .allocatedDays(24)
                .active(true)
                .build();
        paidType.setId(1L);

        unpaidType = LeaveType.builder()
                .name("UNPAID")
                .paid(false)
                .allocatedDays(0)
                .active(true)
                .build();
        unpaidType.setId(2L);

        balance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(paidType)
                .year(LocalDate.now().getYear())
                .allocatedLeaves(24)
                .usedLeaves(10)
                .expiredLeaves(0)
                .remainingLeaves(14)
                .build();

        pending = LeaveRequest.builder()
                .employee(employee)
                .leaveType(paidType)
                .startDate(LocalDate.now().plusDays(7))
                .endDate(LocalDate.now().plusDays(9))
                .totalDays(3)
                .reason("Personal")
                .status(LeaveStatus.PENDING)
                .build();
        pending.setId(REQUEST_ID);
    }

    /** Everything managerAction needs before it reaches the action switch. */
    private void givenRequestLocked() {

        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(leaveRequestRepository.findByIdForUpdate(REQUEST_ID))
                .thenReturn(Optional.of(pending));
        // Attendance conflicts default to false (no attendance rows), which is
        // exactly the "no conflict" case - no stubbing needed.
    }

    private void givenBalanceLocked() {

        when(leaveBalanceService.getLeaveBalanceEntityForUpdate(
                eq(employee), any(LeaveType.class), any(LocalDate.class)))
                .thenReturn(balance);
    }

    private void givenApprovableRequest() {

        givenRequestLocked();
        givenBalanceLocked();
    }

    private static LeaveActionRequest approveRequest() {
        return LeaveActionRequest.builder().action(LeaveAction.APPROVE).build();
    }

    private static LeaveActionRequest rejectRequest(String reason) {
        return LeaveActionRequest.builder().action(LeaveAction.REJECT).reason(reason).build();
    }

    // -----------------------------------------------------------------------
    // Approval
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Approval records the split, the authenticated approver and the monthly breakdown")
    void approvalRecordsSplitApproverAndMonthlyBreakdown() {

        givenApprovableRequest();

        LocalDate start = pending.getStartDate();
        YearMonth month = YearMonth.from(start);

        when(leavePaidLopService.classify(eq(employee), eq(paidType), eq(start), eq(pending.getEndDate()), any()))
                .thenReturn(new PaidLopAllocation(2, 1, List.of(
                        new PaidLopAllocation.MonthAllocation(month, 3, 2, 2, 1)
                )));

        leaveRequestService.managerAction(REQUEST_ID, approveRequest());

        LeaveRequest saved = pending;
        assertEquals(LeaveStatus.APPROVED, saved.getStatus());
        assertEquals(2, saved.getPaidDays());
        assertEquals(1, saved.getLopDays());
        assertEquals(3, saved.getPaidDays() + saved.getLopDays(),
                "PAID + LOP must add up to the request's working days");
        assertEquals(manager, saved.getApprovedBy(),
                "The approver must be the authenticated manager from the security context");

        // Only PAID days are deducted; LOP days never touch the balance.
        verify(leaveBalanceService).deductPaidLeaveDays(balance, saved, 2);
        verify(leaveBalanceService, never()).deductLeaveBalance(any(), any());

        // The per-month breakdown is persisted for date-accurate usage.
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LeaveRequestMonthAllocation>> rows =
                ArgumentCaptor.forClass((Class) List.class);
        verify(leaveRequestMonthAllocationRepository).saveAll(rows.capture());

        List<LeaveRequestMonthAllocation> savedRows = rows.getValue();
        assertEquals(1, savedRows.size());
        assertEquals(month.atDay(1), savedRows.get(0).getAllocationMonth());
        assertEquals(2, savedRows.get(0).getPaidDays());
        assertEquals(3, savedRows.get(0).getWorkingDays());
        assertEquals(saved, savedRows.get(0).getLeaveRequest());

        // Authorization and audit history.
        verify(leaveAuthorizationService).validateManagerApproval(manager, saved);
        verify(leaveApprovalService).createApproval(
                eq(saved), eq(manager), eq(ApprovalLevel.MANAGER),
                eq(LeaveAction.APPROVE), isNull());
    }

    @Test
    @DisplayName("A cross-year request deducts each year's PAID days from that year's balance")
    void crossYearRequestDeductsPerYear() {

        pending.setStartDate(LocalDate.of(2026, 12, 28));
        pending.setEndDate(LocalDate.of(2027, 1, 4));
        pending.setTotalDays(5);
        balance.setYear(2026);

        LeaveBalance balance2027 = LeaveBalance.builder()
                .employee(employee)
                .leaveType(paidType)
                .year(2027)
                .allocatedLeaves(24)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(20)
                .build();

        givenApprovableRequest();
        when(leaveBalanceService.getLeaveBalanceEntityForUpdate(
                eq(employee), eq(paidType), eq(LocalDate.of(2027, 1, 1))))
                .thenReturn(balance2027);

        when(leavePaidLopService.classify(eq(employee), eq(paidType), any(LocalDate.class), any(LocalDate.class), any()))
                .thenReturn(new PaidLopAllocation(3, 2, List.of(
                        new PaidLopAllocation.MonthAllocation(YearMonth.of(2026, 12), 3, 2, 2, 1),
                        new PaidLopAllocation.MonthAllocation(YearMonth.of(2027, 1), 2, 2, 1, 1)
                )));

        leaveRequestService.managerAction(REQUEST_ID, approveRequest());

        // 2 days from the 2026 balance, 1 day from the 2027 balance - January
        // days are never charged to December's balance.
        verify(leaveBalanceService).deductPaidLeaveDays(balance, pending, 2);
        verify(leaveBalanceService).deductPaidLeaveDays(balance2027, pending, 1);
        assertEquals(3, pending.getPaidDays());
        assertEquals(2, pending.getLopDays());
    }

    @Test
    @DisplayName("An already-approved request cannot be approved twice")
    void duplicateApprovalIsRejected() {

        pending.setStatus(LeaveStatus.APPROVED);

        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(leaveRequestRepository.findByIdForUpdate(REQUEST_ID))
                .thenReturn(Optional.of(pending));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.managerAction(REQUEST_ID, approveRequest()));

        assertEquals(ErrorCode.LEAVE_ALREADY_PROCESSED, error.getErrorCode());
        verify(leavePaidLopService, never()).classify(any(), any(), any(), any(), any());
        verify(leaveBalanceService, never()).deductPaidLeaveDays(any(), any(), anyInt0());
        verify(leaveRequestMonthAllocationRepository, never()).saveAll(any());
        assertNull(pending.getApprovedBy(),
                "A failed approval must not leave a partial approver behind");
    }

    @Test
    @DisplayName("A manager cannot approve their own leave")
    void managerCannotApproveOwnLeave() {

        pending.setEmployee(manager);

        when(employeeService.getCurrentEmployee()).thenReturn(manager);
        when(leaveRequestRepository.findByIdForUpdate(REQUEST_ID))
                .thenReturn(Optional.of(pending));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.managerAction(REQUEST_ID, approveRequest()));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
        verify(leaveBalanceService, never()).deductPaidLeaveDays(any(), any(), anyInt0());
    }

    @Test
    @DisplayName("An unauthorized manager is rejected before anything is touched")
    void unauthorizedManagerIsRejected() {

        givenRequestLocked();
        org.mockito.Mockito.doThrow(new BadRequestException(
                        "You are not authorized to approve this leave.", ErrorCode.NOT_ALLOWED))
                .when(leaveAuthorizationService)
                .validateManagerApproval(manager, pending);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.managerAction(REQUEST_ID, approveRequest()));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
        verify(leaveBalanceService, never()).deductPaidLeaveDays(any(), any(), anyInt0());
        verify(leaveBalanceService, never()).getLeaveBalanceEntityForUpdate(any(), any(), any());
        verify(leaveRequestMonthAllocationRepository, never()).saveAll(any());
        assertEquals(LeaveStatus.PENDING, pending.getStatus());
    }

    @Test
    @DisplayName("A cross-month request persists one attribution row per touched month")
    void crossMonthRequestPersistsOneRowPerMonth() {

        pending.setEndDate(pending.getStartDate().plusDays(6));

        LocalDate start = pending.getStartDate();
        YearMonth first = YearMonth.from(start);
        YearMonth second = first.plusMonths(1);

        givenApprovableRequest();
        when(leavePaidLopService.classify(eq(employee), eq(paidType), eq(start), eq(pending.getEndDate()), any()))
                .thenReturn(new PaidLopAllocation(3, 2, List.of(
                        new PaidLopAllocation.MonthAllocation(first, 3, 2, 2, 1),
                        new PaidLopAllocation.MonthAllocation(second, 2, 2, 1, 1)
                )));

        leaveRequestService.managerAction(REQUEST_ID, approveRequest());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<LeaveRequestMonthAllocation>> rows =
                ArgumentCaptor.forClass((Class) List.class);
        verify(leaveRequestMonthAllocationRepository).saveAll(rows.capture());

        List<LeaveRequestMonthAllocation> savedRows = rows.getValue();
        assertEquals(2, savedRows.size(),
                "One row per touched month so each PAID day counts in its own month");
        assertEquals(first.atDay(1), savedRows.get(0).getAllocationMonth());
        assertEquals(second.atDay(1), savedRows.get(1).getAllocationMonth());
        assertEquals(2, savedRows.get(0).getPaidDays());
        assertEquals(1, savedRows.get(1).getPaidDays());
    }

    // -----------------------------------------------------------------------
    // Unpaid leave types
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Unpaid leave keeps the legacy full deduction, clamped to the balance")
    void unpaidLeaveDeductsLegacyButClamped() {

        pending.setLeaveType(unpaidType);
        pending.setTotalDays(4);

        // Only 2 days remain: the deduction must clamp and the stored split
        // must still add up (the 2 days the balance cannot cover are LOP).
        balance.setRemainingLeaves(2);
        balance.setUsedLeaves(22);

        givenApprovableRequest();

        leaveRequestService.managerAction(REQUEST_ID, approveRequest());

        assertEquals(2, pending.getPaidDays(),
                "Only the days the balance can cover are deducted");
        assertEquals(2, pending.getLopDays());
        assertEquals(4, pending.getPaidDays() + pending.getLopDays());

        verify(leaveBalanceService).deductLeaveBalance(balance, pending);
        verify(leavePaidLopService, never()).classify(any(), any(), any(), any(), any());
        verify(leaveRequestMonthAllocationRepository, never()).saveAll(any());
    }

    // -----------------------------------------------------------------------
    // Rejection
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Rejection records the acting manager in the audit trail but never deducts")
    void rejectionNeverDeducts() {

        givenRequestLocked();

        leaveRequestService.managerAction(REQUEST_ID, rejectRequest("Not approved"));

        assertEquals(LeaveStatus.REJECTED, pending.getStatus());
        assertNull(pending.getApprovedBy(),
                "approvedBy stays null for a rejected request");
        assertNull(pending.getPaidDays());
        assertNull(pending.getLopDays());

        verify(leaveBalanceService, never()).deductPaidLeaveDays(any(), any(), anyInt0());
        verify(leaveBalanceService, never()).deductLeaveBalance(any(), any());
        verify(leaveAuthorizationService).validateManagerApproval(manager, pending);
        verify(leaveApprovalService).createApproval(
                eq(pending), eq(manager), eq(ApprovalLevel.MANAGER),
                eq(LeaveAction.REJECT), eq("Not approved"));
    }

    @Test
    @DisplayName("Rejection without a reason is refused")
    void rejectionWithoutReasonIsRefused() {

        givenRequestLocked();

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.managerAction(REQUEST_ID, rejectRequest(" ")));

        assertEquals(ErrorCode.REASON_REQUIRED, error.getErrorCode());
        assertEquals(LeaveStatus.PENDING, pending.getStatus());
        verify(leaveBalanceService, never()).deductPaidLeaveDays(any(), any(), anyInt0());
    }

    // -----------------------------------------------------------------------
    // Cancellation
    // -----------------------------------------------------------------------

    private void givenOwnedRequestWithStatus(LeaveStatus status) {

        pending.setStatus(status);
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(leaveRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));
    }

    @Test
    @DisplayName("Pending leave can be cancelled by its owner")
    void pendingLeaveCanBeCancelled() {

        givenOwnedRequestWithStatus(LeaveStatus.PENDING);

        leaveRequestService.cancelLeave(REQUEST_ID);

        assertEquals(LeaveStatus.CANCELLED, pending.getStatus());
        // Submission never deducted anything, so cancellation restores nothing.
        verify(leaveBalanceService, never()).restoreLeaveBalance(any(), any());
    }

    @Test
    @DisplayName("Approved leave cannot be cancelled - no balance restore, ever")
    void approvedLeaveCannotBeCancelled() {

        givenOwnedRequestWithStatus(LeaveStatus.APPROVED);
        pending.setPaidDays(2);
        pending.setLopDays(1);
        pending.setApprovedBy(manager);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.cancelLeave(REQUEST_ID));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
        assertEquals(LeaveStatus.APPROVED, pending.getStatus(),
                "The request must stay approved");
        verify(leaveBalanceService, never()).restoreLeaveBalance(any(), any());
    }

    @Test
    @DisplayName("Rejected leave cannot be cancelled as though it were pending")
    void rejectedLeaveCannotBeCancelled() {

        givenOwnedRequestWithStatus(LeaveStatus.REJECTED);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.cancelLeave(REQUEST_ID));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
        assertEquals(LeaveStatus.REJECTED, pending.getStatus());
    }

    @Test
    @DisplayName("Already-cancelled leave reports LEAVE_ALREADY_CANCELLED")
    void alreadyCancelledLeaveIsRefused() {

        givenOwnedRequestWithStatus(LeaveStatus.CANCELLED);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.cancelLeave(REQUEST_ID));

        assertEquals(ErrorCode.LEAVE_ALREADY_CANCELLED, error.getErrorCode());
    }

    @Test
    @DisplayName("Another employee's leave cannot be cancelled")
    void foreignLeaveCannotBeCancelled() {

        Employee stranger = Employee.builder().firstName("Stranger").lastName("Other").build();
        stranger.setId(999L);

        when(employeeService.getCurrentEmployee()).thenReturn(stranger);
        when(leaveRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.of(pending));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveRequestService.cancelLeave(REQUEST_ID));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
        assertEquals(LeaveStatus.PENDING, pending.getStatus());
    }

    /** Small helper so anyInt-style matching reads clearly above. */
    private static int anyInt0() {
        return org.mockito.ArgumentMatchers.anyInt();
    }

    @Test
    @DisplayName("The approval body cannot carry an approver - the action type has no such field")
    void approvalRequestBodyHasNoApproverField() {

        // LeaveActionRequest only exposes action + reason; if an approver
        // field ever creeps in, this reflection check forces a deliberate
        // decision about where the approver comes from.
        java.lang.reflect.Field[] fields = LeaveActionRequest.class.getDeclaredFields();

        assertTrue(java.util.Arrays.stream(fields)
                        .noneMatch(field -> field.getName().toLowerCase().contains("approve")),
                "LeaveActionRequest must not carry an approver identity field; "
                        + "the approver comes from the security context only");
        assertTrue(java.util.Arrays.stream(fields)
                        .anyMatch(field -> field.getName().equals("action")));
        assertTrue(java.util.Arrays.stream(fields)
                        .noneMatch(field -> field.getName().equalsIgnoreCase("paidDays")));
        assertTrue(java.util.Arrays.stream(fields)
                        .noneMatch(field -> field.getName().equalsIgnoreCase("lopDays")));
    }
}
