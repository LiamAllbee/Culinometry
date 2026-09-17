package com.culinometry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientWithIngredient;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    // This transaction returns a list of POJOs that lives in memory that is the combined objects
    // RecipeIngredient and Ingredient.
    // @Transaction must happen because it has to query RecipeIngredient and Ingredient. This keeps
    // the data atomic.
    // This transaction and query happens here and not in a "RecipeIngredientWithIngredients" DAO
    // because it's not a table it is an in memory only POJO and RecipeIngredient is the parent in
    // the RecipeIngredientWithIngredient class
    @Transaction
    @Query("SELECT * FROM RecipeIngredient WHERE recipe_id = :recipeId ORDER BY sort_order ASC")
    LiveData<List<RecipeIngredientWithIngredient>> getAllIngredientsForRecipe(long recipeId);

    @Insert
    List<Long> addAllRecipeIngredients(List<RecipeIngredient> recipeIngredientList);

    @Query("DELETE FROM RecipeIngredient WHERE recipe_id = :recipeId")
    void removeAllIngredientsForRecipe(long recipeId);
}
