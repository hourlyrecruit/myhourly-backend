package com.my_hourly.leave.context;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveBalance;
import com.my_hourly.leave.entity.LeaveType;

public record LeaveApplicationContext(

        Employee employee,

        LeaveType leaveType,

        LeaveBalance leaveBalance,

        Integer totalDays

) {
}