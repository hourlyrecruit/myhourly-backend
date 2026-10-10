package com.my_hourly.leave;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.leave.api.response.LeaveRequestResponse;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.mapper.LeaveRequestMapper;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.service.impl.LeaveRequestServiceImpl;
import com.my_hourly.leave.specification.LeaveSpecification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pins the month/year filtering added to the leave-request list endpoints:
 *
 * <ul>
 *   <li>{@link LeaveSpecification#resolveRange} maps the optional query
 *       parameters to an inclusive date range (or none);</li>
 *   <li>{@link LeaveSpecification#overlaps} keeps a request when its date range
 *       intersects the selected period;</li>
 *   <li>the service delegates to a specification-based query for both the
 *       "my requests" and "all requests" endpoints.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Leave request month filter")
class LeaveRequestMonthFilterTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    // -----------------------------------------------------------------------
    // Range resolution
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("No month or year means no filter")
    void noParamsMeansNoFilter() {

        assertNull(LeaveSpecification.resolveRange(null, null));
    }

    @Test
    @DisplayName("Month and year resolve to that calendar month")
    void monthAndYearResolveToTheMonth() {

        LeaveSpecification.MonthRange range = LeaveSpecification.resolveRange(10, 2026);

        assertEquals(LocalDate.of(2026, 10, 1), range.from());
        assertEquals(LocalDate.of(2026, 10, 31), range.to());
    }

    @Test
    @DisplayName("Month alone defaults to the current year")
    void monthAloneUsesCurrentYear() {

        LeaveSpecification.MonthRange range = LeaveSpecification.resolveRange(2, null);

        YearMonth february = YearMonth.of(LocalDate.now().getYear(), 2);
        assertEquals(february.atDay(1), range.from());
        assertEquals(february.atEndOfMonth(), range.to());
    }

    @Test
    @DisplayName("Year alone resolves to the whole year")
    void yearAloneResolvesToTheWholeYear() {

        LeaveSpecification.MonthRange range = LeaveSpecification.resolveRange(null, 2026);

        assertEquals(LocalDate.of(2026, 1, 1), range.from());
        assertEquals(LocalDate.of(2026, 12, 31), range.to());
    }

    @Test
    @DisplayName("An out-of-range month is rejected")
    void invalidMonthIsRejected() {

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> LeaveSpecification.resolveRange(13, 2026));

        assertEquals(ErrorCode.VALIDATION_FAILED, error.getErrorCode());
    }

    @Test
    @DisplayName("An out-of-range year is rejected")
    void invalidYearIsRejected() {

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> LeaveSpecification.resolveRange(1, 1999));

        assertEquals(ErrorCode.VALIDATION_FAILED, error.getErrorCode());
    }

    // -----------------------------------------------------------------------
    // Overlap predicate
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Overlap keeps requests whose range intersects the period")
    @SuppressWarnings({"unchecked", "rawtypes"})
    void overlapSpecificationChecksBothEdgesInclusive() {

        Root<LeaveRequest> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Path startDate = mock(Path.class);
        Path endDate = mock(Path.class);
        doReturn(startDate).when(root).get("startDate");
        doReturn(endDate).when(root).get("endDate");

        Predicate startBeforeTo = mock(Predicate.class);
        Predicate endAfterFrom = mock(Predicate.class);
        Predicate combined = mock(Predicate.class);

        when(cb.lessThanOrEqualTo(any(Expression.class), any(Comparable.class)))
                .thenReturn(startBeforeTo);
        when(cb.greaterThanOrEqualTo(any(Expression.class), any(Comparable.class)))
                .thenReturn(endAfterFrom);
        when(cb.and(any(Predicate.class), any(Predicate.class))).thenReturn(combined);

        LocalDate from = LocalDate.of(2026, 10, 1);
        LocalDate to = LocalDate.of(2026, 10, 31);

        Predicate result = LeaveSpecification.overlaps(from, to)
                .toPredicate(root, query, cb);

        assertSame(combined, result);
        verify(cb).lessThanOrEqualTo(same(startDate), eq(to));
        verify(cb).greaterThanOrEqualTo(same(endDate), eq(from));
        verify(cb).and(same(startBeforeTo), same(endAfterFrom));
    }

    // -----------------------------------------------------------------------
    // Service delegation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("My requests are fetched through a specification when a month is given")
    void myRequestsUseSpecificationWhenFiltered() {

        Employee employee = Employee.builder().firstName("John").lastName("Test").build();
        employee.setId(1L);
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        LeaveRequest request = new LeaveRequest();
        LeaveRequestResponse response = LeaveRequestResponse.builder().id(9L).build();
        when(leaveRequestRepository.findAll(any(Specification.class))).thenReturn(List.of(request));
        when(leaveRequestMapper.toResponse(request)).thenReturn(response);

        List<LeaveRequestResponse> result = leaveRequestService.getMyLeaveRequests(10, 2026);

        assertEquals(List.of(response), result);
        verify(leaveRequestRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("My requests without a filter still use the employee-scoped query")
    void myRequestsWithoutFilterStillScopedToEmployee() {

        Employee employee = Employee.builder().firstName("John").lastName("Test").build();
        employee.setId(1L);
        when(employeeService.getCurrentEmployee()).thenReturn(employee);
        when(leaveRequestRepository.findAll(any(Specification.class))).thenReturn(List.of());

        assertEquals(List.of(), leaveRequestService.getMyLeaveRequests(null, null));
        verify(leaveRequestRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("All requests without a filter pass no specification")
    void allRequestsWithoutFilterPassNoSpecification() {

        when(leaveRequestRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<LeaveRequest>>isNull()))
                .thenReturn(List.of());

        assertEquals(List.of(), leaveRequestService.getAllLeaveRequests(null, null));
        verify(leaveRequestRepository).findAll(
                org.mockito.ArgumentMatchers.<Specification<LeaveRequest>>isNull());
    }
}
