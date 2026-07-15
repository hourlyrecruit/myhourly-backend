package com.my_hourly.attendance.dto;

import com.my_hourly.attendance.enums.BreakType;

public class AttendanceBreakRequest {

    private Long employeeId;
    private Long attendanceId;
    private BreakType breakType;
    private String remarks;

    public AttendanceBreakRequest() {
    }

    public AttendanceBreakRequest(Long employeeId, Long attendanceId,
                                  BreakType breakType, String remarks) {
        this.employeeId = employeeId;
        this.attendanceId = attendanceId;
        this.breakType = breakType;
        this.remarks = remarks;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public BreakType getBreakType() {
        return breakType;
    }

    public void setBreakType(BreakType breakType) {
        this.breakType = breakType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}