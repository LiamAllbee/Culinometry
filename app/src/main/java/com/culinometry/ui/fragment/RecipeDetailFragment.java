package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.R;
import com.culinometry.databinding.FragmentRecipeDetailBinding;
import com.culinometry.ui.adapter.RecipeIngredientDetailAdapter;
import com.culinometry.ui.adapter.InstructionDetailAdapter;
import com.culinometry.viewmodel.RecipeDetailViewModel;

public class RecipeDetailFragment extends Fragment {
    private FragmentRecipeDetailBinding binding;
    private RecipeDetailViewModel viewModel;
    private RecipeIngredientDetailAdapter recipeIngredientAdapter;
    private InstructionDetailAdapter recipeInstructionAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recipeIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recipeIngredientRecyclerView.setItemAnimator(null);

        binding.recipeInstructionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recipeInstructionRecyclerView.setItemAnimator(null);

        viewModel = new ViewModelProvider(this).get(RecipeDetailViewModel.class);

        RecipeDetailFragmentArgs args = RecipeDetailFragmentArgs.fromBundle(requireArguments());

        long recipeId = args.getRecipeId();

        // Stores the current ingredient into the view models memory
        viewModel.initializeRecipe(recipeId);

        observeRecipe();
        observeRecipeIngredientWithIngredient();
        observeRecipeInstruction();
//        setupUnitDropdowns();
//        setupInputListeners();

        setupAppBarMenu(recipeId);
//        saveButtonPressed();
    }

    @Override
    public void onDestroyView() {
        binding.recipeInstructionRecyclerView.setAdapter(null);
        recipeInstructionAdapter = null;

        binding.recipeIngredientRecyclerView.setAdapter(null);
        recipeIngredientAdapter = null;

        super.onDestroyView();
        binding = null;
    }

    private void setupAppBarMenu(long recipeId) {
        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.appbar_recipe_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    viewModel.deleteRecipe();

                    Navigation.findNavController(requireView()).popBackStack();

                    return true;
                }

                if (menuItem.getItemId() == R.id.action_edit) {
                    RecipeDetailFragmentDirections.ActionToRecipeEditorFragment action = RecipeDetailFragmentDirections.actionToRecipeEditorFragment();

                    action.setRecipeId(recipeId);
                    NavHostFragment.findNavController(requireParentFragment()).navigate(action);

                    return true;
                }
                return false;
            }

        },
                getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void observeRecipe() {
        viewModel.getRecipe().observe(getViewLifecycleOwner(), recipe -> {
            // if this is called before recipe live data is loaded it returns and then
            // tries again later
            if (recipe != null) {
                binding.nameEditText.setText(recipe.getName());
                binding.descriptionEditText.setText(recipe.getDescription());
            }
        });
    }

    private void observeRecipeIngredientWithIngredient() {
        viewModel.getRecipeIngredientWithIngredientList().observe(getViewLifecycleOwner(),
                recipeIngredientWithIngredients -> {
            if (recipeIngredientWithIngredients != null) {
                recipeIngredientAdapter = new RecipeIngredientDetailAdapter(recipeIngredientWithIngredients);
                binding.recipeIngredientRecyclerView.setAdapter(recipeIngredientAdapter);
            }
        });
    }

    private void observeRecipeInstruction() {
        viewModel.getRecipeInstructionList().observe(getViewLifecycleOwner(),
                recipeInstructions -> {
                    if (recipeInstructions != null) {
                        recipeInstructionAdapter = new InstructionDetailAdapter(recipeInstructions);
                        binding.recipeInstructionRecyclerView.setAdapter(recipeInstructionAdapter);
                    }
                });
    }
}