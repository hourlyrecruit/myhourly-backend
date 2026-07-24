package com.my_hourly.attendance.specification;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class AttendanceSpecification {

    private AttendanceSpecification() {
    }

    public static Specification<Attendance> hasEmployee(
            Employee employee
    ) {

        return (root, query, cb) ->
                cb.equal(root.get("employee"), employee);
    }

    public static Specification<Attendance> hasStatus(
            AttendanceStatus status
    ) {


        if (status == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("attendanceStatus"), status);
    }

    public static Specification<Attendance> fromDate(
            LocalDate fromDate
    ) {

        if (fromDate == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("attendanceDate"),
                        fromDate
                );
    }

    public static Specification<Attendance> toDate(
            LocalDate toDate
    ) {

        if (toDate == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("attendanceDate"),
                        toDate
                );
    }

}