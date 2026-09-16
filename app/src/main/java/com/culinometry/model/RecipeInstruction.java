package com.culinometry.model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "recipe_id", childColumns = "recipe_id", onDelete = CASCADE),
        indices = @Index("recipe_id"))
public class RecipeInstruction {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recipe_instruction_id")
    private long recipeInstructionId;

    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @NonNull
    @ColumnInfo(name = "instruction")
    private String instruction;

    @ColumnInfo(name = "sort_order")
    private int sortOrder;

    public RecipeInstruction(long recipeId,
                             @NonNull String instruction,
                             int sortOrder) {
        this.recipeId = recipeId;
        this.instruction = instruction;
        this.sortOrder = sortOrder;
    }

    public long getRecipeInstructionId() {
        return recipeInstructionId;
    }

    public void setRecipeInstructionId(long recipeStepId) {
        this.recipeInstructionId = recipeStepId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    @NonNull
    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(@NonNull String instruction) {
        this.instruction = instruction;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
