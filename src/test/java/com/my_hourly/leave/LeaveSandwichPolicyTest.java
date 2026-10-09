package com.my_hourly.leave;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.repository.HolidayRepository;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.context.LeaveApplicationContext;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveBalanceRepository;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.leave.service.LeaveValidationService;
import com.my_hourly.settings.leave.entity.LeaveSettings;
import com.my_hourly.settings.leave.repository.LeaveSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LeaveSandwichPolicyTest {

    @Autowired
    private LeaveValidationService leaveValidationService;

    @Autowired
    private LeaveSettingsRepository leaveSettingsRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private HolidayRepository holidayRepository;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveSettings leaveSettings;

    @BeforeEach
    void setUp() {
        // Create test employee
        employee = Employee.builder()
                .employeeCode("EMP001")
                .firstName("Test")
                .lastName("Employee")
                .email("test@example.com")
                .dateOfJoining(LocalDate.now().minusYears(1))
                .active(true)
                .build();

        // Create test leave type
        leaveType = LeaveType.builder()
                .name("Annual Leave")
                .code("AL")
                .active(true)
                .build();
        leaveType = leaveTypeRepository.save(leaveType);

        // Create leave balance
        LeaveBalance leaveBalance = LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(LocalDate.now().getYear())
                .totalLeaves(24)
                .usedLeaves(0)
                .remainingLeaves(24)
                .build();
        leaveBalanceRepository.save(leaveBalance);

        // Get or create leave settings
        leaveSettings = leaveSettingsRepository.findFirstByOrderByIdAsc()
                .orElse(LeaveSettings.builder()
                        .carryForwardAllowed(false)
                        .monthlyGuideline(2)
                        .annualPaidLeave(24)
                        .sandwichLeaveMondayEnabled(false)
                        .sandwichLeaveFridayEnabled(false)
                        .sandwichLeaveFridayMondayEnabled(false)
                        .active(true)
                        .build());
        leaveSettings = leaveSettingsRepository.save(leaveSettings);
    }

    @Test
    void testNoRulesEnabled_FridayOnly_OneDayCharged() {
        // Arrange: Friday Oct 27, 2023
        LocalDate friday = LocalDate.of(2023, 10, 27);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                friday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(1, context.totalDays(), "Should charge only 1 day when no rules enabled");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days");
    }

    @Test
    void testNoRulesEnabled_MondayOnly_OneDayCharged() {
        // Arrange: Monday Oct 30, 2023
        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                monday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(1, context.totalDays(), "Should charge only 1 day when no rules enabled");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days");
    }

    @Test
    void testNoRulesEnabled_FridayPlusMonday_TwoDaysCharged() {
        // Arrange: Friday Oct 27 to Monday Oct 30, 2023
        LocalDate friday = LocalDate.of(2023, 10, 27);
        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(2, context.totalDays(), "Should charge only 2 working days (Fri + Mon)");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days");
    }

    @Test
    void testMondayRuleEnabled_MondayLeave_ThreeDaysCharged() {
        // Arrange: Enable Monday rule
        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                monday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(3, context.totalDays(), "Should charge 3 days (Sat + Sun + Mon)");
        assertEquals(2, context.forcedWorkingDays().size(), "Should have 2 forced working days");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Saturday should be forced");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sunday should be forced");
    }

    @Test
    void testMondayRuleEnabled_TuesdayLeave_OneDayCharged() {
        // Arrange: Enable Monday rule
        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate tuesday = LocalDate.of(2023, 10, 31);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                tuesday,
                tuesday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(1, context.totalDays(), "Tuesday should charge only 1 day");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days for Tuesday");
    }

    @Test
    void testFridayRuleEnabled_FridayLeave_ThreeDaysCharged() {
        // Arrange: Enable Friday rule
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                friday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(3, context.totalDays(), "Should charge 3 days (Fri + Sat + Sun)");
        assertEquals(2, context.forcedWorkingDays().size(), "Should have 2 forced working days");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Saturday should be forced");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sunday should be forced");
    }

    @Test
    void testFridayRuleEnabled_ThursdayLeave_OneDayCharged() {
        // Arrange: Enable Friday rule
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate thursday = LocalDate.of(2023, 10, 26);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                thursday,
                thursday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(1, context.totalDays(), "Thursday should charge only 1 day");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days for Thursday");
    }

    @Test
    void testFridayMondayRuleEnabled_FridayPlusMonday_FourDaysCharged() {
        // Arrange: Enable Friday+Monday rule
        leaveSettings.setSandwichLeaveFridayMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(4, context.totalDays(), "Should charge 4 days (Fri + Sat + Sun + Mon)");
        assertEquals(2, context.forcedWorkingDays().size(), "Should have 2 forced working days");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Saturday should be forced");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sunday should be forced");
    }

    @Test
    void testFridayMondayRuleEnabled_FridayOnly_OneDayCharged() {
        // Arrange: Enable Friday+Monday rule (but apply for Friday only)
        leaveSettings.setSandwichLeaveFridayMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                friday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(1, context.totalDays(), "Should charge only 1 day when Monday not included");
        assertTrue(context.forcedWorkingDays().isEmpty(), "No forced working days");
    }

    @Test
    void testAllRulesEnabled_FridayPlusMondaySpan_PrecedenceWorks() {
        // Arrange: Enable all three rules
        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettings.setSandwichLeaveFridayMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(4, context.totalDays(), "Should charge 4 days total");
        assertEquals(2, context.forcedWorkingDays().size(), "Should have exactly 2 forced working days (no double-counting)");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Saturday counted once");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sunday counted once");
    }

    @Test
    void testAllRulesEnabled_FridayOnly_FridayRuleFires() {
        // Arrange: Enable all three rules
        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettings.setSandwichLeaveFridayMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                friday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(3, context.totalDays(), "Friday-only rule should fire (3 days)");
        assertEquals(2, context.forcedWorkingDays().size());
    }

    @Test
    void testAllRulesEnabled_MondayOnly_MondayRuleFires() {
        // Arrange: Enable all three rules
        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettings.setSandwichLeaveFridayMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                monday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(3, context.totalDays(), "Monday-only rule should fire (3 days)");
        assertEquals(2, context.forcedWorkingDays().size());
    }

    @Test
    void testMondayRuleWithPublicHolidayOnSaturday_StillThreeDays() {
        // Arrange: Create public holiday on Saturday
        Holiday saturdayHoliday = Holiday.builder()
                .holidayDate(LocalDate.of(2023, 10, 28))
                .name("Test Holiday")
                .active(true)
                .build();
        holidayRepository.save(saturdayHoliday);

        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                monday,
                monday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert: Sandwich weekends are counted regardless of public holidays
        assertEquals(3, context.totalDays(), "Should still charge 3 days even with holiday on Saturday");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Holiday Saturday still forced");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sunday still forced");
    }

    @Test
    void testFridayRuleCrossingMonthBoundary() {
        // Arrange: Friday Oct 31, 2023 (last day of October)
        leaveSettings.setSandwichLeaveFridayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate friday = LocalDate.of(2023, 10, 27);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                friday,
                friday,
                "Test leave"
        );

        // Act
        LeaveApplicationContext context = leaveValidationService.validateLeaveApplication(employee, request);

        // Assert
        assertEquals(3, context.totalDays());
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 28)), "Sat in Oct");
        assertTrue(context.forcedWorkingDays().contains(LocalDate.of(2023, 10, 29)), "Sun in Oct");
    }

    @Test
    void testInsufficientBalanceWithMondayRule() {
        // Arrange: Employee has only 2 days balance
        LeaveBalance balance = leaveBalanceRepository.findByEmployeeAndLeaveTypeAndYear(
                employee, leaveType, LocalDate.now().getYear()).orElseThrow();
        balance.setRemainingLeaves(2);
        balance.setUsedLeaves(22);
        leaveBalanceRepository.save(balance);

        leaveSettings.setSandwichLeaveMondayEnabled(true);
        leaveSettingsRepository.save(leaveSettings);

        LocalDate monday = LocalDate.of(2023, 10, 30);
        LeaveRequestRequest request = new LeaveRequestRequest(
                leaveType.getId(),
                monday,
                monday,
                "Test leave"
        );

        // Act & Assert: Should fail validation
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            leaveValidationService.validateLeaveApplication(employee, request);
        });

        assertEquals(ErrorCode.INSUFFICIENT, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Insufficient leave balance"));
    }
}
