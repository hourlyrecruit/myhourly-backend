package com.my_hourly.project.repository;
import java.util.List;
import java.util.Optional;

import com.my_hourly.project.entity.ProjectMember;
import com.my_hourly.project.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.project.entity.Client;
import com.my_hourly.project.entity.Project;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);
    boolean existsByProjectCode(String projectCode);
    List<Project> findByClient(Client client);
    List<Project> findByManager(Employee manager);
    List<ProjectMember> findByEmployee(Employee employee);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findByProjectNameContainingIgnoreCase(String projectName);

}