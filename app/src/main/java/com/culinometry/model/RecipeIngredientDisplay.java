package com.culinometry.model;

import com.culinometry.measurement.Unit;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientDisplay {
    private final long recipeIngredientId;
    private final int sortOrder;
    private final String ingredientName;
    private final String quantity;
    private final Unit selectedUnit;
    private final List<Unit> availableUnits;
    private final boolean isConversionEnabled;

    public RecipeIngredientDisplay(long recipeIngredientId, int sortOrder, String ingredientName, String quantity, Unit selectedUnit, List<Unit> availableUnits, boolean isConversionEnabled) {
        this.recipeIngredientId = recipeIngredientId;
        this.sortOrder = sortOrder;
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.selectedUnit = selectedUnit;
        this.availableUnits = new ArrayList<>(availableUnits);
        this.isConversionEnabled = isConversionEnabled;
    }

    public long getRecipeIngredientId() {
        return recipeIngredientId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getQuantity() {
        return quantity;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public List<Unit> getAvailableUnits() {
        return availableUnits;
    }

    public boolean isConversionEnabled() {
        return isConversionEnabled;
    }
}
