package com.my_hourly.employee.specification;

import com.my_hourly.employee.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> search(String search) {

        return (root, query, cb) -> {

            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.like(
                            cb.lower(root.get("firstName")),
                            "%" + search.toLowerCase() + "%"
                    ),
                    cb.like(
                            cb.lower(root.get("lastName")),
                            "%" + search.toLowerCase() + "%"
                    ),
                    cb.like(
                            cb.lower(root.get("employeeCode")),
                            "%" + search.toLowerCase() + "%"
                    )
            );
        };
    }


    public static Specification<Employee> reportingManager(
            Employee manager) {

        return (root, query, cb) ->
                cb.equal(
                        root.get("reportingManager"),
                        manager
                );
    }
}
