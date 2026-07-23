package com.my_hourly.leave.service;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.api.request.LeaveRequestRequest;
import com.my_hourly.leave.context.LeaveApplicationContext;

public interface LeaveValidationService {

    LeaveApplicationContext validateLeaveApplication(
            Employee employee,
            LeaveRequestRequest request);

}