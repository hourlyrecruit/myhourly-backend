package com.my_hourly.attendance.controller;

import com.my_hourly.attendance.entity.AttendancePolicy;
import com.my_hourly.attendance.service.AttendancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance-policy")
@CrossOrigin(origins = "*")
public class AttendancePolicyController {

    @Autowired
    private AttendancePolicyService attendancePolicyService;

    @PostMapping
    public AttendancePolicy createPolicy(@RequestBody AttendancePolicy policy) {
        return attendancePolicyService.createPolicy(policy);
    }

    @GetMapping
    public AttendancePolicy getPolicy() {
        return attendancePolicyService.getPolicy();
    }

    @PutMapping("/{id}")
    public AttendancePolicy updatePolicy(@PathVariable Long id,
                                         @RequestBody AttendancePolicy policy) {
        return attendancePolicyService.updatePolicy(id, policy);
    }
}