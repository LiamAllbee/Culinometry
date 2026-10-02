package com.culinometry.validation;

public class RecipeIngredientValidationState {
    private final String ingredientError;
    private final String quantityError;
    private final String unitError;

    public RecipeIngredientValidationState(String ingredientError, String quantityError, String unitError) {
        this.ingredientError = ingredientError;
        this.quantityError = quantityError;
        this.unitError = unitError;
    }

    public String getIngredientError() {
        return ingredientError;
    }

    public String getQuantityError() {
        return quantityError;
    }

    public String getUnitError() {
        return unitError;
    }

    public boolean isAllValid() {
        return ingredientError == null
                && quantityError == null
                && unitError == null;
    }
}
