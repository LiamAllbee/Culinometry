package com.culinometry.measurement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuantityParser {
    // Determine if input is mixed fraction "1 1/3"
    // Must begin with digit(s), then space(s), then digit(s), then slash, then must end with digit(s)
    //"^\\d+\\s+\\d+/\\d+$"
    private static final Pattern mixedFractionPattern = Pattern.compile("^\\d{1,9}\\s+\\d{1,9}/\\d{1,9}$");

    // Determine if input is regular fraction "1/3"
    // Must begin with digits, then slash, then end with digits
    //"^\\d+/\\d+$"
    private static final Pattern fractionPattern = Pattern.compile("^\\d{1,9}/\\d{1,9}$");

    // Determine if input is decimal / whole number
    // Must begin with any amount of digits, must then follow a sequence of . followed by digit,
    // but also . digit sequence part is optional at the end
    private static final Pattern decimalOrWholePattern = Pattern.compile("^\\d{1,9}(\\.\\d{1,9})?$");

    private QuantityParser() {}
    public static QuantityClassification classify(String input) {
        if (input == null || input.trim().isEmpty()) {
            return QuantityClassification.INVALID_EMPTY;
        }

        Matcher mixedFractionMatcher = mixedFractionPattern.matcher(input.trim());
        Matcher fractionMatcher = fractionPattern.matcher(input.trim());
        Matcher decimalOrWholeMatcher = decimalOrWholePattern.matcher(input.trim());

        boolean isMixedFraction = mixedFractionMatcher.matches();
        boolean isFraction = fractionMatcher.matches();
        boolean isDecimalOrWhole = decimalOrWholeMatcher.matches();

        if (isMixedFraction) {
            String[] splitFraction = input.trim().split("\\s+");
            String[] numeratorDenominator = splitFraction[1].split("/");

            int numerator = Integer.parseInt(numeratorDenominator[0]);
            int denominator = Integer.parseInt(numeratorDenominator[1]);

            if (denominator == 0) {
                return QuantityClassification.INVALID_DENOMINATOR;
            }
            else if (numerator >= denominator) {
                return QuantityClassification.INVALID_NUMERATOR;
            }
            else {
                return QuantityClassification.MIXED_FRACTION;
            }
        }
        else if (isFraction) {
            String[] numeratorDenominator = input.trim().split("/");

            int numerator = Integer.parseInt(numeratorDenominator[0]);
            int denominator = Integer.parseInt(numeratorDenominator[1]);
            if (denominator == 0) {
                return QuantityClassification.INVALID_DENOMINATOR;
            }
            else if (numerator >= denominator) {
                return QuantityClassification.INVALID_NUMERATOR;
            }
            else {
                return QuantityClassification.FRACTION;
            }
        }
        else if (isDecimalOrWhole) {
            return QuantityClassification.DECIMAL_OR_WHOLE;
        }

        return QuantityClassification.INVALID;
    }
    public static double parse(String input) {
        QuantityClassification type = classify(input);

        if (!type.isValid()) {
            throw new IllegalArgumentException();
        }

        return parse(type, input);
    }

    private static double parse(QuantityClassification type, String input) {
        switch (type) {
            case MIXED_FRACTION: {
                String[] splitFraction = input.trim().split("\\s+");
                String[] numeratorDenominator = splitFraction[1].split("/");

                int wholeNumber = Integer.parseInt(splitFraction[0]);
                int numerator = Integer.parseInt(numeratorDenominator[0]);
                int denominator = Integer.parseInt(numeratorDenominator[1]);

                return wholeNumber + ((double) numerator / denominator);
            }
            case FRACTION: {
                String[] numeratorDenominator = input.trim().split("/");

                int numerator = Integer.parseInt(numeratorDenominator[0]);
                int denominator = Integer.parseInt(numeratorDenominator[1]);
                return ((double) numerator / denominator);
            }
            case DECIMAL_OR_WHOLE: {
                return Double.parseDouble(input.trim());
            }
            default: {
                throw new IllegalArgumentException("Cannot parse invalid input");
            }
        }
    }
}
