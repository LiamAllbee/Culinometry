package com.culinometry.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.culinometry.dao.IngredientDao;
import com.culinometry.dao.RecipeDao;
import com.culinometry.dao.RecipeIngredientDao;
import com.culinometry.dao.RecipeInstructionDao;
import com.culinometry.model.Ingredient;
import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeInstruction;

@Database(entities = {Ingredient.class, Recipe.class, RecipeIngredient.class, RecipeInstruction.class}, version = 1)
public abstract class CulinometryDatabase extends RoomDatabase {
    public abstract IngredientDao ingredientDao();

    public abstract RecipeDao recipeDao();

    public abstract RecipeIngredientDao recipeIngredientDao();

    public abstract RecipeInstructionDao recipeInstructionDao();
}