package com.my_hourly.leave;

import com.my_hourly.authentication.entity.RoleName;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserStatus;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.entity.EmploymentType;
import com.my_hourly.employee.entity.Gender;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.leave.dto.PaidLopAllocation;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.entity.LeaveRequestMonthAllocation;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.enums.LeaveStatus;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveRequestMonthAllocationRepository;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeavePaidLopService;
import com.my_hourly.master.entity.Department;
import com.my_hourly.master.entity.Designation;
import com.my_hourly.master.entity.JobTitle;
import com.my_hourly.master.repository.DepartmentRepository;
import com.my_hourly.master.repository.DesignationRepository;
import com.my_hourly.master.repository.JobTitleRepository;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.service.LeaveSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Date-accurate monthly PAID-usage attribution against a REAL database.
 *
 * <p>Pins requirement "the monthly usage query counts leave days by their
 * actual dates rather than attributing every day of a multi-month request to
 * the request's start month":</p>
 *
 * <ul>
 *   <li>an approved request spanning October/November contributes its October
 *       PAID days to October usage and its November PAID days to November
 *       usage - never all of them to the start month;</li>
 *   <li>legacy rows without a per-month breakdown keep the old start-date
 *       attribution (their split was never stored);</li>
 *   <li>pending requests and unpaid leave types never consume the paid
 *       allowance;</li>
 *   <li>the classifier sees the same numbers, so a later approval in the
 *       second month receives only the remaining allowance.</li>
 * </ul>
 *
 * <p>This runs with the application's configured datasource (the same one the
 * Flyway migration and schema validation run against); everything it writes is
 * rolled back.</p>
 */
@SpringBootTest
@Transactional
@DisplayName("Monthly PAID usage attribution (real database)")
class LeaveMonthlyUsageIntegrationTest {

    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final YearMonth NOVEMBER = YearMonth.of(2026, 11);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private LeaveRequestMonthAllocationRepository allocationRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private JobTitleRepository jobTitleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LeavePaidLopService leavePaidLopService;

    @Autowired
    private LeaveSettingsService leaveSettingsService;

    private Employee employee;
    private LeaveType paidType;

    @BeforeEach
    void fixture() {

        String uid = Long.toString(System.nanoTime());

        Department department = departmentRepository.save(Department.builder()
                .departmentCode("DU" + uid)
                .departmentName("Usage Attribution Dept")
                .build());

        Designation designation = designationRepository.save(Designation.builder()
                .designationCode("GU" + uid)
                .designationName("Usage Attribution Designation")
                .department(department)
                .build());

        JobTitle jobTitle = jobTitleRepository.save(JobTitle.builder()
                .jobTitleCode("JU" + uid)
                .jobTitle("Usage Attribution Title")
                .designation(designation)
                .build());

        User user = userRepository.save(User.builder()
                .username("u" + uid)
                .email("usage." + uid + "@example.test")
                .password("x")
                .role(RoleName.EMPLOYEE)
                .userStatus(UserStatus.ACTIVE)
                .build());

        employee = employeeRepository.save(Employee.builder()
                .employeeCode("EU" + uid)
                .firstName("Usage")
                .lastName("Attribution")
                .email("usage." + uid + "@example.test")
                .phoneNumber("9999999999")
                .gender(Gender.FEMALE)
                .employmentType(EmploymentType.FULL_TIME)
                .roleName(RoleName.EMPLOYEE)
                .user(user)
                .department(department)
                .designation(designation)
                .jobTitle(jobTitle)
                .build());

        paidType = leaveTypeRepository.findByNameIgnoreCase("ANNUAL")
                .orElseGet(() -> leaveTypeRepository.save(LeaveType.builder()
                        .name("ANNUAL")
                        .paid(true)
                        .allocatedDays(24)
                        .monthlyGuideline(2)
                        .carryForwardAllowed(false)
                        .active(true)
                        .build()));

        leaveBalanceRepository.save(LeaveBalance.builder()
                .employee(employee)
                .leaveType(paidType)
                .year(2026)
                .allocatedLeaves(24)
                .usedLeaves(0)
                .expiredLeaves(0)
                .remainingLeaves(20)
                .build());
    }

