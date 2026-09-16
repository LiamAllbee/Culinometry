package com.culinometry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.culinometry.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM Recipe WHERE recipe_id = :recipeId")
    LiveData<Recipe> getRecipe(long recipeId);

    @Query("SELECT * FROM Recipe ORDER BY name ASC")
    LiveData<List<Recipe>> getAllRecipes();

    @Insert
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);
}