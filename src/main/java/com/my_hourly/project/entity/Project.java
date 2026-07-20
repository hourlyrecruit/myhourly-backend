package com.my_hourly.project.entity;

import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String projectCode;
    @Column(nullable = false)
    private String projectName;
    @Column(length = 1000)
    private String description;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;
    private LocalDate startDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;
    @OneToMany(mappedBy = "project")
    private List<ProjectMember> members;
    @OneToMany(mappedBy = "project")
    private List<EmployeeAllocation> allocations;
    private LocalDateTime createdAt;
    private  LocalDateTime updatedAt;

}
