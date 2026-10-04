package com.culinometry.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.culinometry.model.RecipeReportRow;
import com.culinometry.repo.CulinometryRepository;

import java.util.List;

public class RecipeReportViewModel extends AndroidViewModel {
    private final LiveData<List<RecipeReportRow>> reportRows;

    public RecipeReportViewModel(Application application) {
        super(application);

        CulinometryRepository repo = CulinometryRepository.getInstance(application);

        reportRows = repo.getRecipeReport();
    }

    public LiveData<List<RecipeReportRow>> getReportRows() {
        return reportRows;
    }
}
