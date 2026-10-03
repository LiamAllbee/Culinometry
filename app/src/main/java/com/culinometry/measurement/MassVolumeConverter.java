package com.culinometry.measurement;

import com.culinometry.model.Ingredient;

public final class MassVolumeConverter {
    private MassVolumeConverter() {}

    public static double convert(double value,
                                 Unit originalUnit,
                                 Unit desiredUnit,
                                 Ingredient ingredient) {
        validate(value, originalUnit, desiredUnit, ingredient);

        // Calculate the ingredients reference volume and reference mass
        double referenceMass = QuantityParser.parse(ingredient.getReferenceMass());
        double referenceVolume = QuantityParser.parse(ingredient.getReferenceVolume());
        double referenceMassGrams = UnitConverter.convert(referenceMass, ingredient.getMassUnit(), Unit.GRAM);
        double referenceVolumeMilliliters = UnitConverter.convert(referenceVolume, ingredient.getVolumeUnit(), Unit.MILLILITER);

        // Responsible for converting Mass input into Volume output
        if (originalUnit.getType() == MeasurementType.MASS) {
            double inputToGrams = UnitConverter.convert(value, originalUnit, Unit.GRAM);

            double convertInputToMilliliter = inputToGrams * referenceVolumeMilliliters / referenceMassGrams;

            // Return whatever the input is as the desiredUnit
            return UnitConverter.convert(convertInputToMilliliter, Unit.MILLILITER, desiredUnit);
        }
        // Responsible for converting Volume input into Mass output
        else {
            double inputToMilliliter = UnitConverter.convert(value, originalUnit, Unit.MILLILITER);

            double convertInputToGram = inputToMilliliter * referenceMassGrams / referenceVolumeMilliliters;

            // Return whatever the input is as the desiredUnit
            return UnitConverter.convert(convertInputToGram, Unit.GRAM, desiredUnit);
        }
    }

    private static void validate(double value,
                                 Unit fromUnit,
                                 Unit toUnit,
                                 Ingredient ingredient) {

        if (value < 0) {
            throw new IllegalArgumentException(
                    "Measurement cannot be negative."
            );
        }

        if (fromUnit == null || toUnit == null) {
            throw new IllegalArgumentException(
                    "Units cannot be null."
            );
        }

        if (ingredient == null) {
            throw new IllegalArgumentException(
                    "Ingredient cannot be null."
            );
        }

        if (ingredient.getMode()
                != IngredientMode.MASS_AND_VOLUME) {

            throw new IllegalArgumentException(
                    "Ingredient does not support mass-volume conversion."
            );
        }

        if (ingredient.getReferenceMass() == null
                || ingredient.getMassUnit() == null
                || ingredient.getReferenceVolume() == null
                || ingredient.getVolumeUnit() == null) {

            throw new IllegalArgumentException(
                    "Ingredient is missing reference measurements."
            );
        }

        if (fromUnit.getType() == toUnit.getType()) {
            throw new IllegalArgumentException(
                    "MassVolumeConverter requires different measurement types."
            );
        }
    }
}
