package com.my_hourly.project.dto;

import com.my_hourly.project.entity.AllocationStatus;
import com.my_hourly.project.entity.AllocationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAllocationResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long projectId;
    private String projectName;
    private AllocationType allocationType;
    private LocalDate startDate;
    private LocalDate endDate;
    private AllocationStatus status;
    private String remarks;

}