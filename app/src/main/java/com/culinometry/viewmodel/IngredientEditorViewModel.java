package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.repo.CulinometryRepository;
import com.culinometry.validation.IngredientValidationState;
import com.culinometry.validation.IngredientValidator;

import java.util.ArrayList;
import java.util.List;

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

    // The MediatorLiveData wrapper that when initialized with a validationState.setValue() gets
    // observed. Mediator because we are reliant on two live data the list of ingredients and the
    // validation state
    private final MediatorLiveData<IngredientValidationState> validationState = new MediatorLiveData<>();

    private List<Ingredient> existingIngredients = new ArrayList<>();

    public IngredientEditorViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);

        // When viewModel is created in fragment sync creation with list of ingredients being
        // gathered. Also, if database ever changes sync existingIngredients and then
        // validateIngredients again
        validationState.addSource(repo.getAllIngredients(), ingredients -> {
            if (ingredients != null) {
                existingIngredients = ingredients;
            }
            if (ingredientDraft != null) {
                validateIngredient();
            }
        });
    }

    public void initializeIngredient(long ingredientId) {
        // Forces initialization to only run once
        if (initialized) {
            return;
        }

        initialized = true;
        this.ingredientId = ingredientId;

        // If it is an existing ingredient initialize the databaseIngredient with existing data at
        // the time of ingredient create or edit being accessed. Technically not fully up to date
        // if the DB side list changes mid-access but that should never happen.
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
        validateIngredient();
    }

    public void setDraftReferenceMass(String referenceMass) {
        ingredientDraft.setReferenceMass(referenceMass);
        validateIngredient();
    }

    public void setDraftMassUnit(Unit unit) {
        ingredientDraft.setMassUnit(unit);
        validateIngredient();
    }

    public void setDraftReferenceVolume(String referenceVolume) {
        ingredientDraft.setReferenceVolume(referenceVolume);
        validateIngredient();
    }

    public void setDraftVolumeUnit(Unit unit) {
        ingredientDraft.setVolumeUnit(unit);
        validateIngredient();
    }

    public void setDraftMode(IngredientMode mode) {
        ingredientDraft.setMode(mode);
        validateIngredient();
    }

    public void setSoftDeleted(boolean softDeleted) {
        repo.setSoftDeleted(ingredientDraft.getIngredientId(), softDeleted);
    }

    public LiveData<IngredientValidationState> getValidationState() {
        return validationState;
    }

    private void validateIngredient() {
        IngredientValidationState newState = IngredientValidator.validate(ingredientDraft, existingIngredients);

        validationState.setValue(newState);
    }
}
