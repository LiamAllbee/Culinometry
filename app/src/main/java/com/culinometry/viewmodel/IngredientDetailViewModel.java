package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.model.Ingredient;
import com.culinometry.repo.CulinometryRepository;

import java.util.List;

public class IngredientDetailViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    public IngredientDetailViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public LiveData<Ingredient> getIngredient(long ingredientId) {
        return repo.getIngredient(ingredientId);
    }

    public void setSoftDeleted(long ingredientId, boolean softDeleted) {
        repo.setSoftDeleted(ingredientId, softDeleted);
    }

    public void addIngredient(Ingredient ingredient) {
        repo.addIngredient(ingredient);
    }

    public void updateIngredient(Ingredient ingredient) {
        repo.updateIngredient(ingredient);
    }
}
