package com.my_hourly.project.dto;

import com.my_hourly.project.entity.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse {

    private Long id;
    private Long projectId;
    private String projectName;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private ProjectRole projectRole;
    private LocalDate startDate;
    private LocalDate endDate;

}