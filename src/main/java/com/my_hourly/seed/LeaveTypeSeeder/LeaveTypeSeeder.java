package com.my_hourly.seed.LeaveTypeSeeder;

import com.my_hourly.leave.entity.LeaveType;
import com.my_hourly.leave.repository.LeaveTypeRepository;
import com.my_hourly.seed.config.CsvReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaveTypeSeeder {

    private final LeaveTypeRepository leaveTypeRepository;
    private final CsvReader csvReader;

    @Transactional
    public void seed() {
        if (leaveTypeRepository.count() > 0) {
            log.info("Leave types already seeded. Skipping...");
            return;
        }

        // Seed standard leave types
        seedLeaveType("Annual Leave", "Paid annual leave entitlement", true, 24, 2, false);
        seedLeaveType("Sick Leave", "Paid sick leave for medical reasons", true, 12, 1, false);
        seedLeaveType("Casual Leave", "Short-notice personal leave", false, 6, 0, false);
        seedLeaveType("Maternity Leave", "Paid leave for maternity purposes", true, 90, 0, true);
        seedLeaveType("Paternity Leave", "Paid leave for paternity purposes", true, 15, 0, false);
        seedLeaveType("Unpaid Leave", "Unpaid leave with manager approval", false, 0, 0, false);

        log.info("Leave types seeded successfully.");
    }

    private void seedLeaveType(String name, String description, boolean paid,
                                int allocatedDays, int monthlyGuideline, boolean carryForward) {
        if (!leaveTypeRepository.existsByNameIgnoreCase(name)) {
            LeaveType leaveType = LeaveType.builder()
                    .name(name)
                    .description(description)
                    .paid(paid)
                    .allocatedDays(allocatedDays)
                    .monthlyGuideline(monthlyGuideline)
                    .carryForwardAllowed(carryForward)
                    .active(true)
                    .build();
            leaveTypeRepository.save(leaveType);
            log.info("Seeded leave type: {}", name);
        }
    }
}
