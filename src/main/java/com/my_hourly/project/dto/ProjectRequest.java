package com.my_hourly.project.dto;

import com.my_hourly.project.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    private String projectCode;
    private String projectName;
    private String description;
    private Long clientId;
    private Long managerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;

}