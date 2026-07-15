package com.my_hourly.leave.entity;

import com.my_hourly.employee.entity.Employee;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_balances")
public class LeaveBalance {

    // =====================================================
    // Primary Key
    // =====================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // Employee Details
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "employee_first_name", nullable = false)
    private String employeeFirstName;

    @Column(name = "employee_last_name", nullable = false)
    private String employeeLastName;

    // =====================================================
    // Leave Balance
    // =====================================================

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "monthly_leave", nullable = false)
    private Integer monthlyLeave;

    @Column(name = "used_leave", nullable = false)
    private Integer usedLeave;

    @Column(name = "available_leave", nullable = false)
    private Integer availableLeave;

    @Column(name = "expired_leave", nullable = false)
    private Integer expiredLeave;

    // =====================================================
    // Constructors
    // =====================================================

    public LeaveBalance() {
    }

    public LeaveBalance(Long id,
                        Employee employee,
                        String employeeCode,
                        String employeeFirstName,
                        String employeeLastName,
                        Integer year,
                        Integer month,
                        Integer monthlyLeave,
                        Integer usedLeave,
                        Integer availableLeave,
                        Integer expiredLeave) {

        this.id = id;
        this.employee = employee;
        this.employeeCode = employeeCode;
        this.employeeFirstName = employeeFirstName;
        this.employeeLastName = employeeLastName;
        this.year = year;
        this.month = month;
        this.monthlyLeave = monthlyLeave;
        this.usedLeave = usedLeave;
        this.availableLeave = availableLeave;
        this.expiredLeave = expiredLeave;
    }

    // =====================================================
    // Getters & Setters
    // =====================================================

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

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getMonthlyLeave() {
        return monthlyLeave;
    }

    public void setMonthlyLeave(Integer monthlyLeave) {
        this.monthlyLeave = monthlyLeave;
    }

    public Integer getUsedLeave() {
        return usedLeave;
    }

    public void setUsedLeave(Integer usedLeave) {
        this.usedLeave = usedLeave;
    }

    public Integer getAvailableLeave() {
        return availableLeave;
    }

    public void setAvailableLeave(Integer availableLeave) {
        this.availableLeave = availableLeave;
    }

    public Integer getExpiredLeave() {
        return expiredLeave;
    }

    public void setExpiredLeave(Integer expiredLeave) {
        this.expiredLeave = expiredLeave;
    }
}