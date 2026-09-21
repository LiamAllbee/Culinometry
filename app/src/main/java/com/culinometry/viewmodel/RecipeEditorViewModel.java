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

public class RecipeEditorViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    public RecipeEditorViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
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
