package com.my_hourly.project.service;

import com.my_hourly.project.dto.*;
import com.my_hourly.project.entity.MemberStatus;
import com.my_hourly.project.entity.ProjectStatus;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ProjectService {
    // Project
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse updateProject(Long projectId, ProjectRequest request);

    ProjectResponse updateProjectStatus(Long projectId, ProjectStatus status);

    String deleteProject(Long projectId);
    ProjectResponse getProjectById(Long projectId);
    List<ProjectResponse> getAllProjects();
    List<ProjectResponse> getMyProjects();

    // Project Members
    ProjectMemberResponse addProjectMember(ProjectMemberRequest request);
    void removeProjectMember(Long projectId, Long employeeId);


    ProjectMemberResponse updateMemberStatus(
            Long memberId,
            MemberStatus status);

    List<ProjectMemberResponse> getProjectMembers(Long projectId);

    // Employee Allocation
    EmployeeAllocationResponse allocateEmployee(EmployeeAllocationRequest request);
    EmployeeAllocationResponse updateAllocation(Long allocationId, EmployeeAllocationRequest request);
    void releaseAllocation(Long allocationId);
    List<EmployeeAllocationResponse> getProjectAllocations(Long projectId);
    List<EmployeeAllocationResponse> getEmployeeAllocations();

}
