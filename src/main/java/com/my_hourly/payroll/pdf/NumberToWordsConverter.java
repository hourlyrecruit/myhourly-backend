package com.my_hourly.payroll.pdf;

import java.math.BigDecimal;

public final class NumberToWordsConverter {

    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private NumberToWordsConverter() {
    }

    public static String convert(BigDecimal value) {
        return convert(value == null ? 0L : value.longValue());
    }

    public static String convert(long number) {
        if (number == 0) {
            return "Zero";
        }
        if (number < 0) {
            return "Minus " + convert(-number);
        }

        StringBuilder result = new StringBuilder();

        if (number / 10000000 > 0) {
            result.append(convert(number / 10000000)).append(" Crore ");
            number %= 10000000;
        }

        if (number / 100000 > 0) {
            result.append(convert(number / 100000)).append(" Lakh ");
            number %= 100000;
        }

        if (number / 1000 > 0) {
            result.append(convert(number / 1000)).append(" Thousand ");
            number %= 1000;
        }

        if (number / 100 > 0) {
            result.append(convert(number / 100)).append(" Hundred ");
            number %= 100;
        }

        if (number > 0) {
            if (!result.isEmpty()) {
                result.append("and ");
            }
            result.append(twoDigitWords((int) number));
        }

        return result.toString().trim().replaceAll("\\s+", " ");
    }

    public static String convertAsRupees(BigDecimal value) {
        return "Rupees " + convert(value) + " Only /-";
    }

    private static String twoDigitWords(int number) {
        if (number < 20) {
            return ONES[number];
        }
        return TENS[number / 10]
                + (number % 10 == 0 ? "" : " " + ONES[number % 10]);
    }
}
