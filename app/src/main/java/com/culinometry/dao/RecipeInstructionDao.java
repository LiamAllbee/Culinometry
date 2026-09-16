package com.culinometry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.culinometry.model.RecipeInstruction;

import java.util.List;

@Dao
public interface RecipeInstructionDao {
    @Query("SELECT * FROM RecipeInstruction WHERE recipe_id = :recipeId ORDER BY sort_order ASC")
    LiveData<List<RecipeInstruction>> getAllRecipeInstructionsForRecipe(long recipeId);

    @Insert
    long insert(RecipeInstruction recipeInstruction);

    @Update
    void update(RecipeInstruction recipeInstruction);

    @Delete
    void delete(RecipeInstruction recipeInstruction);
}