package com.my_hourly.form_16.util;

import java.time.LocalDate;

public final class FinancialYearUtil {

    private FinancialYearUtil() {
    }


    // =========================================================
    // CURRENT ASSESSMENT YEAR
    // =========================================================
    // Example:
    // Date: 04-09-2026
    // FY:   2026-27
    // AY:   2027-28
    // =========================================================

    public static String getCurrentAssessmentYear() {

        LocalDate today = LocalDate.now();

        int financialYearStart;

        if (today.getMonthValue() >= 4) {
            financialYearStart = today.getYear();
        } else {
            financialYearStart = today.getYear() - 1;
        }

        int assessmentYearStart = financialYearStart + 1;

        return assessmentYearStart
                + "-"
                + String.format(
                "%02d",
                (assessmentYearStart + 1) % 100
        );
    }


    // =========================================================
    // FINANCIAL YEAR START
    // =========================================================

    public static LocalDate getFinancialYearStart(
            String assessmentYear
    ) {

        int assessmentStartYear =
                Integer.parseInt(
                        assessmentYear.substring(0, 4)
                );

        int financialStartYear =
                assessmentStartYear - 1;

        return LocalDate.of(
                financialStartYear,
                4,
                1
        );
    }


    // =========================================================
    // FINANCIAL YEAR END
    // =========================================================

    public static LocalDate getFinancialYearEnd(
            String assessmentYear
    ) {

        int assessmentStartYear =
                Integer.parseInt(
                        assessmentYear.substring(0, 4)
                );

        int financialStartYear =
                assessmentStartYear - 1;

        return LocalDate.of(
                financialStartYear + 1,
                3,
                31
        );
    }


    // =========================================================
    // EMPLOYMENT FROM
    // =========================================================

    public static LocalDate calculateEmploymentFrom(
            LocalDate dateOfJoining,
            String assessmentYear
    ) {

        LocalDate financialYearStart =
                getFinancialYearStart(
                        assessmentYear
                );

        if (dateOfJoining == null) {
            return financialYearStart;
        }

        if (dateOfJoining.isAfter(
                financialYearStart
        )) {

            return dateOfJoining;
        }

        return financialYearStart;
    }


    // =========================================================
    // EMPLOYMENT TO
    // =========================================================

    public static LocalDate calculateEmploymentTo(
            String assessmentYear
    ) {

        return getFinancialYearEnd(
                assessmentYear
        );
    }
}