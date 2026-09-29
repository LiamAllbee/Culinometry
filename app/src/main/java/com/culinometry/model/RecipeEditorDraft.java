package com.culinometry.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeEditorDraft {
    private final RecipeDraft recipeDraft;

    private final List<RecipeIngredientDraft> recipeIngredientDrafts;

    private final List<RecipeInstructionDraft> recipeInstructionDrafts;

    public RecipeEditorDraft(RecipeDraft recipeDraft,
                             List<RecipeIngredientDraft> recipeIngredientDrafts,
                             List<RecipeInstructionDraft> recipeInstructionDrafts) {
        this.recipeDraft = recipeDraft;

        // Make both lists impossible to update manually. Lists can only be updated by publishing a
        // list through setValue() to the MutableLiveData
        this.recipeIngredientDrafts =
                Collections.unmodifiableList(
                        new ArrayList<>(recipeIngredientDrafts)
                );

        this.recipeInstructionDrafts =
                Collections.unmodifiableList(
                        new ArrayList<>(recipeInstructionDrafts)
                );
    }

    public RecipeDraft getRecipeDraft() {
        return recipeDraft;
    }

    public List<RecipeIngredientDraft> getRecipeIngredientDrafts() {
        return recipeIngredientDrafts;
    }

    public List<RecipeInstructionDraft> getRecipeInstructionDrafts() {
        return recipeInstructionDrafts;
    }
}
