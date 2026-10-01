package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientWithIngredient;
import com.culinometry.model.RecipeInstruction;
import com.culinometry.repo.CulinometryRepository;

import java.util.List;

public class RecipeDetailViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    private boolean initialized = false;
    private long recipeId = -1L;
    private LiveData<Recipe> recipe;
    private LiveData<List<RecipeIngredientWithIngredient>> recipeIngredientWithIngredientList;
    private LiveData<List<RecipeInstruction>> recipeInstructionList;

    public RecipeDetailViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public void initializeRecipe(long recipeId) {
        // Forces initialization to only run once
        if (initialized) {
            return;
        }

        initialized = true;
        this.recipeId = recipeId;

        this.recipe = repo.getRecipe(recipeId);
        this.recipeIngredientWithIngredientList = repo.getAllIngredientsForRecipe(recipeId);
        this.recipeInstructionList = repo.getAllRecipeInstructionsForRecipe(recipeId);
    }

    public LiveData<Recipe> getRecipe() {
        return recipe;
    }
    
    public LiveData<List<RecipeIngredientWithIngredient>> getRecipeIngredientWithIngredientList() {
        return recipeIngredientWithIngredientList;
    }

    public LiveData<List<RecipeInstruction>> getRecipeInstructionList() {
        return recipeInstructionList;
    }

    public void deleteRecipe() {
        Recipe recipe = this.recipe.getValue();
        repo.deleteRecipe(recipe);
    }
}
