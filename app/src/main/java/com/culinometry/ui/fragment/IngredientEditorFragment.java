package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.culinometry.databinding.FragmentIngredientEditorBinding;
import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.viewmodel.IngredientEditorViewModel;

public class IngredientEditorFragment extends Fragment {
    private FragmentIngredientEditorBinding binding;
    private IngredientEditorViewModel viewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentIngredientEditorBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(IngredientEditorViewModel.class);

        IngredientEditorFragmentArgs args = IngredientEditorFragmentArgs.fromBundle(requireArguments());

        long ingredientId = args.getIngredientId();

        // Stores the current ingredient into the view models memory
        viewModel.initializeIngredient(ingredientId);

        // If it is an existing ingredient the fields get populated
        observeIngredient();
        //// TO-DO Listeners to listen for updates on the page for input validation
        //// Save button to save the finished ingredient
//        setupInputListeners();
        saveButtonPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void observeIngredient() {
        if (viewModel.isNewIngredient()) {
            return;
        }

        viewModel.getDatabaseIngredient().observe(getViewLifecycleOwner(), ingredient -> {
            // if this is called before ingredient live data is loaded it returns and then
            // tries again later
            if (ingredient == null) {
                return;
            }

            // Sync the draft with databaseIngredient if this is called twice it returns can only
            // happen once per session. This is important so populate fields always updates with
            // user edited data any time this fragment is rebuilt.
            viewModel.syncIngredientDraft(ingredient);

            populateUIFields(viewModel.getIngredientDraft());
        });
    }

    private void updateModeUI(IngredientMode mode) {
        boolean isConversionEnabled = (mode == IngredientMode.MASS_AND_VOLUME);

        // If enabled it's visible if not it's invisible
        binding.conversionCard.setVisibility(isConversionEnabled ? View.VISIBLE : View.GONE);
    }

    private void populateUIFields(Ingredient ingredient) {
        // Leave everything blank if the ingredient is new so that fields don't get populated with
        // null
        if (viewModel.isNewIngredient()) {
            return;
        }

        binding.nameEditText.setText(ingredient.getName());

        IngredientMode mode = ingredient.getMode();

        switch (mode) {
            case MASS_ONLY:
                binding.massButton.setChecked(true);
                break;

            case VOLUME_ONLY:
                binding.volumeButton.setChecked(true);
                break;

            case MASS_AND_VOLUME:
                // Populate text fields before making the UI visible
                binding.massAndVolumeButton.setChecked(true);

                //// TO-DO Re-evaluate the if statements here if they are necessary depends on implementation of the input validation
                if (ingredient.getReferenceMass() != null && ingredient.getMassUnit() != null) {
                    binding.massEditText.setText(String.valueOf(ingredient.getReferenceMass()));
                    binding.referenceMassUnitDropdown.setText(String.valueOf(ingredient.getMassUnit()));
                }
                if (ingredient.getReferenceVolume() != null && ingredient.getVolumeUnit() != null) {
                    binding.volumeEditText.setText(String.valueOf(ingredient.getReferenceVolume()));
                    binding.referenceVolumeUnitDropdown.setText(String.valueOf(ingredient.getVolumeUnit()));
                }
                break;
        }

        updateModeUI(mode);
    }

    private void saveButtonPressed() {
        binding.saveIngredientButton.setOnClickListener(view -> {

            String name = binding.nameEditText.getText().toString();

            String referenceMass = null;
            Unit massUnit = null;
            String referenceVolume = null;
            Unit volumeUnit = null;
            IngredientMode mode = IngredientMode.MASS_ONLY;

            if (binding.volumeButton.isChecked()) {
                mode = IngredientMode.VOLUME_ONLY;
            }
            else if (binding.massAndVolumeButton.isChecked()) {
                mode = IngredientMode.MASS_AND_VOLUME;
                referenceMass = binding.massEditText.getText().toString();
                //// massUnit data

                referenceVolume = binding.volumeEditText.getText().toString();
                //// volumeUnit data
            }

            viewModel.setDraftName(name);
            viewModel.setDraftMass(referenceMass);
            viewModel.setDraftMassUnit(massUnit);
            viewModel.setDraftVolume(referenceVolume);
            viewModel.setDraftVolumeUnit(volumeUnit);
            viewModel.setDraftMode(mode);

            viewModel.saveIngredient();
        });
    }
}
