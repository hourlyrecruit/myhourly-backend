package com.my_hourly.attendance.specification;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.employee.entity.Employee;
import jakarta.persistence.criteria.Join;
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

    public static Specification<Attendance> search(String search) {

        return (root, query, cb) -> {

            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            Join<Attendance, Employee> employee = root.join("employee");

            return cb.or(
                    cb.like(
                            cb.lower(employee.get("firstName")),
                            "%" + search.toLowerCase() + "%"
                    ),
                    cb.like(
                            cb.lower(employee.get("lastName")),
                            "%" + search.toLowerCase() + "%"
                    ),
                    cb.like(
                            cb.lower(employee.get("employeeCode")),
                            "%" + search.toLowerCase() + "%"
                    )
            );
        };
    }

}