package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.model.Recipe;
import com.culinometry.repo.CulinometryRepository;

import java.util.List;

public class RecipeListViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;

    public RecipeListViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return repo.getAllRecipes();
    }
}
