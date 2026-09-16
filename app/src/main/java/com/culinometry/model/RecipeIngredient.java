package com.culinometry.model;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.culinometry.measurement.Unit;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Recipe.class,
                parentColumns = "recipe_id",
                childColumns = "recipe_id",
                onDelete = CASCADE),
        @ForeignKey(
                entity = Ingredient.class,
                parentColumns = "ingredient_id",
                childColumns = "ingredient_id",
                onDelete = RESTRICT)
        },
        indices = {
            @Index("recipe_id"), @Index("ingredient_id")
        })
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_ingredient_id")
    private long recipeIngredientId;

    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;

    @NonNull
    @ColumnInfo(name = "quantity")
    private String quantity;

    @NonNull
    @ColumnInfo(name = "unit")
    private Unit unit;

    @ColumnInfo(name = "unit_locked")
    private boolean unitLocked;

    @ColumnInfo(name = "sort_order")
    private int sortOrder;

    public RecipeIngredient(long recipeId,
                            long ingredientId,
                            @NonNull String quantity,
                            @NonNull Unit unit,
                            boolean unitLocked,
                            int sortOrder) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
        this.unitLocked = unitLocked;
        this.sortOrder = sortOrder;
    }

    public long getRecipeIngredientId() {
        return recipeIngredientId;
    }

    public void setRecipeIngredientId(long recipeIngredientId) {
        this.recipeIngredientId = recipeIngredientId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    @NonNull
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(@NonNull String quantity) {
        this.quantity = quantity;
    }

    @NonNull
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(@NonNull Unit unit) {
        this.unit = unit;
    }

    public boolean isUnitLocked() {
        return unitLocked;
    }

    public void setUnitLocked(boolean unitLocked) {
        this.unitLocked = unitLocked;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
