package com.my_hourly.notification.api.response;

import java.time.LocalDate;

/**
 * An active employee whose next birthday falls within the requested window.
 *
 * @param employeeId            employee id
 * @param firstName             employee first name
 * @param lastName              employee last name
 * @param employeeName          full display name
 * @param dateOfBirth           stored date of birth
 * @param upcomingBirthdayDate  the next actual birthday date (handles year wrap-around)
 * @param daysUntil             days from today until {@code upcomingBirthdayDate}
 */
public record UpcomingBirthdayResponse(
        Long employeeId,
        String firstName,
        String lastName,
        String employeeName,
        LocalDate dateOfBirth,
        LocalDate upcomingBirthdayDate,
        long daysUntil
) {
}
