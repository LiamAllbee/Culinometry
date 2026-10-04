package com.culinometry.validation;

import com.culinometry.model.RecipeEditorDraft;
import com.culinometry.model.RecipeIngredientDraft;
import com.culinometry.model.RecipeInstructionDraft;

import java.util.HashMap;
import java.util.Map;

public class RecipeValidator {
    public static RecipeValidationState validate(RecipeEditorDraft editorDraft) {
        String nameError = null;
        Map<Long, RecipeIngredientValidationState> recipeIngredientErrors = new HashMap<>();
        Map<Long, String> recipeInstructionErrors = new HashMap<>();

        // Recipe name
        if (editorDraft.getRecipeDraft().getName().isBlank()) {
            nameError = "Recipe name is required.";
        }

        // RecipeIngredient Rows Validation
        for (RecipeIngredientDraft draft : editorDraft.getRecipeIngredientDrafts()) {
            RecipeIngredientValidationState curRowState = RecipeIngredientValidator.validate(draft);

            // Only store validation state of rows that produce errors.
            if (!curRowState.isAllValid()) {
                recipeIngredientErrors.put(draft.getDraftId(), curRowState);
            }
        }

        for (RecipeInstructionDraft draft : editorDraft.getRecipeInstructionDrafts()) {
            // Only store validation state of rows that produce errors.
            if (draft.getInstruction().isBlank()) {
                recipeInstructionErrors.put(draft.getDraftId(), "Instruction is required.");
            }
        }

        return new RecipeValidationState(nameError, recipeIngredientErrors, recipeInstructionErrors);
    }
}
