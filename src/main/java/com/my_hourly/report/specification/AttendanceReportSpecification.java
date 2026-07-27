package com.my_hourly.report.specification;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.report.dto.request.AttendanceReportRequest;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for filtering Attendance reports
 */
public class AttendanceReportSpecification {

    private AttendanceReportSpecification() {
    }

    /**
     * Build dynamic filter specification based on request parameters
     * Includes JOIN FETCH to avoid LazyInitializationException
     */
    public static Specification<Attendance> filter(AttendanceReportRequest request) {

        return (root, query, cb) -> {

            // Add FETCH joins to eagerly load associations (only for non-count queries)
            if (query != null && Long.class != query.getResultType()) {
                var employeeFetch = root.fetch("employee", JoinType.LEFT);
                employeeFetch.fetch("department", JoinType.LEFT);
            }

            List<Predicate> predicates = new ArrayList<>();

            // Filter by Employee ID
            if (request.getEmployeeId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("employee").get("id"),
                                request.getEmployeeId()
                        )
                );
            }

            // Filter by Employee Name (searches both first and last name)
            if (request.getEmployeeName() != null &&
                    !request.getEmployeeName().isBlank()) {

                String searchPattern = "%" + request.getEmployeeName().toLowerCase() + "%";

                Predicate firstNamePredicate = cb.like(
                        cb.lower(root.get("employee").get("firstName")),
                        searchPattern
                );

                Predicate lastNamePredicate = cb.like(
                        cb.lower(root.get("employee").get("lastName")),
                        searchPattern
                );

                predicates.add(cb.or(firstNamePredicate, lastNamePredicate));
            }

            // Filter by Department
            if (request.getDepartmentId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("employee")
                                        .get("department")
                                        .get("id"),
                                request.getDepartmentId()
                        )
                );
            }

            // Filter by Attendance Status
            if (request.getAttendanceStatus() != null) {
                predicates.add(
                        cb.equal(
                                root.get("attendanceStatus"),
                                request.getAttendanceStatus()
                        )
                );
            }

            // Filter by Date Range - Start Date
            if (request.getStartDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("attendanceDate"),
                                request.getStartDate()
                        )
                );
            }

            // Filter by Date Range - End Date
            if (request.getEndDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("attendanceDate"),
                                request.getEndDate()
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}