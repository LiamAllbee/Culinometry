package com.culinometry.validation;

import com.culinometry.measurement.QuantityClassification;
import com.culinometry.measurement.QuantityParser;
import com.culinometry.model.RecipeIngredientDraft;

public class RecipeIngredientValidator {
    public static RecipeIngredientValidationState validate(RecipeIngredientDraft draft) {
        String ingredientError = null;
        String quantityError = null;
        String unitError = null;

        if (draft.getIngredientInput().isBlank()) {
            ingredientError = "Ingredient is required.";
        }
        else if (draft.getIngredient() == null) {
            ingredientError = "Select a valid ingredient.";
        }

        QuantityClassification classification = QuantityParser.classify(draft.getQuantity());
        if (classification == QuantityClassification.INVALID) {
            quantityError = "Input is invalid";
        }
        else if (classification == QuantityClassification.INVALID_EMPTY) {
            quantityError = "Amount is required.";
        }
        else if (classification == QuantityClassification.INVALID_DENOMINATOR) {
            quantityError = "Denominator cannot be zero";
        }
        else if (classification == QuantityClassification.INVALID_NUMERATOR) {
            quantityError = "Numerator cannot be larger than or equal to denominator";
        }

        if (draft.getUnit() == null) {
            unitError = "Unit is required.";
        }

        return new RecipeIngredientValidationState(ingredientError, quantityError, unitError);
    }
}
