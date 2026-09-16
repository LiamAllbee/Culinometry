package com.culinometry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.culinometry.model.Ingredient;

import java.util.List;

@Dao
public interface IngredientDao {
    @Query("SELECT * FROM Ingredient WHERE ingredient_id = :ingredientId")
    LiveData<Ingredient> getIngredient(long ingredientId);

    @Query("SELECT * FROM Ingredient WHERE is_soft_deleted = 0 ORDER BY name ASC")
    LiveData<List<Ingredient>> getAllIngredients();

    @Query("SELECT * FROM Ingredient WHERE is_soft_deleted = 1 ORDER BY name ASC")
    LiveData<List<Ingredient>> getAllSoftDeletedIngredients();

    // Delete that clears any archived ingredients with no references to a recipe
    @Query("DELETE FROM Ingredient WHERE is_soft_deleted = 1 AND NOT EXISTS (SELECT 1 FROM RecipeIngredient WHERE RecipeIngredient.ingredient_id = Ingredient.ingredient_id)")
    int deleteUnusedSoftDeletedIngredients();

    // Purpose is to soft-delete and recover soft-deletions
    @Query("UPDATE Ingredient SET is_soft_deleted = :softDeleted WHERE ingredient_id = :ingredientId")
    int setSoftDeleted(long ingredientId, boolean softDeleted);

    @Insert
    long insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);
}