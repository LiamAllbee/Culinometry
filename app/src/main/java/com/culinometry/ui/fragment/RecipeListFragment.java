package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.databinding.FragmentRecipeListBinding;
import com.culinometry.ui.adapter.RecipeAdapter;
import com.culinometry.viewmodel.RecipeListViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RecipeListFragment extends Fragment {
    private RecipeAdapter adapter;
    private FragmentRecipeListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        RecipeListViewModel viewModel = new ViewModelProvider(this).get(RecipeListViewModel.class);

        binding.addRecipeButton.setOnClickListener(v -> {
            NavDirections action = RecipeListFragmentDirections.actionToRecipeEditorFragment();

            NavHostFragment.findNavController(this).navigate(action);
        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.setSearchQuery(editable.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        });

        RecipeAdapter.OnRecipeClickListener listener = recipe -> {
            // This is the implementation of the onRecipeClick() using safe args. The Lambda
            // simplifies the override into a shorthand. This isn't actually executed here but
            // is just defining the behavior then handing it to the adapter to use.
            NavDirections action = RecipeListFragmentDirections.actionToRecipeDetailFragment(recipe.getRecipeId());
            NavHostFragment.findNavController(this).navigate(action);
        };

        adapter = new RecipeAdapter(listener);
        binding.recipeRecyclerView.setAdapter(adapter);

        viewModel.getFilteredRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
        });
    }

    @Override
    public void onDestroyView() {
        binding.recipeRecyclerView.setAdapter(null);
        adapter = null;

        super.onDestroyView();
        binding = null;
    }
}
