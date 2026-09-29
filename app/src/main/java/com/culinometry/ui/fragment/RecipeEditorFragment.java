package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.culinometry.databinding.FragmentIngredientListBinding;
import com.culinometry.databinding.FragmentRecipeEditorBinding;
import com.culinometry.model.RecipeInstructionDraft;
import com.culinometry.ui.adapter.IngredientAdapter;
import com.culinometry.ui.adapter.RecipeInstructionAdapter;
import com.culinometry.viewmodel.IngredientListViewModel;
import com.culinometry.viewmodel.RecipeEditorViewModel;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RecipeEditorFragment extends Fragment {
    private RecipeInstructionAdapter instructionAdapter;
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

        binding.recipeInstructionRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recipeInstructionRecyclerView.setItemAnimator(null);

        viewModel = new ViewModelProvider(this).get(RecipeEditorViewModel.class);

        RecipeEditorFragmentArgs args = RecipeEditorFragmentArgs.fromBundle(requireArguments());

        long recipeId = args.getRecipeId();

        // Want the observer ready to observe before initialization
        setupInstructionRecyclerView();
        observeEditorDraft();

        // Stores the current recipeId into the view models memory
        viewModel.initializeRecipe(recipeId);

        setupAddInstructionButton();
    }

    @Override
    public void onDestroyView() {
        binding.recipeInstructionRecyclerView.setAdapter(null);
        instructionAdapter = null;

        super.onDestroyView();
        binding = null;
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
        });
    }

    private void setupAddInstructionButton() {
        binding.addInstructionButton.setOnClickListener(
                view -> viewModel.addInstruction()
        );
    }
}
