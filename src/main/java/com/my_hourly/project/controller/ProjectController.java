package com.my_hourly.project.controller;

import com.my_hourly.project.dto.EmployeeAllocationRequest;
import com.my_hourly.project.dto.EmployeeAllocationResponse;
import com.my_hourly.project.dto.ProjectMemberRequest;
import com.my_hourly.project.dto.ProjectMemberResponse;
import com.my_hourly.project.dto.ProjectRequest;
import com.my_hourly.project.dto.ProjectResponse;
import com.my_hourly.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest request) {
        return new ResponseEntity<>(projectService.createProject(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/manager/my-projects")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }


    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/my-allocations")
    public ResponseEntity<List<EmployeeAllocationResponse>> getMyAllocations() {
        return ResponseEntity.ok(projectService.getEmployeeAllocations());
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/members")
    public ResponseEntity<ProjectMemberResponse> addProjectMember(@RequestBody ProjectMemberRequest request) {
        return new ResponseEntity<>(projectService.addProjectMember(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @DeleteMapping("/{projectId}/members/{employeeId}")
    public ResponseEntity<String> removeProjectMember(@PathVariable Long projectId, @PathVariable Long employeeId) {
        projectService.removeProjectMember(projectId, employeeId);
        return ResponseEntity.ok("Project member removed successfully.");
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectMembers(projectId));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/allocations")
    public ResponseEntity<EmployeeAllocationResponse> allocateEmployee(@RequestBody EmployeeAllocationRequest request) {
        return new ResponseEntity<>(projectService.allocateEmployee(request), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/allocations/{allocationId}")
    public ResponseEntity<EmployeeAllocationResponse> updateAllocation(@PathVariable Long allocationId, @RequestBody EmployeeAllocationRequest request) {
        return ResponseEntity.ok(projectService.updateAllocation(allocationId, request));
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PutMapping("/allocations/{allocationId}/release")
    public ResponseEntity<String> releaseAllocation(@PathVariable Long allocationId) {
        projectService.releaseAllocation(allocationId);
        return ResponseEntity.ok("Allocation released successfully.");
    }

    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping("/{projectId}/allocations")
    public ResponseEntity<List<EmployeeAllocationResponse>> getProjectAllocations(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectAllocations(projectId));
    }

}