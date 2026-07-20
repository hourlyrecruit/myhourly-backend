package com.my_hourly.project.dto;

import com.my_hourly.project.entity.AllocationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAllocationRequest {

    private Long employeeId;
    private Long projectId;
    private AllocationType allocationType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String remarks;

}