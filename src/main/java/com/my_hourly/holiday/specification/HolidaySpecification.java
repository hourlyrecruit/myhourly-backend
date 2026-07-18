package com.my_hourly.holiday.specification;

import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.entity.HolidayType;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor
public final class HolidaySpecification {


    public static Specification<Holiday> isActive(Boolean active) {

        return (root, query, cb) -> {

            if (active == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<Holiday> holidayNameContains(String holidayName) {

        return (root, query, cb) -> {

            if (holidayName == null || holidayName.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("holidayName")),
                    "%" + holidayName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Holiday> hasType(HolidayType type) {

        return (root, query, cb) -> {

            if (type == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("holidayType"), type);
        };
    }

    public static Specification<Holiday> fromDate(LocalDate fromDate) {

        return (root, query, cb) -> {

            if (fromDate == null) {
                return cb.conjunction();
            }

            return cb.greaterThanOrEqualTo(
                    root.get("holidayDate"),
                    fromDate
            );
        };
    }

    public static Specification<Holiday> toDate(LocalDate toDate) {

        return (root, query, cb) -> {

            if (toDate == null) {
                return cb.conjunction();
            }

            return cb.lessThanOrEqualTo(
                    root.get("holidayDate"),
                    toDate
            );
        };
    }

}