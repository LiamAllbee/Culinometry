package com.culinometry.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.culinometry.R;
import com.culinometry.databinding.FragmentIngredientEditorBinding;
import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.MeasurementType;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.viewmodel.IngredientEditorViewModel;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

public class IngredientEditorFragment extends Fragment {
    private FragmentIngredientEditorBinding binding;
    private IngredientEditorViewModel viewModel;
    private final List<Unit> massUnits = new ArrayList<>();
    private final List<Unit> volumeUnits = new ArrayList<>();

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

        setupUnitDropdowns();
        setupInputListeners();

        if (viewModel.isNewIngredient()) {
            // Important preventing new ingredients from getting overwritten if fragment recreates itself
            populateUIFields(viewModel.getIngredientDraft());
        }
        else {
            // If it is an existing ingredient the fields get populated
            observeIngredient();
        }

        setupAppBarMenu();
        saveButtonPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupAppBarMenu() {
        MenuHost menuHost = requireActivity();

        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                if (!viewModel.isNewIngredient()) {
                    menuInflater.inflate(R.menu.appbar_ingredient_menu, menu);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    viewModel.setSoftDeleted(true);

                    Navigation.findNavController(requireView()).popBackStack();

                    return true;
                }
                return false;
            }

        },
                getViewLifecycleOwner(), Lifecycle.State.RESUMED);
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
                break;
        }

        if (ingredient.getReferenceMass() != null) {
            binding.massEditText.setText(String.valueOf(ingredient.getReferenceMass()));
        }
        if (ingredient.getMassUnit() != null) {
            binding.referenceMassUnitDropdown.setText(ingredient.getMassUnit().toString(), false);
        }
        if (ingredient.getReferenceVolume() != null) {
            binding.volumeEditText.setText(String.valueOf(ingredient.getReferenceVolume()));
        }
        if (ingredient.getVolumeUnit() != null) {
            binding.referenceVolumeUnitDropdown.setText(ingredient.getVolumeUnit().toString(), false);
        }

        updateModeUI(mode);
    }

    private void setupUnitDropdowns() {
        massUnits.clear();
        volumeUnits.clear();

        // Sorts and populates every unit in the Unit ENUM into their respective category
        for (Unit unit : Unit.values()) {
            if (unit.getType() == MeasurementType.MASS) {
                massUnits.add(unit);
            }
            else if (unit.getType() == MeasurementType.VOLUME) {
                volumeUnits.add(unit);
            }
        }

        // Create adapter that populates each 1line with a Unit from massUnits
        ArrayAdapter<Unit> massAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                massUnits
        );

        // Create adapter that populates each 1line with a Unit from volumeUnits
        ArrayAdapter<Unit> volumeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                volumeUnits
        );

        // Attach the adapter
        binding.referenceMassUnitDropdown.setAdapter(massAdapter);
        binding.referenceVolumeUnitDropdown.setAdapter(volumeAdapter);
    }

    private void setupInputListeners() {
        // Ingredient name input listener
        binding.nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setDraftName(charSequence.toString());
            }
        });

        // RadioGroup checked listener
        binding.radioGroup.setOnCheckedChangeListener( (group, checkedId) -> {
            IngredientMode mode = IngredientMode.MASS_ONLY;

            if (checkedId == binding.volumeButton.getId()) {
                mode = IngredientMode.VOLUME_ONLY;
            }
            else if (checkedId == binding.massAndVolumeButton.getId()) {
                mode = IngredientMode.MASS_AND_VOLUME;
            }

            viewModel.setDraftMode(mode);
            updateModeUI(mode);
        });

        // Mass edit text listener
        binding.massEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setDraftReferenceMass(charSequence.toString());
            }
        });

        // Mass unit drop down listener
        binding.referenceMassUnitDropdown.setOnItemClickListener(
                (parent, view, position, id) -> {
            viewModel.setDraftMassUnit(massUnits.get(position));
        });

        // Volume edit text listener
        binding.volumeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                viewModel.setDraftReferenceVolume(charSequence.toString());
            }
        });

        // Volume unit drop down listener
        binding.referenceVolumeUnitDropdown.setOnItemClickListener(
                (parent, view, position, id) -> {
                    viewModel.setDraftVolumeUnit(volumeUnits.get(position));
                }
        );
    }
    private void saveButtonPressed() {
        binding.saveIngredientButton.setOnClickListener(view -> {
            viewModel.saveIngredient();
            Navigation.findNavController(view).popBackStack();
        });
    }
}
