package com.culinometry.repo;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.culinometry.dao.IngredientDao;
import com.culinometry.dao.RecipeDao;
import com.culinometry.dao.RecipeIngredientDao;
import com.culinometry.dao.RecipeInstructionDao;
import com.culinometry.database.CulinometryDatabase;
import com.culinometry.model.Ingredient;
import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientWithIngredient;
import com.culinometry.model.RecipeInstruction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CulinometryRepository {
    private static CulinometryRepository culinometryRepo;

    private final IngredientDao ingredientDao;
    private final RecipeDao recipeDao;
    private final RecipeIngredientDao recipeIngredientDao;
    private final RecipeInstructionDao recipeInstructionDao;
    private final CulinometryDatabase db;

    // We only need one background thread since we are not also doing things like network requests,
    // image processing, and independant file processing at the same time. This makes processing
    // simpler since writes can now only happen sequentially on the background thread. Though we
    // still have to make sure to synchronize ids on dependant objects regardless because data can
    // still be hidden if written separately from each other.
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    public static synchronized CulinometryRepository getInstance(Context context) {
        if (culinometryRepo == null) {
            culinometryRepo = new CulinometryRepository(context);
        }

        return culinometryRepo;
    }

    private CulinometryRepository(Context context) {
        db = Room.databaseBuilder(
                context.getApplicationContext(),
                CulinometryDatabase.class,
                "culinometry.db")
                .build();

        ingredientDao = db.ingredientDao();
        recipeDao = db.recipeDao();
        recipeIngredientDao = db.recipeIngredientDao();
        recipeInstructionDao = db.recipeInstructionDao();
    }

    //---------------
    // Beginning of IngredientDao
    //---------------
    public LiveData<Ingredient> getIngredient(long ingredientId) {
        return ingredientDao.getIngredient(ingredientId);
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return ingredientDao.getAllIngredients();
    }

    public LiveData<List<Ingredient>> getAllSoftDeletedIngredients() {
        return ingredientDao.getAllSoftDeletedIngredients();
    }

    // PRIVATE BECAUSE THIS SHOULD NEVER BE CALLED IN ISOLATION IN THE VIEW MODEL
    // IT IS A MAINTENANCE OPERATION SHOULD ALWAYS BE DONE WITH OTHER OPERATIONS
    private void cleanupUnusedIngredients() {
        ingredientDao.deleteUnusedSoftDeletedIngredients();
    }

    public void setSoftDeleted(long ingredientId, boolean softDeleted) {
        dbExecutor.execute(() -> {
            ingredientDao.setSoftDeleted(ingredientId, softDeleted);

            if(softDeleted) {
                cleanupUnusedIngredients();
            }
        });
    }

    public void addIngredient(Ingredient ingredient) {
        dbExecutor.execute(() -> {
            long ingredientId = ingredientDao.insert(ingredient);
            ingredient.setIngredientId(ingredientId);
        });
    }

    public void updateIngredient(Ingredient ingredient) {
        dbExecutor.execute(() -> {
            ingredientDao.update(ingredient);
        });
    }

    //---------------
    // Beginning of RecipeDao
    //---------------

    public LiveData<Recipe> getRecipe(long recipeId) {
        return recipeDao.getRecipe(recipeId);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipeDao.getAllRecipes();
    }

    public void saveNewRecipe(Recipe recipe,
                              List<RecipeIngredient> recipeIngredientList,
                              List<RecipeInstruction> recipeInstructionList) {
        dbExecutor.execute(() -> {

            // Transaction prevents partial additions to the DB if any point fails the entire
            // operation reverts
            db.runInTransaction(() -> {
                long currentTime = System.currentTimeMillis();

                recipe.setCreatedAt(currentTime);
                recipe.setUpdatedAt(currentTime);

                long recipeId = recipeDao.insert(recipe);
                //Syncs the in memory recipe object with the database
                recipe.setRecipeId(recipeId);

                for(RecipeIngredient recipeIngredient : recipeIngredientList) {
                    recipeIngredient.setRecipeId(recipeId);
                }

                for(RecipeInstruction recipeInstruction : recipeInstructionList) {
                    recipeInstruction.setRecipeId(recipeId);
                }

                if (!recipeIngredientList.isEmpty()) {
                    List<Long> recipeIngredientIds = recipeIngredientDao.addAllRecipeIngredients(recipeIngredientList);

                    // Makes the recipeIngredientList in memory have up-to-date IDs
                    for (int i = 0; i < recipeIngredientList.size(); i++) {
                        recipeIngredientList.get(i).setRecipeIngredientId(recipeIngredientIds.get(i));
                    }
                }

                if (!recipeInstructionList.isEmpty()) {
                    List<Long> recipeInstructionIds = recipeInstructionDao.addAllRecipeInstructions(recipeInstructionList);

                    // Makes the recipeInstructionList in memory have up-to-date IDs
                    for (int i = 0; i < recipeInstructionList.size(); i++) {
                        recipeInstructionList.get(i).setRecipeInstructionId(recipeInstructionIds.get(i));
                    }
                }
            });
        });
    }

    public void updateRecipe(Recipe recipe,
                             List<RecipeIngredient> recipeIngredientList,
                             List<RecipeInstruction> recipeInstructionList,
                             Runnable onComplete) {
        dbExecutor.execute(() -> {

            // Because we are not checking for diffs the easiest way to update is to just remove
            // and replace everything which will account for creations, deletions, and updates in
            // one swoop.
            db.runInTransaction(() -> {
                recipe.setUpdatedAt(System.currentTimeMillis());
                recipeDao.update(recipe);

                long recipeId = recipe.getRecipeId();

                for (RecipeIngredient recipeIngredient : recipeIngredientList) {
                    recipeIngredient.setRecipeId(recipeId);
                }

                for (RecipeInstruction recipeInstruction : recipeInstructionList) {
                    recipeInstruction.setRecipeId(recipeId);
                }

                recipeIngredientDao.removeAllIngredientsForRecipe(recipeId);

                if (!recipeIngredientList.isEmpty()) {
                    List<Long> recipeIngredientIds = recipeIngredientDao.addAllRecipeIngredients(recipeIngredientList);

                    // Makes the recipeIngredientList in memory have up-to-date IDs
                    for (int i = 0; i < recipeIngredientList.size(); i++) {
                        recipeIngredientList.get(i).setRecipeIngredientId(recipeIngredientIds.get(i));
                    }
                }

                recipeInstructionDao.removeAllInstructionsForRecipe(recipeId);

                if (!recipeInstructionList.isEmpty()) {
                    List<Long> recipeInstructionIds = recipeInstructionDao.addAllRecipeInstructions(recipeInstructionList);

                    // Makes the recipeInstructionList in memory have up-to-date IDs
                    for (int i = 0; i < recipeInstructionList.size(); i++) {
                        recipeInstructionList.get(i).setRecipeInstructionId(recipeInstructionIds.get(i));
                    }
                }

                cleanupUnusedIngredients();
            });

            onComplete.run();
        });
    }

    // Cascade handles the delete behavior so we don't need to also delete from RecipeIngredient
    // and RecipeInstruction
    public void deleteRecipe(Recipe recipe) {
        dbExecutor.execute(() -> {

            db.runInTransaction(() -> {
                recipeDao.delete(recipe);
                cleanupUnusedIngredients();
            });
        });
    }

    //---------------
    // Beginning of RecipeIngredientDao
    //---------------

    public LiveData<List<RecipeIngredientWithIngredient>> getAllIngredientsForRecipe(long recipeId) {
        return recipeIngredientDao.getAllIngredientsForRecipe(recipeId);
    }

    //---------------
    // Beginning of RecipeInstructionDao
    //---------------

    public LiveData<List<RecipeInstruction>> getAllRecipeInstructionsForRecipe(long recipeId) {
        return recipeInstructionDao.getAllRecipeInstructionsForRecipe(recipeId);
    }
}
