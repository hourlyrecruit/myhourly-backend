package com.my_hourly.form_16.calculation;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class Form16TaxCalculationService {

    private static final BigDecimal ZERO =
            BigDecimal.ZERO;

    private static final BigDecimal FOUR =
            new BigDecimal("4");

    private static final BigDecimal HUNDRED =
            new BigDecimal("100");



    public BigDecimal round(
            BigDecimal value
    ) {

        if (value == null) {
            return ZERO;
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }



    // year tax cal

    /**
     * New regime slabs for AY 2026-27:
     *
     * Up to 4L       = 0%
     * 4L - 8L        = 5%
     * 8L - 12L       = 10%
     * 12L - 16L      = 15%
     * 16L - 20L      = 20%
     * 20L - 24L      = 25%
     * Above 24L      = 30%
     */
    public BigDecimal calculateNewRegimeTax(
            BigDecimal taxableIncome
    ) {

        taxableIncome =
                nonNegative(taxableIncome);

        BigDecimal tax = ZERO;



        if (taxableIncome.compareTo(
                new BigDecimal("400000")
        ) <= 0) {

            return ZERO;
        }



        BigDecimal amount =
                taxableIncome
                        .min(new BigDecimal("800000"))
                        .subtract(new BigDecimal("400000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 5)
            );
        }



        amount =
                taxableIncome
                        .min(new BigDecimal("1200000"))
                        .subtract(new BigDecimal("800000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 10)
            );
        }



        amount =
                taxableIncome
                        .min(new BigDecimal("1600000"))
                        .subtract(new BigDecimal("1200000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 15)
            );
        }



        amount =
                taxableIncome
                        .min(new BigDecimal("2000000"))
                        .subtract(new BigDecimal("1600000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 20)
            );
        }



        amount =
                taxableIncome
                        .min(new BigDecimal("2400000"))
                        .subtract(new BigDecimal("2000000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 25)
            );
        }



        amount =
                taxableIncome
                        .subtract(new BigDecimal("2400000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 30)
            );
        }


        return round(tax);
    }



    public BigDecimal calculateOldRegimeTax(
            BigDecimal taxableIncome
    ) {

        taxableIncome =
                nonNegative(taxableIncome);

        BigDecimal tax = ZERO;



        if (taxableIncome.compareTo(
                new BigDecimal("250000")
        ) <= 0) {

            return ZERO;
        }



        BigDecimal amount =
                taxableIncome
                        .min(new BigDecimal("500000"))
                        .subtract(new BigDecimal("250000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 5)
            );
        }



        amount =
                taxableIncome
                        .min(new BigDecimal("1000000"))
                        .subtract(new BigDecimal("500000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 20)
            );
        }


        // Above 10L @ 30%
        amount =
                taxableIncome
                        .subtract(new BigDecimal("1000000"));

        if (amount.compareTo(ZERO) > 0) {

            tax = tax.add(
                    percentage(amount, 30)
            );
        }


        return round(tax);
    }




    public BigDecimal calculateNewRegimeRebate(
            BigDecimal taxableIncome,
            BigDecimal tax
    ) {

        if (
                taxableIncome.compareTo(
                        new BigDecimal("1200000")
                ) <= 0
        ) {

            BigDecimal maximumRebate =
                    new BigDecimal("60000");

            return tax.min(maximumRebate);
        }

        return ZERO;
    }




    public BigDecimal calculateOldRegimeRebate(
            BigDecimal taxableIncome,
            BigDecimal tax
    ) {

        if (
                taxableIncome.compareTo(
                        new BigDecimal("500000")
                ) <= 0
        ) {

            BigDecimal maximumRebate =
                    new BigDecimal("12500");

            return tax.min(maximumRebate);
        }

        return ZERO;
    }




    public BigDecimal calculateCess(
            BigDecimal taxAfterRebate,
            BigDecimal surcharge
    ) {

        BigDecimal base =
                nonNegative(taxAfterRebate)
                        .add(
                                nonNegative(surcharge)
                        );

        return round(
                percentage(base, 4)
        );
    }


    //sur

    public BigDecimal calculateSurcharge(
            BigDecimal taxableIncome,
            BigDecimal taxAfterRebate,
            String taxRegime
    ) {

        taxableIncome =
                nonNegative(taxableIncome);

        taxAfterRebate =
                nonNegative(taxAfterRebate);



        int rate = 0;


        if (
                taxableIncome.compareTo(
                        new BigDecimal("5000000")
                ) > 0
                        &&
                        taxableIncome.compareTo(
                                new BigDecimal("10000000")
                        ) <= 0
        ) {

            rate = 10;
        }



        else if (
                taxableIncome.compareTo(
                        new BigDecimal("10000000")
                ) > 0
                        &&
                        taxableIncome.compareTo(
                                new BigDecimal("20000000")
                        ) <= 0
        ) {

            rate = 15;
        }



        else if (
                taxableIncome.compareTo(
                        new BigDecimal("20000000")
                ) > 0
        ) {


            if (
                    "OLD".equalsIgnoreCase(taxRegime)
                            &&
                            taxableIncome.compareTo(
                                    new BigDecimal("50000000")
                            ) > 0
            ) {

                rate = 37;
            }

            else {

                rate = 25;
            }
        }


        return round(
                percentage(
                        taxAfterRebate,
                        rate
                )
        );
    }




    private BigDecimal percentage(
            BigDecimal amount,
            int percentage
    ) {

        return amount
                .multiply(
                        BigDecimal.valueOf(
                                percentage
                        )
                )
                .divide(
                        HUNDRED,
                        2,
                        RoundingMode.HALF_UP
                );
    }




    private BigDecimal nonNegative(
            BigDecimal value
    ) {

        if (value == null) {
            return ZERO;
        }

        return value.compareTo(ZERO) < 0
                ? ZERO
                : value;
    }
}