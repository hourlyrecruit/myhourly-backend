package com.my_hourly.attendance.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static String formatTime(LocalDateTime dateTime) {

        if (dateTime == null) {
            return "--";
        }

        return dateTime.toLocalTime().format(
                DateTimeFormatter.ofPattern("hh:mm a")
        );
    }

}
