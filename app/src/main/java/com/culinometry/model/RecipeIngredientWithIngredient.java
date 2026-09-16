package com.culinometry.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RecipeIngredientWithIngredient {
    @Embedded
    private RecipeIngredient recipeIngredient;

    // After RecipeIngredient gets initialized @Relation takes the ingredient_id from recipeIngredient
    // and initializes it based on the ingredient_id column in ingredient
    @Relation(
            parentColumn = "ingredient_id",
            entityColumn = "ingredient_id"
    )
    private Ingredient ingredient;

    public RecipeIngredient getRecipeIngredient() {
        return recipeIngredient;
    }

    public void setRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.recipeIngredient = recipeIngredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }
}
