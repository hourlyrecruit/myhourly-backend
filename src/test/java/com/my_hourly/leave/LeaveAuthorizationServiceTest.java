package com.my_hourly.leave;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.impl.LeaveAuthorizationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins who may act on a leave request: only the employee's own reporting
 * manager passes {@code validateManagerApproval}. This is the check that stops
 * an arbitrary MANAGER-role user from approving someone else's leave.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Manager approval authorization")
class LeaveAuthorizationServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveAuthorizationServiceImpl leaveAuthorizationService;

    private Employee manager;
    private Employee otherManager;
    private Employee employee;
    private LeaveRequest request;

    @BeforeEach
    void setUp() {

        manager = Employee.builder().firstName("Rohit").lastName("Sharma").build();
        manager.setId(7L);

        otherManager = Employee.builder().firstName("Arjun").lastName("Rao").build();
        otherManager.setId(8L);

        employee = Employee.builder().firstName("Jitendra").lastName("Prajapati").build();
        employee.setId(101L);
        employee.setReportingManager(manager);

        request = LeaveRequest.builder().employee(employee).build();
    }

    @Test
    @DisplayName("The employee's own reporting manager may approve")
    void reportingManagerMayApprove() {

        assertDoesNotThrow(
                () -> leaveAuthorizationService.validateManagerApproval(manager, request));
    }

    @Test
    @DisplayName("Any other manager is refused with NOT_ALLOWED")
    void anyOtherManagerIsRefused() {

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveAuthorizationService.validateManagerApproval(otherManager, request));

        assertEquals(ErrorCode.NOT_ALLOWED, error.getErrorCode());
    }

    @Test
    @DisplayName("A missing reporting manager is refused, not silently allowed")
    void missingReportingManagerIsRefused() {

        employee.setReportingManager(null);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> leaveAuthorizationService.validateManagerApproval(manager, request));

        assertEquals(ErrorCode.REPORTING_MANAGER_NOT_ASSIGNED, error.getErrorCode());
    }
}
