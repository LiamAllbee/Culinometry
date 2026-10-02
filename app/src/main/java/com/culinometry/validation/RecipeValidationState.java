package com.culinometry.validation;

import java.util.Map;

public class RecipeValidationState {
    private final String nameError;
    private final Map<Long, RecipeIngredientValidationState> recipeIngredientErrors;
    private final Map<Long, String> recipeInstructionErrors;

    public RecipeValidationState(String nameError, Map<Long, RecipeIngredientValidationState> recipeIngredientErrors, Map<Long, String> recipeInstructionErrors) {
        this.nameError = nameError;
        this.recipeIngredientErrors = recipeIngredientErrors;
        this.recipeInstructionErrors = recipeInstructionErrors;
    }

    public String getNameError() {
        return nameError;
    }

    public Map<Long, RecipeIngredientValidationState> getRecipeIngredientErrors() {
        return recipeIngredientErrors;
    }

    public Map<Long, String> getRecipeInstructionErrors() {
        return recipeInstructionErrors;
    }

    public boolean isAllValid() {
        return nameError == null &&
                recipeIngredientErrors.isEmpty() &&
                recipeInstructionErrors.isEmpty();
    }
}
