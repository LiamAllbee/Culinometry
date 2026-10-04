package com.culinometry.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.culinometry.model.RecipeReportRow;

import java.util.List;

@Dao
public interface ReportDao {
    @Query(
            "SELECT " +
            "r.recipe_id AS recipeId, " +
            "r.name AS recipeName, " +
            "(SELECT COUNT(*) " +
            "FROM RecipeIngredient ri " +
            "WHERE ri.recipe_id = r.recipe_id) AS ingredientCount, " +
            "(SELECT COUNT(*) " +
            "FROM RecipeInstruction rins " +
            "WHERE rins.recipe_id = r.recipe_id) AS instructionCount " +
            "FROM Recipe r " +
            "ORDER BY r.name COLLATE NOCASE"
    )
    LiveData<List<RecipeReportRow>> getRecipeReport();
}
