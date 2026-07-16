package com.my_hourly.attendance.service;

import com.my_hourly.attendance.entity.AttendancePolicy;

public interface AttendancePolicyService {

    AttendancePolicy createPolicy(AttendancePolicy policy);

    AttendancePolicy updatePolicy(Long id, AttendancePolicy policy);

    AttendancePolicy getPolicy();

}