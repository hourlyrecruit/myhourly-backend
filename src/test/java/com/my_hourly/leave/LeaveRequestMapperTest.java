package com.my_hourly.leave;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The API is the only place the PAID/LOP split and the approver are visible to
 * the frontend, so this pins that they actually reach the response.
 */
@DisplayName("Leave request response mapping")
class LeaveRequestMapperTest {

    private final LeaveRequestMapper mapper = new LeaveRequestMapper();

    private Employee employee;
    private Employee approver;
    private LeaveType leaveType;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {

        employee = Employee.builder()
                .employeeCode("EMP001")
                .firstName("John")
                .lastName("Test")
                .build();
        employee.setId(1L);

        approver = Employee.builder()
                .employeeCode("EMP002")
                .firstName("Mary")
                .lastName("Manager")
                .build();
        approver.setId(2L);

        leaveType = LeaveType.builder()
                .name("Annual Leave")
                .paid(true)
                .allocatedDays(24)
                .active(true)
                .build();
        leaveType.setId(10L);

        leaveRequest = LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(LocalDate.of(2026, 10, 5))
                .endDate(LocalDate.of(2026, 10, 8))
                .totalDays(4)
                .reason("Personal")
                .status(LeaveStatus.PENDING)
                .build();
        leaveRequest.setId(100L);
    }

    @Test
    @DisplayName("An approved request exposes its PAID/LOP split and approver")
    void approvedRequestExposesTheSplitAndApprover() {

        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setPaidDays(2);
        leaveRequest.setLopDays(2);
        leaveRequest.setApprovedBy(approver);

        LeaveRequestResponse response = mapper.toResponse(leaveRequest);

        assertEquals(2, response.getPaidDays());
        assertEquals(2, response.getLopDays());

        assertEquals(2L, response.getApprovedById());
        assertEquals("Mary Manager", response.getApprovedByName());
        assertEquals("EMP002", response.getApprovedByCode());
    }

    @Test
    @DisplayName("A pending request exposes no split and no approver")
    void pendingRequestExposesNoSplitAndNoApprover() {

        LeaveRequestResponse response = mapper.toResponse(leaveRequest);

        assertNull(response.getPaidDays(),
                "The split is decided at approval, not submission");
        assertNull(response.getLopDays());
        assertNull(response.getApprovedById());
        assertNull(response.getApprovedByName());
        assertNull(response.getApprovedByCode());
    }

    @Test
    @DisplayName("An approver without a last name still exposes a usable name")
    void approverWithoutLastNameStillHasAName() {

        approver.setLastName(null);
        leaveRequest.setStatus(LeaveStatus.APPROVED);
        leaveRequest.setPaidDays(0);
        leaveRequest.setLopDays(4);
        leaveRequest.setApprovedBy(approver);

        LeaveRequestResponse response = mapper.toResponse(leaveRequest);

        assertEquals("Mary", response.getApprovedByName());
        assertEquals(0, response.getPaidDays(),
                "An entirely LOP approval exposes zero PAID days");
        assertEquals(4, response.getLopDays());
    }
}
