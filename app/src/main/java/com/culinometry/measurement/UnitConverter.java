package com.culinometry.measurement;

public final class UnitConverter {
    private UnitConverter() {}

    public static double convert(double value,
                                 Unit originalUnit,
                                 Unit desiredUnit) {
        validate(value, originalUnit, desiredUnit);

        // if GRAM == GRAM conversion unnecessary
        if (originalUnit == desiredUnit) {
            return value;
        }

        // Turns any unit into Gs or mLs
        double normalizedValue = normalizeUnit(value, originalUnit);

        // Return the normalizedValue represented as the desiredUnit
        return convertToDesiredUnit(normalizedValue, desiredUnit);
    }

    private static double normalizeUnit(double value, Unit originalUnit) {
        switch (originalUnit) {

            // MASS -> grams
            case GRAM:
                return value;

            case KILOGRAM:
                return value * 1000.0;

            case OUNCE:
                return value * 28.349523125;

            case POUND:
                return value * 453.59237;


            // VOLUME -> milliliters
            case MILLILITER:
                return value;

            case LITER:
                return value * 1000.0;

            case US_TEASPOON:
                return value * 4.92892159375;

            case US_TABLESPOON:
                return value * 14.78676478125;

            case US_FLUID_OUNCE:
                return value * 29.5735295625;

            case US_CUP:
                return value * 236.5882365;

            case US_PINT:
                return value * 473.176473;

            case US_QUART:
                return value * 946.352946;

            case US_GALLON:
                return value * 3785.411784;

            default:
                throw new IllegalArgumentException("Unsupported unit: " + originalUnit);
        }
    }

    private static double convertToDesiredUnit(double baseValue, Unit desiredUnit) {
        switch (desiredUnit) {

            // grams -> MASS
            case GRAM:
                return baseValue;

            case KILOGRAM:
                return baseValue / 1000.0;

            case OUNCE:
                return baseValue / 28.349523125;

            case POUND:
                return baseValue / 453.59237;


            // milliliters -> VOLUME
            case MILLILITER:
                return baseValue;

            case LITER:
                return baseValue / 1000.0;

            case US_TEASPOON:
                return baseValue / 4.92892159375;

            case US_TABLESPOON:
                return baseValue / 14.78676478125;

            case US_FLUID_OUNCE:
                return baseValue / 29.5735295625;

            case US_CUP:
                return baseValue / 236.5882365;

            case US_PINT:
                return baseValue / 473.176473;

            case US_QUART:
                return baseValue / 946.352946;

            case US_GALLON:
                return baseValue / 3785.411784;

            default:
                throw new IllegalArgumentException(
                        "Unsupported unit: " + desiredUnit
                );
        }

    }

    private static void validate(double value,
                                 Unit originalUnit,
                                 Unit desiredUnit) {

        if (value < 0) {
            throw new IllegalArgumentException(
                    "Measurement cannot be negative."
            );
        }

        if (originalUnit == null || desiredUnit == null) {
            throw new IllegalArgumentException(
                    "Units cannot be null."
            );
        }

        if (originalUnit.getType() != desiredUnit.getType()) {
            throw new IllegalArgumentException(
                    "UnitConverter cannot convert between mass and volume."
            );
        }
    }
}
