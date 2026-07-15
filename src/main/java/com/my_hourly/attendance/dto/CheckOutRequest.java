package com.my_hourly.attendance.dto;


public class CheckOutRequest {

    private Long employeeId;
    private String remarks;

    public CheckOutRequest() {
    }

    public CheckOutRequest(Long employeeId, String remarks) {
        this.employeeId = employeeId;
        this.remarks = remarks;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}