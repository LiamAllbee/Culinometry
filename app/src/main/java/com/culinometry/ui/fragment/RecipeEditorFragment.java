package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.databinding.FragmentRecipeEditorBinding;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.model.RecipeDraft;
import com.culinometry.ui.adapter.RecipeIngredientAdapter;
import com.culinometry.ui.adapter.RecipeInstructionAdapter;
import com.culinometry.viewmodel.RecipeEditorViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RecipeEditorFragment extends Fragment {
    private RecipeInstructionAdapter instructionAdapter;
    private RecipeIngredientAdapter ingredientAdapter;
    private RecipeEditorViewModel viewModel;
    private FragmentRecipeEditorBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recipeIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recipeIngredientRecyclerView.setItemAnimator(null);

        binding.recipeInstructionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recipeInstructionRecyclerView.setItemAnimator(null);

        viewModel = new ViewModelProvider(this).get(RecipeEditorViewModel.class);

        RecipeEditorFragmentArgs args = RecipeEditorFragmentArgs.fromBundle(requireArguments());

        long recipeId = args.getRecipeId();

        // Want the observer ready to observe before initialization
        setupIngredientRecyclerView();
        setupInstructionRecyclerView();

        setupRecipeInputListeners();

        observeEditorDraft();
        observeIngredientCatalog();
        observeValidationState();

        // Stores the current recipeId into the view models memory
        viewModel.initializeRecipe(recipeId);

        setupAddInstructionButton();
        setupAddIngredientButton();
        saveButtonPressed();
    }

    @Override
    public void onDestroyView() {
        binding.recipeInstructionRecyclerView.setAdapter(null);
        instructionAdapter = null;

        binding.recipeIngredientRecyclerView.setAdapter(null);
        ingredientAdapter = null;

        super.onDestroyView();
        binding = null;
    }

    private void setupRecipeInputListeners() {
        // Recipe name input listener
        binding.nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setRecipeName(charSequence.toString());
            }
        });

        // Recipe name input listener
        binding.descriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setRecipeDescription(charSequence.toString());
            }
        });
    }
    private void setupIngredientRecyclerView() {
        ingredientAdapter = new RecipeIngredientAdapter(new RecipeIngredientAdapter.OnRecipeIngredientActionListener() {
            @Override
            public void onIngredientInputChanged(long draftId, String ingredientInput) {
                viewModel.updateIngredientInput(draftId, ingredientInput);
            }

            @Override
            public void onIngredientSelected(long draftId, Ingredient ingredient) {
                viewModel.updateIngredientSelection(draftId, ingredient);
            }

            @Override
            public void onQuantityChanged(long draftId, String quantity) {
                viewModel.updateQuantity(draftId, quantity);
            }

            @Override
            public void onUnitChanged(long draftId, Unit unit) {
                viewModel.updateUnit(draftId, unit);
            }

            @Override
            public void onCheckBoxChanged(long draftId, boolean unitLocked) {
                viewModel.updateUnitLocked(draftId, unitLocked);
            }

            @Override
            public void onDeleteRecipeIngredient(long draftId) {
                viewModel.deleteRecipeIngredient(draftId);
            }
        });

        binding.recipeIngredientRecyclerView.setAdapter(ingredientAdapter);
    }
    private void setupInstructionRecyclerView() {
        instructionAdapter = new RecipeInstructionAdapter(new RecipeInstructionAdapter.OnInstructionActionListener() {
            @Override
            public void onInstructionChanged(long draftId, String instruction) {
                viewModel.updateInstruction(draftId, instruction);
            }

            @Override
            public void onDeleteInstruction(long draftId) {
                viewModel.deleteInstruction(draftId);
            }
        });

        binding.recipeInstructionRecyclerView.setAdapter(instructionAdapter);
    }

    private void observeEditorDraft() {
        viewModel.getEditorDraft().observe(getViewLifecycleOwner(), draft -> {
            instructionAdapter.submitList(draft.getRecipeInstructionDrafts());
            ingredientAdapter.submitList(draft.getRecipeIngredientDrafts());

            RecipeDraft draftRecipe = draft.getRecipeDraft();
            // Set Recipe Name with drafts data on the name if user isn't actively updating
            if (!binding.nameEditText.hasFocus()) {
                String displayed = binding.nameEditText.getText().toString();

                if (!displayed.equals(draftRecipe.getName())) {
                    binding.nameEditText.setText(draftRecipe.getName());
                }
            }

            // Set Recipe description with drafts data on the name if user isn't actively updating
            if (!binding.descriptionEditText.hasFocus()) {
                String displayed = binding.descriptionEditText.getText().toString();

                if (!displayed.equals(draftRecipe.getDescription())) {
                    binding.descriptionEditText.setText(draftRecipe.getDescription());
                }
            }
        });
    }

    private void observeIngredientCatalog() {
        viewModel.getAllIngredients().observe(
                getViewLifecycleOwner(), ingredients -> {
                    if (ingredients != null) {
                        ingredientAdapter.setIngredientCatalog(ingredients);
                    }
                }
        );
    }

    private void observeValidationState() {
        viewModel.getValidationState().observe(
                getViewLifecycleOwner(), state -> {
                    // Explicitly don't allow the save button until isAllValid proves otherwise.
                    if (state == null) {
                        binding.saveRecipeButton.setEnabled(false);
                        return;
                    }

                    binding.nameInputLayout.setError(state.getNameError());

                    ingredientAdapter.setValidationErrors(state.getRecipeIngredientErrors());
                    instructionAdapter.setValidationErrors(state.getRecipeInstructionErrors());

                    binding.saveRecipeButton.setEnabled(state.isAllValid());

                }
        );
    }
    private void setupAddInstructionButton() {
        binding.addInstructionButton.setOnClickListener(
                view -> viewModel.addInstruction()
        );
    }

    private void setupAddIngredientButton() {
        binding.addIngredientButton.setOnClickListener(
                view -> viewModel.addIngredient()
        );
    }

    private void saveButtonPressed() {
        binding.saveRecipeButton.setOnClickListener(view -> {
            viewModel.saveRecipe();
            Navigation.findNavController(view).popBackStack();
        });
    }
}
