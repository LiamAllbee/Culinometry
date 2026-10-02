package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeDraft;
import com.culinometry.model.RecipeEditorDraft;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientDraft;
import com.culinometry.model.RecipeIngredientWithIngredient;
import com.culinometry.model.RecipeInstruction;
import com.culinometry.model.RecipeInstructionDraft;
import com.culinometry.repo.CulinometryRepository;
import com.culinometry.validation.RecipeValidationState;
import com.culinometry.validation.RecipeValidator;

import java.util.ArrayList;
import java.util.List;

public class RecipeEditorViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    private final MediatorLiveData<RecipeEditorDraft> editorDraft = new MediatorLiveData<>();
    private Recipe loadedRecipe;
    private List<RecipeIngredientWithIngredient> loadedRIWIList;
    private List<RecipeInstruction> loadedInstructionList;
    private boolean existingRecipeSynced = false;

    private long nextInstructionDraftId = 1L;
    private long nextIngredientDraftId = 1L;

    private long recipeId = -1L;
    private boolean initialized = false;

    private List<Ingredient> ingredientCatalog = new ArrayList<>();

    private final MediatorLiveData<List<Ingredient>> ingredientCatalogLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<RecipeValidationState> validationState = new MediatorLiveData<>();

    public RecipeEditorViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);

        ingredientCatalogLiveData.addSource(
                repo.getAllIngredients(), ingredients -> {
                    if (ingredients == null) {
                        ingredientCatalog = new ArrayList<>();
                    }
                    else {
                        ingredientCatalog = new ArrayList<>(ingredients);
                    }

                    ingredientCatalogLiveData.setValue(ingredientCatalog);
                });

        validationState.addSource(
                editorDraft, draft -> {
                    if (draft != null) {
                        validationState.setValue(RecipeValidator.validate(draft));
                    }
                }
        );
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Recipe Draft methods
    public void setRecipeName(String name) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current Recipe then if names don't match replace create new Recipe and put it in
        // new RecipeEditorDraft then publish
        RecipeDraft currentRecipe = current.getRecipeDraft();

        if (!currentRecipe.getName().equals(name)) {
            RecipeDraft updatedRecipe = new RecipeDraft(
                    name,
                    currentRecipe.getDescription()
            );

            RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(updatedRecipe, current.getRecipeIngredientDrafts(), current.getRecipeInstructionDrafts());
            editorDraft.setValue(newRecipeEditorDraft);
        }
    }

    public void setRecipeDescription(String description) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current Recipe then if descriptions don't match replace create new Recipe and
        // put it in a new RecipeEditorDraft then publish
        RecipeDraft currentRecipe = current.getRecipeDraft();

        if (!currentRecipe.getDescription().equals(description)) {
            RecipeDraft updatedRecipe = new RecipeDraft(
                    currentRecipe.getName(),
                    description
            );

            RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(updatedRecipe, current.getRecipeIngredientDrafts(), current.getRecipeInstructionDrafts());
            editorDraft.setValue(newRecipeEditorDraft);
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Instruction Draft Methods
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

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Ingredient Draft Methods
    public void addIngredient() {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then add a new RecipeIngredient object to
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        RecipeIngredientDraft newRecipeIngredientDraft = new RecipeIngredientDraft(nextIngredientDraftId++);

        updatedIngredients.add(newRecipeIngredientDraft);

        RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
        editorDraft.setValue(newRecipeEditorDraft);
    }
    public void updateIngredientInput(long draftId, String newIngredientInput) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then replace a RecipeIngredient object in
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedIngredients.size(); i++) {
            RecipeIngredientDraft currentDraft = updatedIngredients.get(i);

            if (currentDraft.getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (!currentDraft.getIngredientInput().equals(newIngredientInput)) {

                    // EITHER RETURNS NULL OR RETURNS A MATCHING INGREDIENT
                    Ingredient matchedIngredient = findIngredientByName(newIngredientInput);
                    RecipeIngredientDraft updatedDraft = new RecipeIngredientDraft(
                            draftId,
                            matchedIngredient,
                            newIngredientInput,
                            currentDraft.getQuantity(),
                            null,
                            currentDraft.isUnitLocked());

                    updatedIngredients.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }
    }

    // Check every ingredient in the list of ingredients to see if the typed name matches. Use case
    // if user does not select from the drop-down and instead types the full name.
    private Ingredient findIngredientByName(String input) {
        String normalizedInput = input.trim();

        for (Ingredient ingredient : ingredientCatalog) {
            if (ingredient.getName().equalsIgnoreCase(normalizedInput)) {
                return ingredient;
            }
        }

        return null;
    }

    public void updateIngredientSelection(long draftId, Ingredient newIngredient) {
        if (newIngredient == null) {
            return;
        }

        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then replace a RecipeIngredient object in
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedIngredients.size(); i++) {
            RecipeIngredientDraft currentDraft = updatedIngredients.get(i);

            if (currentDraft.getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (currentDraft.getIngredient() == null || currentDraft.getIngredient().getIngredientId() != newIngredient.getIngredientId()) {

                    RecipeIngredientDraft updatedDraft = new RecipeIngredientDraft(
                            draftId,
                            newIngredient,
                            newIngredient.getName(),
                            currentDraft.getQuantity(),
                            null,
                            currentDraft.isUnitLocked());

                    updatedIngredients.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }
    }

    public void updateQuantity(long draftId, String newQuantity) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then replace a RecipeIngredient object in
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedIngredients.size(); i++) {
            RecipeIngredientDraft currentDraft = updatedIngredients.get(i);

            if (currentDraft.getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (!currentDraft.getQuantity().equals(newQuantity)) {

                    RecipeIngredientDraft updatedDraft = new RecipeIngredientDraft(
                            draftId,
                            currentDraft.getIngredient(),
                            currentDraft.getIngredientInput(),
                            newQuantity,
                            currentDraft.getUnit(),
                            currentDraft.isUnitLocked());

                    updatedIngredients.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }
    }

    public void updateUnit(long draftId, Unit unit) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then replace a RecipeIngredient object in
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedIngredients.size(); i++) {
            RecipeIngredientDraft currentDraft = updatedIngredients.get(i);

            if (currentDraft.getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (currentDraft.getUnit() != unit) {

                    RecipeIngredientDraft updatedDraft = new RecipeIngredientDraft(
                            draftId,
                            currentDraft.getIngredient(),
                            currentDraft.getIngredientInput(),
                            currentDraft.getQuantity(),
                            unit,
                            currentDraft.isUnitLocked());

                    updatedIngredients.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }
    }

    public void updateUnitLocked(long draftId, boolean newUnitLocked) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then replace a RecipeIngredient object in
        // the list then publish with new RecipeEditorDraft

        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());

        // If texts match do NOTHING to prevent unnecessary publishing
        for (int i = 0; i < updatedIngredients.size(); i++) {
            RecipeIngredientDraft currentDraft = updatedIngredients.get(i);

            if (currentDraft.getDraftId() == draftId) {
                // If texts do NOT match but IDs do perform the update and publish
                if (currentDraft.isUnitLocked() != newUnitLocked) {

                    RecipeIngredientDraft updatedDraft = new RecipeIngredientDraft(
                            draftId,
                            currentDraft.getIngredient(),
                            currentDraft.getIngredientInput(),
                            currentDraft.getQuantity(),
                            currentDraft.getUnit(),
                            newUnitLocked);

                    updatedIngredients.set(i, updatedDraft);

                    RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
                    editorDraft.setValue(newRecipeEditorDraft);
                }
                return;
            }
        }
    }

    public void deleteRecipeIngredient(long draftId) {
        RecipeEditorDraft current = editorDraft.getValue();

        if (current == null) {
            return;
        }

        // Get the current list of RecipeIngredients then delete the RecipeIngredient with matching
        // draftId then publish with new RecipeEditorDraft
        // Initially the current list but WILL become the updated version
        List<RecipeIngredientDraft> updatedIngredients = new ArrayList<>(current.getRecipeIngredientDrafts());;

        boolean isRemoved = updatedIngredients.removeIf(
                draft -> draft.getDraftId() == draftId
        );

        if (!isRemoved) {
            return;
        }

        RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(current.getRecipeDraft(), updatedIngredients, current.getRecipeInstructionDrafts());
        editorDraft.setValue(newRecipeEditorDraft);
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Misc functionality methods

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
            LiveData<Recipe> recipeLiveData = repo.getRecipe(recipeId);
            LiveData<List<RecipeIngredientWithIngredient>> recipeIngredientListWithIngredientLiveData = repo.getAllIngredientsForRecipe(recipeId);
            LiveData<List<RecipeInstruction>> recipeInstructionListLiveData = repo.getAllRecipeInstructionsForRecipe(recipeId);

            editorDraft.addSource(recipeLiveData, recipe -> {
                loadedRecipe = recipe;
                tryInitializeExistingRecipe();
            });

            editorDraft.addSource(recipeIngredientListWithIngredientLiveData, recipeIngredientWithIngredients -> {
                loadedRIWIList = recipeIngredientWithIngredients;
                tryInitializeExistingRecipe();
            });

            editorDraft.addSource(recipeInstructionListLiveData, recipeInstructions -> {
                loadedInstructionList = recipeInstructions;
                tryInitializeExistingRecipe();
            });

        }
        else {
            RecipeDraft recipeDraft = new RecipeDraft("", "");
            List<RecipeIngredientDraft> recipeIngredientDrafts = new ArrayList<>();
            List<RecipeInstructionDraft> recipeInstructionDrafts = new ArrayList<>();

            RecipeEditorDraft newRecipeEditorDraft = new RecipeEditorDraft(recipeDraft, recipeIngredientDrafts, recipeInstructionDrafts);

            editorDraft.setValue(newRecipeEditorDraft);
        }

    }

    private void tryInitializeExistingRecipe() {
        if (existingRecipeSynced) {
            return;
        }

        if (loadedRecipe == null || loadedRIWIList == null || loadedInstructionList == null) {
            return;
        }

        RecipeDraft recipeDraft = new RecipeDraft(
                loadedRecipe.getName(),
                loadedRecipe.getDescription() == null ? "" : loadedRecipe.getDescription());

        List<RecipeIngredientDraft> recipeIngredientDraftList = new ArrayList<>();

        // Initialize list of recipeIngredientDrafts
        for (RecipeIngredientWithIngredient rIWI : loadedRIWIList) {
            RecipeIngredientDraft draft = new RecipeIngredientDraft(
                    nextIngredientDraftId++,
                    rIWI.getIngredient(),
                    rIWI.getIngredient().getName(),
                    rIWI.getRecipeIngredient().getQuantity(),
                    rIWI.getRecipeIngredient().getUnit(),
                    rIWI.getRecipeIngredient().isUnitLocked()
            );

            recipeIngredientDraftList.add(draft);
        }

        List<RecipeInstructionDraft> recipeInstructionDraftList = new ArrayList<>();

        // Initialize list of recipeIngredientDrafts
        for (RecipeInstruction recipeInstruction : loadedInstructionList) {
            RecipeInstructionDraft draft = new RecipeInstructionDraft(
                    nextInstructionDraftId++,
                    recipeInstruction.getInstruction()
            );

            recipeInstructionDraftList.add(draft);
        }

        existingRecipeSynced = true;

        RecipeEditorDraft updatedEditorDraft = new RecipeEditorDraft(
                recipeDraft,
                recipeIngredientDraftList,
                recipeInstructionDraftList);

        editorDraft.setValue(updatedEditorDraft);
    }
    public boolean isNewRecipe() {
        return recipeId == -1L;
    }

    public void saveRecipe() {
        RecipeEditorDraft currentEditorDraft = editorDraft.getValue();

        if (currentEditorDraft == null) {
            return;
        }

        RecipeDraft recipeDraft = currentEditorDraft.getRecipeDraft();
        List<RecipeIngredientDraft> recipeIngredientDrafts = currentEditorDraft.getRecipeIngredientDrafts();
        List<RecipeInstructionDraft> recipeInstructionDrafts = currentEditorDraft.getRecipeInstructionDrafts();

        Recipe recipe = new Recipe(
                recipeDraft.getName(),
                recipeDraft.getDescription()
        );

        if (!isNewRecipe()) {
            recipe.setRecipeId(recipeId);
            recipe.setCreatedAt(loadedRecipe.getCreatedAt());
        }

        List<RecipeIngredient> recipeIngredientList = new ArrayList<>();

        for (int i = 0; i < recipeIngredientDrafts.size(); i++) {
            RecipeIngredientDraft currentDraft = recipeIngredientDrafts.get(i);
            RecipeIngredient recipeIngredient = new RecipeIngredient(
                    -1L,
                    currentDraft.getIngredient().getIngredientId(),
                    currentDraft.getQuantity(),
                    currentDraft.getUnit(),
                    currentDraft.isUnitLocked(),
                    i
            );

            recipeIngredientList.add(recipeIngredient);
        }

        List<RecipeInstruction> recipeInstructionList = new ArrayList<>();

        for (int i = 0; i < recipeInstructionDrafts.size(); i++) {
            RecipeInstructionDraft currentDraft = recipeInstructionDrafts.get(i);
            RecipeInstruction recipeInstruction = new RecipeInstruction(
                    -1L,
                    currentDraft.getInstruction(),
                    i
            );

            recipeInstructionList.add(recipeInstruction);
        }

        if(isNewRecipe()) {
            repo.saveNewRecipe(recipe, recipeIngredientList, recipeInstructionList);
        }
        else {
            repo.updateRecipe(recipe, recipeIngredientList, recipeInstructionList);
        }
    }

    public LiveData<RecipeValidationState> getValidationState() {
        return validationState;
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return ingredientCatalogLiveData;
    }
}
