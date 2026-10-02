package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.culinometry.model.Recipe;
import com.culinometry.repo.CulinometryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeListViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;
    private List<Recipe> allRecipes = new ArrayList<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<Recipe>> filteredRecipes = new MediatorLiveData<>();
    public RecipeListViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);

        filteredRecipes.addSource(repo.getAllRecipes(), recipes -> {
            if (recipes == null) {
                allRecipes = new ArrayList<>();
            }
            else {
                allRecipes = new ArrayList<>(recipes);
            }

            filterRecipes();
        });

        filteredRecipes.addSource(searchQuery, query -> {
            filterRecipes();
        });
    }
    // Because we have to publish the value to alert the observer we must call setValue()
    public void setSearchQuery(String query) {
        searchQuery.setValue(query == null ? "" : query);
    }

    public void filterRecipes() {
        String query = searchQuery.getValue();

        if (query == null || query.isBlank()) {
            filteredRecipes.setValue(new ArrayList<>(allRecipes));
        }
        else {
            String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
            List<Recipe> filteredList = new ArrayList<>();

            for (Recipe recipe : allRecipes) {
                if (recipe.getName()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedQuery)) {
                    filteredList.add(recipe);
                }
            }

            filteredRecipes.setValue(filteredList);
        }
    }
    public LiveData<List<Recipe>> getFilteredRecipes() {
        return filteredRecipes;
    }
}
