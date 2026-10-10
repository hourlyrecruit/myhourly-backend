package com.my_hourly.leave.specification;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.entity.LeaveRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Reusable JPA specifications for the leave-request list endpoints.
 *
 * <p>Month filtering is deliberately based on <b>overlap</b>: a request is kept
 * when its {@code [startDate, endDate]} range intersects the selected period,
 * so a request spanning a month boundary appears under both months. This is the
 * same rule the leave calendar uses.</p>
 */
public final class LeaveSpecification {

    private LeaveSpecification() {
    }

    /**
     * An inclusive {@code [from, to]} date range.
     */
    public record MonthRange(LocalDate from, LocalDate to) {
    }

    /**
     * Restricts the query to one employee's own requests.
     */
    public static Specification<LeaveRequest> hasEmployee(Employee employee) {

        return (root, query, cb) ->
                cb.equal(root.get("employee").get("id"), employee.getId());
    }

    /**
     * Restricts the query to the direct reports of one manager.
     */
    public static Specification<LeaveRequest> hasReportingManager(Employee manager) {

        return (root, query, cb) ->
                cb.equal(root.get("employee").get("reportingManager").get("id"), manager.getId());
    }

    /**
     * Requests whose date range intersects {@code [from, to]} (both bounds
     * inclusive): {@code startDate <= to AND endDate >= from}.
     */
    public static Specification<LeaveRequest> overlaps(LocalDate from, LocalDate to) {

        return (root, query, cb) -> cb.and(
                cb.lessThanOrEqualTo(root.get("startDate"), to),
                cb.greaterThanOrEqualTo(root.get("endDate"), from)
        );
    }

    /**
     * Resolves the optional {@code month} / {@code year} query parameters into
     * an inclusive date range.
     *
     * <ul>
     *   <li>both {@code null} — no filter, returns {@code null};</li>
     *   <li>{@code month} set, {@code year} omitted — that month of the current year;</li>
     *   <li>{@code month} omitted, {@code year} set — the whole year;</li>
     *   <li>both set — that calendar month.</li>
     * </ul>
     *
     * @throws BadRequestException when {@code month} is outside 1..12 or
     *         {@code year} is outside the supported range.
     */
    public static MonthRange resolveRange(Integer month, Integer year) {

        if (month == null && year == null) {
            return null;
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new BadRequestException(
                    "Month must be between 1 and 12.", ErrorCode.VALIDATION_FAILED);
        }

        if (year != null && (year < 2000 || year > 2100)) {
            throw new BadRequestException(
                    "Year must be between 2000 and 2100.", ErrorCode.VALIDATION_FAILED);
        }

        int resolvedYear = year != null ? year : LocalDate.now().getYear();

        if (month == null) {
            return new MonthRange(
                    LocalDate.of(resolvedYear, 1, 1),
                    LocalDate.of(resolvedYear, 12, 31));
        }

        YearMonth yearMonth = YearMonth.of(resolvedYear, month);
        return new MonthRange(yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }
}
