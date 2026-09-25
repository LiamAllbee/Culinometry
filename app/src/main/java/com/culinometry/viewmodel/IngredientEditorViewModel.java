package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.repo.CulinometryRepository;

public class IngredientEditorViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;
    private long ingredientId = -1L;

    // Is the current state of an existing ingredient in the database. Only necessary if the
    // ingredient selected exists
    private LiveData<Ingredient> databaseIngredient;

    // Is the ingredient that the user edits. Initialized in
    // If it is a new ingredient initialized with default values.
    // If an existing ingredient initialized with the databaseIngredient data.
    private Ingredient ingredientDraft;

    private boolean initialized = false;

    public IngredientEditorViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public void initializeIngredient(long ingredientId) {
        // Forces initialization to only run once
        if (initialized) {
            return;
        }

        initialized = true;
        this.ingredientId = ingredientId;

        // If it is an existing ingredient initialize the databaseIngredient with existing data
        if (ingredientId != -1L) {
            databaseIngredient = repo.getIngredient(ingredientId);
        }
        else {
            // If it is a new ingredient initialize the ingredientDraft with default values
            ingredientDraft = new Ingredient(
                    "",
                    null,
                    null,
                    null,
                    null,
                    IngredientMode.MASS_ONLY,
                    false
            );
        }
    }

    public void syncIngredientDraft(Ingredient ingredient) {
        // Sync should only happen once. If ingredientDraft has already been synced return
        if (ingredientDraft != null) {
            return;
        }

        // It is pulling data from the observed ingredient which is also the databaseIngredient
        ingredientDraft = new Ingredient(
                ingredient.getName(),
                ingredient.getReferenceMass(),
                ingredient.getMassUnit(),
                ingredient.getReferenceVolume(),
                ingredient.getVolumeUnit(),
                ingredient.getMode(),
                ingredient.isSoftDeleted()
        );

        ingredientDraft.setIngredientId(ingredient.getIngredientId());
    }

    public boolean isNewIngredient() {
        return ingredientId == -1L;
    }

    public LiveData<Ingredient> getDatabaseIngredient() {
        return databaseIngredient;
    }

    public Ingredient getIngredientDraft() {
        return ingredientDraft;
    }

    public void saveIngredient() {
        if (ingredientDraft == null) {
            return;
        }
        if(isNewIngredient()) {
            repo.addIngredient(ingredientDraft);
        }
        else {
            repo.updateIngredient(ingredientDraft);
        }
    }

    public void setDraftName(String name) {
        ingredientDraft.setName(name);
    }

    public void setDraftReferenceMass(String referenceMass) {
        ingredientDraft.setReferenceMass(referenceMass);
    }

    public void setDraftMassUnit(Unit unit) {
        ingredientDraft.setMassUnit(unit);
    }

    public void setDraftReferenceVolume(String referenceVolume) {
        ingredientDraft.setReferenceVolume(referenceVolume);
    }

    public void setDraftVolumeUnit(Unit unit) {
        ingredientDraft.setVolumeUnit(unit);
    }

    public void setDraftMode(IngredientMode mode) {
        ingredientDraft.setMode(mode);
    }

    public void setSoftDeleted(long ingredientId, boolean softDeleted) {
        repo.setSoftDeleted(ingredientId, softDeleted);
    }
}
