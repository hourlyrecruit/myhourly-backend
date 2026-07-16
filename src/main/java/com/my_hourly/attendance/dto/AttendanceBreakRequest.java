package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.BreakType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceBreakRequest {



    private BreakType breakType;

    private String remarks;

}