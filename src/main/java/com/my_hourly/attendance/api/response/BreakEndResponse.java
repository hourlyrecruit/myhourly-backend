package com.my_hourly.attendance.api.response;

import com.my_hourly.attendance.entity.BreakType;
import com.my_hourly.attendance.entity.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreakEndResponse {
    private Long id;

    private LocalDate date;

    private String breakEndTime;

    private Integer breakMinutes;

    private EmployeeStatus employeeStatus;
}
