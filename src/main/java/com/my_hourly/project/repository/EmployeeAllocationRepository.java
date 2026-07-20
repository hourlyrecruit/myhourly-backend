package com.my_hourly.project.repository;
import java.util.List;

import com.my_hourly.project.entity.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.project.entity.EmployeeAllocation;
import com.my_hourly.project.entity.Project;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAllocationRepository extends JpaRepository<EmployeeAllocation, Long> {

    List<EmployeeAllocation> findByEmployee(Employee employee);
    List<EmployeeAllocation> findByProject(Project project);
    List<EmployeeAllocation> findByStatus(AllocationStatus status);
    List<EmployeeAllocation> findByEmployeeAndStatus(Employee employee, AllocationStatus status);
    List<EmployeeAllocation> findByProjectAndStatus(Project project, AllocationStatus status);

}