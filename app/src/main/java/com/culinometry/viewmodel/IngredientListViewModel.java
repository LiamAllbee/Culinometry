package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.culinometry.model.Ingredient;
import com.culinometry.repo.CulinometryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientListViewModel extends AndroidViewModel {
    private final CulinometryRepository repo;
    private List<Ingredient> allIngredients = new ArrayList<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<Ingredient>> filteredIngredients = new MediatorLiveData<>();

    public IngredientListViewModel(Application application) {
        super(application);
        repo = CulinometryRepository.getInstance(application);

        filteredIngredients.addSource(repo.getAllIngredients(), ingredients -> {
            if (ingredients == null) {
                allIngredients = new ArrayList<>();
            }
            else {
                allIngredients = new ArrayList<>(ingredients);
            }

            filterIngredients();
        });

        filteredIngredients.addSource(searchQuery, query -> {
            filterIngredients();
        });
    }

    // Because we have to publish the value to alert the observer we must call setValue()
    public void setSearchQuery(String query) {
        searchQuery.setValue(query == null ? "" : query);
    }

    public void filterIngredients() {
        String query = searchQuery.getValue();

        if (query == null || query.isBlank()) {
            filteredIngredients.setValue(new ArrayList<>(allIngredients));
        }
        else {
            String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
            List<Ingredient> filteredList = new ArrayList<>();

            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getName()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedQuery)) {
                    filteredList.add(ingredient);
                }
            }

            filteredIngredients.setValue(filteredList);
        }
    }
    public LiveData<List<Ingredient>> getFilteredIngredients() {
        return filteredIngredients;
    }
}
