package com.my_hourly.attendance.service;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.employee.entity.Employee;

public interface AttendanceValidationService {

    void validateCheckIn(Employee employee);

    void validateCheckOut(Attendance attendance);

    void validateBreakStart(Attendance attendance);

    void validateBreakEnd(Attendance attendance);


}

