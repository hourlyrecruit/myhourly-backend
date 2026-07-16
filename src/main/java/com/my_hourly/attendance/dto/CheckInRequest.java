package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.AttendanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {



    private AttendanceType attendanceType;
    private String employeeCode;

    private String remarks;

}