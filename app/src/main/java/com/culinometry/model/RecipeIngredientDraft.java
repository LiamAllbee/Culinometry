package com.culinometry.model;

import com.culinometry.measurement.Unit;

public class RecipeIngredientDraft {
    private final long draftId;
    private final Ingredient ingredient;
    private final String ingredientInput;
    private final String quantity;
    private final Unit unit;
    private final boolean unitLocked;

    // Constructor when data is available
    public RecipeIngredientDraft(long draftId,
                                 Ingredient ingredient,
                                 String ingredientInput,
                                 String quantity,
                                 Unit unit,
                                 boolean unitLocked) {
        this.draftId = draftId;
        this.ingredient = ingredient;
        this.ingredientInput = ingredientInput;
        this.quantity = quantity;
        this.unit = unit;
        this.unitLocked = unitLocked;
    }

    // Constructor when adding ingredient
    public RecipeIngredientDraft(long draftId) {
        this.draftId = draftId;
        this.ingredient = null;
        this.ingredientInput = "";
        this.quantity = "";
        this.unit = null;
        this.unitLocked = false;
    }

    public long getDraftId() {
        return draftId;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getIngredientInput() {
        return ingredientInput;
    }

    public String getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public boolean isUnitLocked() {
        return unitLocked;
    }
}
