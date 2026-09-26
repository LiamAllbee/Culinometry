package com.culinometry.validation;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.QuantityClassification;
import com.culinometry.measurement.QuantityParser;
import com.culinometry.model.Ingredient;

import java.util.List;

public class IngredientValidator {
    public static IngredientValidationState validate(Ingredient ingredientDraft, List<Ingredient> existingIngredients) {
        String nameError = null;
        String massError = null;
        String massUnitError = null;
        String volumeError = null;
        String volumeUnitError = null;

        // Name validation
        if (ingredientDraft.getName().isEmpty()) {
            nameError = "Name is required!";
        }

        if (existingIngredients != null) {
            for (Ingredient i : existingIngredients) {
                // If they have different IDs and their name matches return error
                if (ingredientDraft.getIngredientId() != i.getIngredientId() && ingredientDraft.getName().equalsIgnoreCase(i.getName())) {
                    nameError = ingredientDraft.getName() + " already exists! Names cannot match.";
                    break;
                }
            }
        }

        if (ingredientDraft.getMode() == IngredientMode.MASS_AND_VOLUME) {
            // Mass validation
            QuantityClassification massClassification = QuantityParser.classify(ingredientDraft.getReferenceMass());
            if (massClassification == QuantityClassification.INVALID) {
                massError = "Input is invalid";
            } else if (massClassification == QuantityClassification.INVALID_EMPTY) {
                massError = "Mass is required.";
            } else if (massClassification == QuantityClassification.INVALID_DENOMINATOR) {
                massError = "Denominator cannot be zero";
            } else if (massClassification == QuantityClassification.INVALID_NUMERATOR) {
                massError = "Numerator cannot be larger than or equal to denominator";
            }

            //Volume Validation
            QuantityClassification volumeClassification = QuantityParser.classify(ingredientDraft.getReferenceVolume());
            if (volumeClassification == QuantityClassification.INVALID) {
                volumeError = "Input is invalid";
            } else if (volumeClassification == QuantityClassification.INVALID_EMPTY) {
                volumeError = "Volume is required.";
            } else if (volumeClassification == QuantityClassification.INVALID_DENOMINATOR) {
                volumeError = "Denominator cannot be zero";
            } else if (volumeClassification == QuantityClassification.INVALID_NUMERATOR) {
                volumeError = "Numerator cannot be larger than or equal to denominator";
            }

            // Mass Unit Validation
            if (ingredientDraft.getMassUnit() == null) {
                massUnitError = "Unit is required.";
            }

            // Volume Unit Validation
            if (ingredientDraft.getVolumeUnit() == null) {
                volumeUnitError = "Unit is required.";
            }

        }

        return new IngredientValidationState(nameError,
                massError,
                massUnitError,
                volumeError,
                volumeUnitError);
    }
}
