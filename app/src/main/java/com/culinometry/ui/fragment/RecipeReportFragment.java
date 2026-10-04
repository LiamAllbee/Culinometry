package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.databinding.FragmentRecipeReportBinding;
import com.culinometry.ui.adapter.RecipeReportAdapter;
import com.culinometry.viewmodel.RecipeReportViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecipeReportFragment extends Fragment {
    private FragmentRecipeReportBinding binding;
    private RecipeReportViewModel viewModel;
    private RecipeReportAdapter recipeReportAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRecipeReportBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RecipeReportViewModel.class);


        binding.recipeReportRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeReportAdapter = new RecipeReportAdapter();
        binding.recipeReportRecyclerView.setAdapter(recipeReportAdapter);

        viewModel.getReportRows().observe(
                getViewLifecycleOwner(), rows -> {
                    recipeReportAdapter.submitList(rows);

                    //"Generated: Oct 4, 2026 10:34 AM"
                    long currentTime = System.currentTimeMillis();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());

                    String formattedTime = dateFormat.format(new Date(currentTime));

                    String generatedTime = "Generated: " + formattedTime;
                    binding.dateTimeTextView.setText(generatedTime);

                    String totalRecipes = "Total Recipes: " + rows.size();
                    binding.totalRecipesTextView.setText(totalRecipes);
                }
        );
    }

    @Override
    public void onDestroyView() {
        binding.recipeReportRecyclerView.setAdapter(null);
        recipeReportAdapter = null;

        super.onDestroyView();
        binding = null;
    }
}
