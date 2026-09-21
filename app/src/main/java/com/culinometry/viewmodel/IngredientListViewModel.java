package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.model.Ingredient;
import com.culinometry.repo.CulinometryRepository;

import java.util.List;

public class IngredientListViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    public IngredientListViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return repo.getAllIngredients();
    }
}
