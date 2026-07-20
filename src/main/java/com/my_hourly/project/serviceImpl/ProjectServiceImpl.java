package com.my_hourly.project.serviceImpl;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.repository.UserRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.project.dto.*;
import com.my_hourly.project.entity.*;
import com.my_hourly.project.repository.ClientRepository;
import com.my_hourly.project.repository.EmployeeAllocationRepository;
import com.my_hourly.project.repository.ProjectMemberRepository;
import com.my_hourly.project.repository.ProjectRepository;
import com.my_hourly.project.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeAllocationRepository employeeAllocationRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;


    private Employee getLoggedInEmployee() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        if(projectRepository.existsByProjectCode(request.getProjectCode())){
            throw new RuntimeException("Project code already exists");
        }
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(()->new EntityNotFoundException("Client not found."));
        Employee manager = getLoggedInEmployee();

        Project project = new Project();
        project.setProjectCode(request.getProjectCode());
        project.setProjectName(request.getProjectName());
        project.setDescription(request.getDescription());
        project.setClient(client);
        project.setManager(manager);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus());
        project.setCreatedAt(LocalDateTime.now());
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        return mapProjectResponse(savedProject);

    }

    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new EntityNotFoundException("Project not found."));
        if(request.getProjectCode() != null &&
        !request.getProjectCode().equals(project.getProjectCode()) &&
        projectRepository.existsByProjectCode(request.getProjectCode())){
            throw new RuntimeException("Project code already exists.");
        }
        if (request.getClientId() != null) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found."));
            project.setClient(client);
        }
        Employee manager = getLoggedInEmployee();

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You are not authorized to update this project.");
        }
        if (request.getProjectCode() != null) {
            project.setProjectCode(request.getProjectCode());
        }
        if (request.getProjectName() != null) {
            project.setProjectName(request.getProjectName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);
        return mapProjectResponse(updatedProject);

    }



    @Override
    public String deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new EntityNotFoundException("Project not found."));
        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You are not authorized.");
        }
        projectRepository.delete(project);
        return "Deleted Successfully.";
    }

    @Override
    public ProjectResponse getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new EntityNotFoundException("Project not found"));

        return mapProjectResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        Employee manager = getLoggedInEmployee();

        return projectRepository.findByManager(manager)
                .stream()
                .map(this::mapProjectResponse)
                .toList();
    }
    @Override
    public List<ProjectResponse> getMyProjects() {
        Employee employee = getLoggedInEmployee();
        return projectMemberRepository.findByEmployee(employee)
                .stream()
                .map(pm -> mapProjectResponse(pm.getProject()))
                .toList();
    }
    private ProjectResponse mapProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setProjectCode(project.getProjectCode());
        response.setProjectName(project.getProjectName());
        response.setDescription(project.getDescription());
        if (project.getClient() != null) {
            response.setClientId(project.getClient().getId());
            response.setClientName(project.getClient().getCompanyName());
        }
        if (project.getManager() != null) {
            response.setManagerId(project.getManager().getId());
            String managerName = project.getManager().getFirstName();
            if (project.getManager().getLastName() != null) {
                managerName += " " + project.getManager().getLastName();
            }
            response.setManagerName(managerName);
        }
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setStatus(project.getStatus());

        return response;
    }

    @Override
    public ProjectMemberResponse addProjectMember(ProjectMemberRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(()->new EntityNotFoundException("Project not found."));
        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Only project manager can add members.");
        }
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(()->new EntityNotFoundException("Employee not found."));
        if(projectMemberRepository.existsByProjectAndEmployee(project,employee)){
            throw new RuntimeException("Employee is already member of this project.");
        }
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setEmployee(employee);
        member.setProjectRole(request.getProjectRole());
        member.setStartDate(request.getStartDate());
        member.setEndDate(request.getEndDate());
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());

        ProjectMember savedMember = projectMemberRepository.save(member);
        return mapProjectMemberResponse(savedMember);

    }

    @Override
    public void removeProjectMember(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()->new EntityNotFoundException("Project not found"));
        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Only project manager can remove members.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(()->new EntityNotFoundException("Employee not found."));
        ProjectMember member = projectMemberRepository.findByProjectAndEmployee(project, employee)
                .orElseThrow(()->new RuntimeException("Project member not found."));

        projectMemberRepository.delete(member);
    }

    @Override
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found."));

        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Access denied. You are not the manager of this project.");
        }
        return projectMemberRepository.findByProject(project)
                .stream()
                .map(this::mapProjectMemberResponse)
                .toList();
    }
    private ProjectMemberResponse mapProjectMemberResponse(ProjectMember member) {

        ProjectMemberResponse response = new ProjectMemberResponse();
        response.setId(member.getId());
        response.setProjectId(member.getProject().getId());
        response.setProjectName(member.getProject().getProjectName());
        response.setEmployeeId(member.getEmployee().getId());
        response.setEmployeeCode(member.getEmployee().getEmployeeCode());
        String employeeName = member.getEmployee().getFirstName();
        if (member.getEmployee().getLastName() != null) {
            employeeName += " " + member.getEmployee().getLastName();
        }
        response.setEmployeeName(employeeName);
        response.setProjectRole(member.getProjectRole());
        response.setStartDate(member.getStartDate());
        response.setEndDate(member.getEndDate());

        return response;
    }

    @Override
    public EmployeeAllocationResponse allocateEmployee(EmployeeAllocationRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found."));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found."));
        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Only project manager can allocate employees.");
        }
        if (!projectMemberRepository.existsByProjectAndEmployee(project, employee)) {
            throw new RuntimeException("Employee is not a member of this project.");
        }

        EmployeeAllocation allocation = new EmployeeAllocation();

        allocation.setEmployee(employee);
        allocation.setProject(project);
        allocation.setAllocationType(request.getAllocationType());
        allocation.setStartDate(request.getStartDate());
        allocation.setEndDate(request.getEndDate());
        allocation.setStatus(AllocationStatus.ACTIVE);
        allocation.setRemarks(request.getRemarks());
        allocation.setCreatedAt(LocalDateTime.now());
        allocation.setUpdatedAt(LocalDateTime.now());

        EmployeeAllocation savedAllocation = employeeAllocationRepository.save(allocation);

        return mapAllocationResponse(savedAllocation);
    }

    @Override
    public EmployeeAllocationResponse updateAllocation(Long allocationId, EmployeeAllocationRequest request) {
        EmployeeAllocation allocation = employeeAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new EntityNotFoundException("Allocation not found."));
        Employee manager = getLoggedInEmployee();
        if (!allocation.getProject().getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Access denied.");
        }
        if (request.getAllocationType() != null) {
            allocation.setAllocationType(request.getAllocationType());
        }
        if (request.getStartDate() != null) {
            allocation.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            allocation.setEndDate(request.getEndDate());
        }
        if (request.getRemarks() != null) {
            allocation.setRemarks(request.getRemarks());
        }
        allocation.setUpdatedAt(LocalDateTime.now());

        EmployeeAllocation updatedAllocation = employeeAllocationRepository.save(allocation);

        return mapAllocationResponse(updatedAllocation);
    }

    @Override
    public void releaseAllocation(Long allocationId) {
        EmployeeAllocation allocation = employeeAllocationRepository.findById(allocationId)
                .orElseThrow(() -> new EntityNotFoundException("Allocation not found."));
        Employee manager = getLoggedInEmployee();
        if (!allocation.getProject().getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Access denied.");
        }
        allocation.setStatus(AllocationStatus.RELEASED);
        allocation.setEndDate(LocalDate.now());
        allocation.setUpdatedAt(LocalDateTime.now());

        employeeAllocationRepository.save(allocation);
    }

    @Override
    public List<EmployeeAllocationResponse> getProjectAllocations(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found."));
        Employee manager = getLoggedInEmployee();
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Access denied. You are not the manager of this project.");
        }
        return employeeAllocationRepository.findByProject(project)
                .stream()
                .map(this::mapAllocationResponse)
                .toList();
    }

    @Override
    public List<EmployeeAllocationResponse> getEmployeeAllocations() {
        Employee employee = getLoggedInEmployee();

        return employeeAllocationRepository.findByEmployee(employee)
                .stream()
                .map(this::mapAllocationResponse)
                .toList();
    }
    private EmployeeAllocationResponse mapAllocationResponse(EmployeeAllocation allocation) {
        EmployeeAllocationResponse response = new EmployeeAllocationResponse();
        response.setId(allocation.getId());
        response.setEmployeeId(allocation.getEmployee().getId());
        String employeeName = allocation.getEmployee().getFirstName();
        if (allocation.getEmployee().getLastName() != null) {
            employeeName += " " + allocation.getEmployee().getLastName();
        }
        response.setEmployeeName(employeeName);
        response.setProjectId(allocation.getProject().getId());
        response.setProjectName(allocation.getProject().getProjectName());
        response.setAllocationType(allocation.getAllocationType());
        response.setStartDate(allocation.getStartDate());
        response.setEndDate(allocation.getEndDate());
        response.setStatus(allocation.getStatus());
        response.setRemarks(allocation.getRemarks());

        return response;
    }
}
