package com.my_hourly.leave.entity;

import java.time.LocalDate;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.enums.LeaveStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_leaves")
public class Leave {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    
    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LeaveStatus status;

    @Column(name = "manager_remarks", length = 500)
    private String managerRemarks;

    

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Column(name = "approved_date")
    private LocalDate approvedDate;

    

    public Leave() {
    }

    
  

    public Leave(Long id,
                 Employee employee,
                 String employeeCode,
                 String employeeName,
                 LocalDate fromDate,
                 LocalDate toDate,
                 Integer totalDays,
                 LeaveStatus status,
                 String managerRemarks,
                 LocalDate appliedDate,
                 LocalDate approvedDate) {

        this.id = id;
        this.employee = employee;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.totalDays = totalDays;
        this.status = status;
        this.managerRemarks = managerRemarks;
        this.appliedDate = appliedDate;
        this.approvedDate = approvedDate;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getManagerRemarks() {
        return managerRemarks;
    }

    public void setManagerRemarks(String managerRemarks) {
        this.managerRemarks = managerRemarks;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }
}