    private LeaveRequest approvedRequest(LocalDate start, LocalDate end,
                                         int totalDays, Integer paidDays) {

        LeaveRequest request = leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee)
                .leaveType(paidType)
                .startDate(start)
                .endDate(end)
                .totalDays(totalDays)
                .reason("usage attribution fixture")
                .status(LeaveStatus.APPROVED)
                .paidDays(paidDays)
                .lopDays(paidDays == null ? null : totalDays - paidDays)
                .build());

        return request;
    }

    private void allocate(LeaveRequest request, YearMonth month, int paid, int working) {

        allocationRepository.save(LeaveRequestMonthAllocation.builder()
                .leaveRequest(request)
                .allocationMonth(month.atDay(1))
                .paidDays(paid)
                .workingDays(working)
                .build());
    }

    private int paidUsage(YearMonth month) {
        return leaveRequestRepository.sumPaidLeaveDaysInMonth(
                employee, month.atDay(1), month.atEndOfMonth());
    }

    // -----------------------------------------------------------------------
    // Date attribution
    // -----------------------------------------------------------------------

    @Test
    void crossMonthRequestCountsEachDayInItsOwnMonth() {

        // Oct 29 (Thu) - Nov 3 (Tue) 2026: 2 working days in October,
        // 2 working days in November, all four classified PAID.
        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, 2, 2);
        allocate(cross, NOVEMBER, 2, 2);

        assertEquals(2, paidUsage(OCTOBER),
                "October usage must contain only the PAID days that fall in October");
        assertEquals(2, paidUsage(NOVEMBER),
                "November usage must contain only the PAID days that fall in November - "
                        + "not the whole request attributed to the start month");
        assertEquals(0, paidUsage(YearMonth.of(2026, 12)),
                "No day may leak into a month it does not belong to");
    }

    @Test
    void subsequentApprovalSeesTheSecondMonthsRemainingAllowanceOnly() {

        int guideline = leaveSettingsService.getSettings().getMonthlyGuideline();
        assertNotNull(guideline);

        // An October/November request that already spent the FULL monthly
        // allowance in each of its two months - the exact per-month figures the
        // classifier will read back for a later approval.
        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, guideline, Math.max(guideline, 2));
        allocate(cross, NOVEMBER, guideline, Math.max(guideline, 2));

        assertEquals(guideline, paidUsage(NOVEMBER),
                "November usage must equal the figures stored at approval time");

        LeaveRequest laterInNovember = LeaveRequest.builder()
                .employee(employee)
                .leaveType(paidType)
                .startDate(LocalDate.of(2026, 11, 16)) // Monday
                .endDate(LocalDate.of(2026, 11, 17))
                .totalDays(2)
                .reason("later november request")
                .status(LeaveStatus.PENDING)
                .build();

        leaveRequestRepository.save(laterInNovember);

        PaidLopAllocation allocation = leavePaidLopService.classify(
                employee, paidType,
                laterInNovember.getStartDate(), laterInNovember.getEndDate());

        // November's allowance is already fully spent by the cross-month
        // request - nothing more can be PAID in November, whatever the
        // configured guideline, while October's usage stays out of the way.
        assertEquals(0, allocation.paidDays(),
                "November's allowance is already spent by the cross-month request; "
                        + "the later request must be entirely LOP");
        assertTrue(allocation.totalDays() >= 1,
                "The later request must contain at least one working day");
        assertEquals(allocation.totalDays(), allocation.lopDays(),
                "Every working day of the later request is LOP");
    }

    @Test
    void legacyRequestsKeepStartMonthAttribution() {

        // Approved before the per-month table existed: paid_days is null and
        // there are no allocation rows. Its split is unrecoverable, so it keeps
        // the documented start-date fallback - and must not be counted twice.
        LeaveRequest legacy = approvedRequest(
                LocalDate.of(2026, 10, 30), LocalDate.of(2026, 11, 2), 3, null);

        assertEquals(3, paidUsage(OCTOBER),
                "A legacy row counts as fully PAID in its start month");
        assertEquals(0, paidUsage(NOVEMBER),
                "A legacy row must not also appear in the month it spans into");
    }

    @Test
    void pendingAndUnpaidRowsNeverConsumeTheAllowance() {

        // Pending request with a (hypothetical) paid value: status wins.
        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee)
                .leaveType(paidType)
                .startDate(LocalDate.of(2026, 10, 5))
                .endDate(LocalDate.of(2026, 10, 6))
                .totalDays(2)
                .reason("pending fixture")
                .status(LeaveStatus.PENDING)
                .paidDays(2)
                .lopDays(0)
                .build());

        // Approved row of an UNPAID leave type, legacy shape: excluded from
        // the paid allowance entirely (its days are not salary-paid).
        LeaveType unpaidType = leaveTypeRepository.findByNameIgnoreCase("UNPAID")
                .orElseGet(() -> leaveTypeRepository.save(LeaveType.builder()
                        .name("UNPAID")
                        .paid(false)
                        .allocatedDays(0)
                        .monthlyGuideline(0)
                        .carryForwardAllowed(false)
                        .active(true)
                        .build()));

        leaveRequestRepository.save(LeaveRequest.builder()
                .employee(employee)
                .leaveType(unpaidType)
                .startDate(LocalDate.of(2026, 10, 7))
                .endDate(LocalDate.of(2026, 10, 8))
                .totalDays(2)
                .reason("unpaid fixture")
                .status(LeaveStatus.APPROVED)
                .paidDays(2)
                .lopDays(0)
                .build());

        // A genuine approved PAID row still counts.
        LeaveRequest approved = approvedRequest(
                LocalDate.of(2026, 10, 9), LocalDate.of(2026, 10, 9), 1, 1);
        allocate(approved, OCTOBER, 1, 1);

        assertEquals(1, paidUsage(OCTOBER),
                "Only the approved PAID row may count - pending and unpaid rows must not");
    }

    @Test
    void groupedUsageMatchesThePerEmployeeSums() {

        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, 2, 2);
        allocate(cross, NOVEMBER, 2, 2);

        Map<Long, Integer> october = leaveRequestRepository
                .sumPaidLeaveDaysInMonthGrouped(OCTOBER.atDay(1), OCTOBER.atEndOfMonth())
                .stream()
                .collect(Collectors.toMap(
                        LeaveRequestRepository.PaidDaysProjection::getEmployeeId,
                        LeaveRequestRepository.PaidDaysProjection::getPaidDays));

        Map<Long, Integer> november = leaveRequestRepository
                .sumPaidLeaveDaysInMonthGrouped(NOVEMBER.atDay(1), NOVEMBER.atEndOfMonth())
                .stream()
                .collect(Collectors.toMap(
                        LeaveRequestRepository.PaidDaysProjection::getEmployeeId,
                        LeaveRequestRepository.PaidDaysProjection::getPaidDays));

        assertEquals(2, october.get(employee.getId()),
                "The month-end report must see the same October figure as the allowance query");
        assertEquals(2, november.get(employee.getId()),
                "The month-end report must see the same November figure as the allowance query");
    }

    @Test
    void allocationRowsSurviveOnlyWhileTheirRequestExists() {

        LeaveRequest cross = approvedRequest(
                LocalDate.of(2026, 10, 29), LocalDate.of(2026, 11, 3), 4, 4);
        allocate(cross, OCTOBER, 2, 2);
        allocate(cross, NOVEMBER, 2, 2);

        Long requestId = cross.getId();

        // Verify ON DELETE CASCADE at the database level. We use native SQL for
        // both the delete and the count so that Hibernate's persistence-context
        // checks on the still-managed allocation entities cannot block the test.
        int before = ((Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM leave_request_month_allocations WHERE leave_request_id = ?")
                .setParameter(1, requestId)
                .getSingleResult()).intValue();
        assertTrue(before > 0, "Expected allocation rows to exist before delete");

        entityManager.createNativeQuery(
                "DELETE FROM leave_requests WHERE id = ?")
                .setParameter(1, requestId)
                .executeUpdate();

        int after = ((Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM leave_request_month_allocations WHERE leave_request_id = ?")
                .setParameter(1, requestId)
                .getSingleResult()).intValue();

        assertTrue(after == 0,
                "Allocation rows must be removed with their request (ON DELETE CASCADE), "
                        + "so a hard-deleted leave type cannot leave orphans behind");
    }
}
