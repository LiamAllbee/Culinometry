package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeDraft;
import com.culinometry.model.RecipeEditorDraft;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientDraft;
import com.culinometry.model.RecipeIngredientWithIngredient;
import com.culinometry.model.RecipeInstruction;
import com.culinometry.model.RecipeInstructionDraft;
import com.culinometry.repo.CulinometryRepository;

import java.util.ArrayList;
import java.util.List;

public class RecipeEditorViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    private final MutableLiveData<RecipeEditorDraft> editorDraft = new MutableLiveData<>();

    private long nextInstructionDraftId = 1L;

    private long recipeId = -1L;
    private boolean initialized = false;

    public RecipeEditorViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public LiveData<RecipeEditorDraft> getEditorDraft() {
        return editorDraft;
    }

    public void initializeRecipe(long recipeId) {
        if (initialized) {
            return;
        }

        initialized = true;
        this.recipeId = recipeId;

        if (recipeId != -1L) {
            // Initialize existing recipe
        }
        else {
            RecipeDraft recipeDraft = new RecipeDraft("", "");
            List<RecipeIngredientDraft> recipeIngredientDrafts = new ArrayList<>();
            List<RecipeInstructionDraft> recipeInstructionDrafts = new ArrayList<>();

            RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(recipeDraft, recipeIngredientDrafts, recipeInstructionDrafts);

            editorDraft.setValue(newRecipeEditorDraft);
        }

    }
    public void addInstruction() {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeInstructions then add a new RecipeInstruction object to
        // the list then publish with new RecipeEditorDraft
        // Initially the current list but WILL become the updated version
        List<RecipeInstructionDraft> updatedInstructions = new ArrayList<>(current.getRecipeInstructionDrafts());

        RecipeInstructionDraft newRecipeInstructionDraft = new RecipeInstructionDraft(nextInstructionDraftId++);

        updatedInstructions.add(newRecipeInstructionDraft);

        RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), current.getRecipeIngredientDrafts(), updatedInstructions);
        editorDraft.setValue(newRecipeEditorDraft);
    }

    public void updateInstruction(long draftId, String newInstruction) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeInstructions then replace the instruction with matching
        // draftId then publish with new RecipeEditorDraft
        // Initially the current list but WILL become the updated version
        List<RecipeInstructionDraft> updatedInstructions = new ArrayList<>(current.getRecipeInstructionDrafts());;

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedInstructions.size(); i++) {
            if (updatedInstructions.get(i).getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (!updatedInstructions.get(i).getInstruction().equals(newInstruction)) {
                    RecipeInstructionDraft updatedDraft = new RecipeInstructionDraft(draftId, newInstruction);
                    updatedInstructions.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), current.getRecipeIngredientDrafts(), updatedInstructions);
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }

    }

    public void deleteInstruction(long draftId) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeInstructions then delete the instruction with matching
        // draftId then publish with new RecipeEditorDraft
        // Initially the current list but WILL become the updated version
        List<RecipeInstructionDraft> updatedInstructions = new ArrayList<>(current.getRecipeInstructionDrafts());;

        boolean isRemoved = updatedInstructions.removeIf(
                    draft -> draft.getDraftId() == draftId
        );

        if (!isRemoved) {
            return;
        }

        RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), current.getRecipeIngredientDrafts(), updatedInstructions);
        editorDraft.setValue(newRecipeEditorDraft);
    }



















    public LiveData<Recipe> getRecipe(long recipeId) {
        return repo.getRecipe(recipeId);
    }

    public LiveData<List<RecipeIngredientWithIngredient>> getAllIngredientsForRecipe(long recipeId) {
        return repo.getAllIngredientsForRecipe(recipeId);
    }

    public LiveData<List<RecipeInstruction>> getAllRecipeInstructionsForRecipe(long recipeId) {
        return repo.getAllRecipeInstructionsForRecipe(recipeId);
    }

    public void saveNewRecipe(Recipe recipe, List<RecipeIngredient> recipeIngredientList, List<RecipeInstruction> recipeInstructionList) {
        repo.saveNewRecipe(recipe, recipeIngredientList, recipeInstructionList);
    }

    public void updateRecipe(Recipe recipe, List<RecipeIngredient> recipeIngredientList, List<RecipeInstruction> recipeInstructionList) {
        repo.updateRecipe(recipe, recipeIngredientList, recipeInstructionList);
    }
}
