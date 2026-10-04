package com.culinometry.model;

public class RecipeReportRow {
    private final long recipeId;
    private final String recipeName;
    private final int ingredientCount;
    private final int instructionCount;

    public RecipeReportRow(
            long recipeId,
            String recipeName,
            int ingredientCount,
            int instructionCount) {

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.ingredientCount = ingredientCount;
        this.instructionCount = instructionCount;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public int getInstructionCount() {
        return instructionCount;
    }
}
