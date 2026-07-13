package com.my_hourly.common.enums;

public enum ErrorCode {

    // Authentication
    INVALID_CREDENTIALS,
    ACCESS_DENIED,
    TOKEN_EXPIRED,
    TOKEN_INVALID,

    // Employee
    EMPLOYEE_NOT_FOUND,
    EMPLOYEE_ALREADY_EXISTS,

    // Organization
    COMPANY_NOT_FOUND,
    DEPARTMENT_NOT_FOUND,
    DESIGNATION_NOT_FOUND,

    // Attendance
    ATTENDANCE_ALREADY_EXISTS,
    INVALID_ATTENDANCE,

    // Leave
    LEAVE_NOT_FOUND,
    LEAVE_BALANCE_EXCEEDED,

    // Payroll
    PAYROLL_ALREADY_PROCESSED,

    // Validation
    VALIDATION_FAILED,

    // Generic
    RESOURCE_NOT_FOUND,
    INTERNAL_SERVER_ERROR

}
