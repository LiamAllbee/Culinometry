package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.culinometry.measurement.MassVolumeConverter;
import com.culinometry.measurement.MeasurementType;
import com.culinometry.measurement.QuantityParser;
import com.culinometry.measurement.Unit;
import com.culinometry.measurement.UnitConverter;
import com.culinometry.measurement.formatters.FormattedMeasurement;
import com.culinometry.measurement.formatters.MassFormatter;
import com.culinometry.measurement.formatters.MeasurementFormatter;
import com.culinometry.measurement.formatters.VolumeFormatter;
import com.culinometry.model.Ingredient;
import com.culinometry.model.Recipe;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientDisplay;
import com.culinometry.model.RecipeIngredientWithIngredient;
import com.culinometry.model.RecipeInstruction;
import com.culinometry.repo.CulinometryRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    private boolean initialized = false;
    private long recipeId = -1L;
    private LiveData<Recipe> recipe;
    private LiveData<List<RecipeIngredientWithIngredient>> recipeIngredientWithIngredientList;
    private LiveData<List<RecipeInstruction>> recipeInstructionList;
    private List<RecipeIngredientWithIngredient> originalIngredients = new ArrayList<>();
    private final Map<Long, Unit> selectedUnits = new HashMap<>();
    private final MediatorLiveData<List<RecipeIngredientDisplay>> displayedIngredients = new MediatorLiveData<>();

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

        displayedIngredients.addSource(
                recipeIngredientWithIngredientList, ingredients -> {

            originalIngredients = ingredients == null
                    ? new ArrayList<>() : new ArrayList<>(ingredients);

            for (RecipeIngredientWithIngredient item : originalIngredients) {
                RecipeIngredient recipeIngredient = item.getRecipeIngredient();

                selectedUnits.putIfAbsent(recipeIngredient.getRecipeIngredientId(), recipeIngredient.getUnit());

            }

            rebuildDisplayedIngredients();
        });
    }

    private List<Unit> getAvailableUnits(RecipeIngredient recipeIngredient, Ingredient ingredient) {
        List<Unit> units = new ArrayList<>();

        if (recipeIngredient.isUnitLocked()) {
            units.add(recipeIngredient.getUnit());
            return units;
        }
        else {
            for (Unit unit : Unit.values()) {
                switch (ingredient.getMode()) {
                    case MASS_ONLY:
                        if (unit.getType() == MeasurementType.MASS) {
                            units.add(unit);
                        }
                        break;

                    case VOLUME_ONLY:
                        if (unit.getType() == MeasurementType.VOLUME) {
                            units.add(unit);
                        }
                        break;

                    case MASS_AND_VOLUME:
                        units.add(unit);
                        break;
                }
            }
            return units;
        }
    }

    private RecipeIngredientDisplay buildDIsplayItem(RecipeIngredientWithIngredient item) {
        RecipeIngredient recipeIngredient = item.getRecipeIngredient();
        Ingredient ingredient = item.getIngredient();

        long id = recipeIngredient.getIngredientId();

        Unit originalUnit = recipeIngredient.getUnit();
        Unit selectedUnit = selectedUnits.getOrDefault(id, originalUnit);

        String displayedQuantity;

        // Preserve exactly what the user typed
        if (selectedUnit == originalUnit) {
            displayedQuantity = recipeIngredient.getQuantity();
        }
        else {
            double originalQuantity = QuantityParser.parse(recipeIngredient.getQuantity());

            double convertedQuantity;

            if (originalUnit.getType() == selectedUnit.getType()) {
                convertedQuantity = UnitConverter.convert(originalQuantity, originalUnit, selectedUnit);
            }
            else {
                convertedQuantity = MassVolumeConverter.convert(originalQuantity, originalUnit, selectedUnit, ingredient);
            }

            MeasurementFormatter formatter;

            if (selectedUnit.getType() == MeasurementType.MASS) {
                formatter = new MassFormatter();
            }
            else {
                formatter = new VolumeFormatter();
            }

            FormattedMeasurement formatted = formatter.format(convertedQuantity, selectedUnit);

            displayedQuantity = formatted.getParts().get(0).getQuantity();
        }

        List<Unit> availableUnits = getAvailableUnits(recipeIngredient, ingredient);

        return new RecipeIngredientDisplay(
                id,
                recipeIngredient.getSortOrder(),
                ingredient.getName(),
                displayedQuantity,
                selectedUnit,
                availableUnits,
                !recipeIngredient.isUnitLocked());

    }

    private void rebuildDisplayedIngredients() {
        List<RecipeIngredientDisplay> displayList = new ArrayList<>();

        for (RecipeIngredientWithIngredient item : originalIngredients) {
            displayList.add(buildDIsplayItem(item));
        }

        displayedIngredients.setValue(displayList);
    }

    public LiveData<List<RecipeIngredientDisplay>> getDisplayedIngredients() {
        return displayedIngredients;
    }

    public void setDisplayedUnit(long recipeIngredientId, Unit unit) {
        selectedUnits.put(recipeIngredientId, unit);

        rebuildDisplayedIngredients();
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
