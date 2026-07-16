package com.my_hourly.attendance.serviceImpl;

import com.my_hourly.attendance.entity.AttendancePolicy;
import com.my_hourly.attendance.repository.AttendancePolicyRepository;
import com.my_hourly.attendance.service.AttendancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttendancePolicyServiceImpl
        implements AttendancePolicyService {

    @Autowired
    private AttendancePolicyRepository repository;

    @Override
    public AttendancePolicy createPolicy(AttendancePolicy policy) {

        policy.setActive(true);

        return repository.save(policy);
    }

    @Override
    public AttendancePolicy getPolicy() {

        return repository.findByActiveTrue()
                .orElseThrow(() ->
                        new RuntimeException("Attendance Policy not found"));
    }

    @Override
    public AttendancePolicy updatePolicy(Long id,
                                         AttendancePolicy policy) {

        AttendancePolicy existing =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Policy not found"));

        existing.setOfficeStartTime(policy.getOfficeStartTime());
        existing.setOfficeEndTime(policy.getOfficeEndTime());
        existing.setGracePeriodMinutes(policy.getGracePeriodMinutes());
        existing.setMinimumWorkingHours(policy.getMinimumWorkingHours());
        existing.setHalfDayWorkingHours(policy.getHalfDayWorkingHours());
        existing.setSaturdayWorking(policy.getSaturdayWorking());
        existing.setSundayWorking(policy.getSundayWorking());
        existing.setHolidayCheckInAllowed(policy.getHolidayCheckInAllowed());

        return repository.save(existing);
    }
}