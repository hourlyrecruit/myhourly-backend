package com.my_hourly.attendance.util;

public final class TimeUtil {

    private TimeUtil() {
    }

    public static String formatMinutes(Integer minutes) {

        if (minutes == null || minutes <= 0) {
            return "0m";
        }

        int hours = minutes / 60;
        int mins = minutes % 60;

        if (hours == 0) {
            return mins + "m";
        }

        if (mins == 0) {
            return hours + "h";
        }

        return hours + "h " + mins + "m";
    }
}