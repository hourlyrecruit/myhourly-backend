package com.my_hourly.project.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.project.entity.Project;
import com.my_hourly.project.entity.ProjectMember;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProject(Project project);
    List<ProjectMember> findByEmployee(Employee employee);
    Optional<ProjectMember> findByProjectAndEmployee(Project project, Employee employee);
    boolean existsByProjectAndEmployee(Project project, Employee employee);
    void deleteByProjectAndEmployee(Project project, Employee employee);

}