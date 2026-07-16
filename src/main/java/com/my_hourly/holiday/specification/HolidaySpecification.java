package com.my_hourly.holiday.specification;

import com.my_hourly.holiday.entity.Holiday;
import com.my_hourly.holiday.entity.HolidayType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class HolidaySpecification {

    private HolidaySpecification() {
    }

    public static Specification<Holiday> hasType(
            HolidayType type
    ) {

        if (type == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("holidayType"), type);
    }

    public static Specification<Holiday> isActive(
            Boolean active
    ) {

        if (active == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(root.get("active"), active);
    }

    public static Specification<Holiday> fromDate(
            LocalDate fromDate
    ) {

        if (fromDate == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("holidayDate"),
                        fromDate
                );
    }

    public static Specification<Holiday> toDate(
            LocalDate toDate
    ) {

        if (toDate == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("holidayDate"),
                        toDate
                );
    }

    public static Specification<Holiday> holidayNameContains(
            String holidayName
    ) {

        if (holidayName == null || holidayName.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("holidayName")),
                        "%" + holidayName.toLowerCase() + "%"
                );
    }

}