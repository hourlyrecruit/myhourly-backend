package com.my_hourly.attendance.util;

public final class TimeUtil {

    private TimeUtil() {
    }

    public static String formatMinutes(Integer minutes) {

        if (minutes <= 0) {
            return "0h 0m";
        }

        int hours = minutes / 60;
        int mins = minutes % 60;

        return hours + "h " + mins + "m";
    }
}