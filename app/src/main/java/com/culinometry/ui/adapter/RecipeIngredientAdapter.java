package com.culinometry.ui.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemRecipeIngredientBinding;
import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.MeasurementType;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;
import com.culinometry.model.RecipeIngredientDraft;
import com.culinometry.validation.RecipeIngredientValidationState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeIngredientAdapter extends ListAdapter<RecipeIngredientDraft, RecipeIngredientAdapter.RecipeIngredientHolder> {
    public interface OnRecipeIngredientActionListener {

        // Listeners for inputs
        void onIngredientInputChanged(long draftId, String ingredientInput);
        void onIngredientSelected(long draftId, Ingredient ingredient);
        void onQuantityChanged(long draftId, String quantity);
        void onUnitChanged(long draftId, Unit unit);
        void onCheckBoxChanged(long draftId, boolean unitLocked);
        void onDeleteRecipeIngredient(long draftId);
    }

    // Checks if the old draft item matches the new draft item to keep track of changes to items
    // in the list. How update and delete are tracked and managed. How user interaction with inputs
    // are prevented from being constantly updated.
    private static final DiffUtil.ItemCallback<RecipeIngredientDraft> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecipeIngredientDraft>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecipeIngredientDraft oldItem, @NonNull RecipeIngredientDraft newItem) {
            return oldItem.getDraftId() == newItem.getDraftId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecipeIngredientDraft oldItem, @NonNull RecipeIngredientDraft newItem) {
            return sameIngredient(oldItem.getIngredient(), newItem.getIngredient()) &&
                    oldItem.getIngredientInput().equals(newItem.getIngredientInput()) &&
                    oldItem.getQuantity().equals(newItem.getQuantity()) &&
                    Objects.equals(oldItem.getUnit(), newItem.getUnit()) &&
                    oldItem.isUnitLocked() == newItem.isUnitLocked();
        }

        // DiffUtil reports whether instruction item needs to be rebound based on payload
        @Override
        public Object getChangePayload(@NonNull RecipeIngredientDraft oldItem,
                                       @NonNull RecipeIngredientDraft newItem) {
            return ROW_STATE_CHANGED;
        }

        private boolean sameIngredient(Ingredient oldIngredient, Ingredient newIngredient) {
            if (oldIngredient == null && newIngredient == null) {
                return true;
            }

            if (oldIngredient == null || newIngredient == null) {
                return false;
            }

            return oldIngredient.getIngredientId()
                    == newIngredient.getIngredientId();
        }

    };

    private final OnRecipeIngredientActionListener listener;
    private static final Object ROW_STATE_CHANGED = new Object();
    private static final Object INGREDIENT_CATALOG_CHANGED = new Object();
    private static final Object VALIDATION_CHANGED = new Object();
    private List<Ingredient> ingredientCatalog = new ArrayList<>();
    private Map<Long, RecipeIngredientValidationState> validationErrors = new HashMap<>();

    public RecipeIngredientAdapter(OnRecipeIngredientActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // Inflate the item
    @NonNull
    @Override
    public RecipeIngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeIngredientBinding binding = ItemRecipeIngredientBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);

        return new RecipeIngredientHolder(binding, listener);
    }

    // Binds the current item
    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientHolder holder, int position) {
        RecipeIngredientDraft draft = getItem(position);

        holder.bindIngredientCatalog(ingredientCatalog);
        holder.bind(draft, position);

        // Calls the validation state for each row and binds the errors to each row
        holder.bindValidation(validationErrors.get(draft.getDraftId()));
    }

    // Uses payload to perform prevent rebinding while user is editing
    @Override
    public void onBindViewHolder(@NonNull RecipeIngredientHolder holder, int position, @NonNull List<Object> payload) {
        if (payload.isEmpty()) {
            onBindViewHolder(holder, position);
            return;
        }

        if (payload.contains(VALIDATION_CHANGED)) {
            RecipeIngredientDraft draft = getItem(position);
            holder.bindValidation(validationErrors.get(draft.getDraftId()));
        }
        if (payload.contains(INGREDIENT_CATALOG_CHANGED)) {
            holder.bindIngredientCatalog(ingredientCatalog);
        }
        if (payload.contains(ROW_STATE_CHANGED)){
            holder.bindState(getItem(position));
        }
    }

    @Override
    public void onCurrentListChanged(@NonNull List<RecipeIngredientDraft> oldList, @NonNull List<RecipeIngredientDraft> currentList) {
        super.onCurrentListChanged(oldList, currentList);

        if (structureChanged(oldList, currentList)) {
            notifyItemRangeChanged(
                    0,
                    currentList.size(),
                    ROW_STATE_CHANGED
            );
        }
    }

    // Check if old list and current list are the same size and if their IDs match in every row
    // If they all match returns false because the structure is the same
    // If they don't match in 1 position returns true that the structure has changed
    private boolean structureChanged(List<RecipeIngredientDraft> oldList, List<RecipeIngredientDraft> currentList) {
        if (oldList.size() != currentList.size()) {
            return true;
        }

        for (int i = 0; i < oldList.size(); i++) {
            if (oldList.get(i).getDraftId() != currentList.get(i).getDraftId()) {
                return true;
            }
        }

        return false;
    }

    public void setIngredientCatalog(List<Ingredient> ingredients) {
        if (ingredients == null) {
            ingredientCatalog = new ArrayList<>();
        }
        else {
            ingredientCatalog = new ArrayList<>(ingredients);
        }

        notifyItemRangeChanged(
                0,
                getItemCount(),
                INGREDIENT_CATALOG_CHANGED
        );
    }

    // Is used for calling the setting of errors from the fragment. Is called regardless of if there
    // is or isn't errors
    public void setValidationErrors(Map<Long, RecipeIngredientValidationState> validationErrors) {
        this.validationErrors = new HashMap<>(validationErrors);

        notifyItemRangeChanged(
                0,
                getItemCount(),
                VALIDATION_CHANGED
        );
    }




    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    public static class RecipeIngredientHolder extends RecyclerView.ViewHolder {
        private RecipeIngredientDraft currentDraft;

        // Is the binding that's happening being used to attach the item? True if yes False if no
        private boolean isBinding;
        private final ItemRecipeIngredientBinding binding;
        private final ArrayAdapter<Unit> unitDropDownAdapter;

        private final ArrayAdapter<Ingredient> ingredientDropDownAdapter;

        // This is the creation of the row for each item
        public RecipeIngredientHolder(ItemRecipeIngredientBinding binding, OnRecipeIngredientActionListener listener) {
            super(binding.getRoot());

            this.binding = binding;

            ingredientDropDownAdapter = new ArrayAdapter<>(
                    binding.getRoot().getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new ArrayList<>()
            );

            unitDropDownAdapter = new ArrayAdapter<>(
                    binding.getRoot().getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new ArrayList<>()
            );

            binding.ingredientAutoComplete.setAdapter(ingredientDropDownAdapter);
            binding.unitDropdown.setAdapter(unitDropDownAdapter);

            // user typing in the input
            binding.ingredientAutoComplete.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    // If we're not binding listen to the stuff being typed in the instruction
                    if (currentDraft != null && !isBinding) {
                        listener.onIngredientInputChanged(currentDraft.getDraftId(), editable.toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
            });

            // if user clicks item in the input dropdown
            binding.ingredientAutoComplete.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        if (currentDraft != null && !isBinding) {
                            Ingredient ingredient = (Ingredient) parent.getItemAtPosition(position);

                            listener.onIngredientSelected(currentDraft.getDraftId(), ingredient);
                        }
                    }
            );

            binding.ingredientQuantityEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    // If we're not binding listen to the stuff being typed in the instruction
                    if (currentDraft != null && !isBinding) {
                        listener.onQuantityChanged(currentDraft.getDraftId(), editable.toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }
            });

            binding.unitDropdown.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        if (currentDraft != null && !isBinding) {
                            Unit unit = (Unit) parent.getItemAtPosition(position);
                            listener.onUnitChanged(currentDraft.getDraftId(), unit);
                        }
                    });

            binding.unitLockedCheckbox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {
                        if (currentDraft != null && !isBinding) {
                            listener.onCheckBoxChanged(currentDraft.getDraftId(), isChecked);
                        }
                    }
            );

            binding.deleteButton.setOnClickListener(view -> {
                // Only deletes if there even is a draft set up
                if (currentDraft != null) {
                    listener.onDeleteRecipeIngredient(currentDraft.getDraftId());
                }
            });
        }

        // Fill in fields
        public void bind(RecipeIngredientDraft currentDraft, int position) {
            this.currentDraft = currentDraft;

            ingredientInputBind(currentDraft);
            quantityInputBind(currentDraft);
            unitInputBind(currentDraft);
            checkBoxBind(currentDraft);
        }

        private void ingredientInputBind(RecipeIngredientDraft currentDraft) {
            // Prevent re-binding the item when user is typing into the Ingredient autocomplete
            String ingredientInput = currentDraft.getIngredientInput();

            if (binding.ingredientAutoComplete.getText() != null) {
                String currentlyDisplayedText = binding.ingredientAutoComplete.getText().toString();

                // If draft text and display text don't match then it's because the item has not
                // been bound.
                if(!currentlyDisplayedText.equals(ingredientInput)) {
                    isBinding = true;
                    binding.ingredientAutoComplete.setText(ingredientInput, false);
                    isBinding = false;
                }

            }
        }

        private void quantityInputBind(RecipeIngredientDraft currentDraft) {
            // Prevent re-binding the item when user is typing into the Ingredient autocomplete
            String quantityInput = currentDraft.getQuantity();

            if (binding.ingredientQuantityEditText.getText() != null) {
                String currentlyDisplayedText = binding.ingredientQuantityEditText.getText().toString();

                // If draft text and display text don't match then it's because the item has not
                // been bound.
                if(!currentlyDisplayedText.equals(quantityInput)) {
                    isBinding = true;
                    binding.ingredientQuantityEditText.setText(quantityInput);
                    isBinding = false;
                }

            }
        }

        private void unitInputBind(RecipeIngredientDraft currentDraft) {

            bindAllowedUnits(currentDraft);

            Unit unit = currentDraft.getUnit();
            String unitText = unit == null ? "" : unit.toString();

            isBinding = true;
            binding.unitDropdown.setText(unitText, false);
            isBinding = false;
        }

        private void bindAllowedUnits(RecipeIngredientDraft draft) {
            // On bind always reset the adapter
            unitDropDownAdapter.clear();

            Ingredient ingredient = draft.getIngredient();

            // If there's no ingredient selected disable all inputs besides ingredient
            if (ingredient == null) {
                binding.unitDropdown.setEnabled(false);
                binding.ingredientQuantityEditText.setEnabled(false);
                binding.unitLockedCheckbox.setEnabled(false);

                unitDropDownAdapter.notifyDataSetChanged();
                return;
            }

            binding.unitDropdown.setEnabled(true);
            binding.ingredientQuantityEditText.setEnabled(true);
            binding.unitLockedCheckbox.setEnabled(true);

            IngredientMode mode = ingredient.getMode();

            for (Unit unit : Unit.values()) {
                if (mode == IngredientMode.MASS_AND_VOLUME) {
                    unitDropDownAdapter.add(unit);
                }
                else if (mode == IngredientMode.MASS_ONLY && unit.getType() == MeasurementType.MASS) {
                    unitDropDownAdapter.add(unit);
                }
                else if (mode == IngredientMode.VOLUME_ONLY && unit.getType() == MeasurementType.VOLUME) {
                    unitDropDownAdapter.add(unit);
                }
            }

            unitDropDownAdapter.notifyDataSetChanged();
        }

        private void checkBoxBind(RecipeIngredientDraft currentDraft) {
            boolean displayed = binding.unitLockedCheckbox.isChecked();

            if (displayed != currentDraft.isUnitLocked()) {
                isBinding = true;
                binding.unitLockedCheckbox.setChecked(currentDraft.isUnitLocked());
                isBinding = false;
            }
        }

        // This runs whenever a row needs to be refreshed.
        public void bindState(RecipeIngredientDraft draft) {
            currentDraft = draft;

            bindIngredientInputState(draft);
            bindQuantityState(draft);
            bindAllowedUnits(draft);
            bindUnitState(draft);
            bindCheckBoxState(draft);
        }

        public void bindIngredientInputState(RecipeIngredientDraft draft) {
            this.currentDraft = draft;

            // Only when instructionEditText does NOT have focus can it update the bind.
            if (!binding.ingredientAutoComplete.hasFocus()) {
                String displayed = binding.ingredientAutoComplete.getText().toString();

                if (!displayed.equals(draft.getIngredientInput())) {
                    isBinding = true;
                    binding.ingredientAutoComplete.setText(draft.getIngredientInput(), false);
                    isBinding = false;
                }
            }
        }

        public void bindQuantityState(RecipeIngredientDraft draft) {
            this.currentDraft = draft;

            // Only when instructionEditText does NOT have focus can it update the bind.
            if (!binding.ingredientQuantityEditText.hasFocus()) {
                String displayed = binding.ingredientQuantityEditText.getText().toString();

                if (!displayed.equals(draft.getQuantity())) {
                    isBinding = true;
                    binding.ingredientQuantityEditText.setText(draft.getQuantity());
                    isBinding = false;
                }
            }
        }

        public void bindUnitState(RecipeIngredientDraft draft) {
            this.currentDraft = draft;

            // Only when instructionEditText does NOT have focus can it update the bind.
            if (!binding.unitDropdown.hasFocus()) {
                String displayed = binding.unitDropdown.getText().toString();
                Unit unit = draft.getUnit();
                String unitText = unit == null ? "" : unit.toString();

                if (!displayed.equals(unitText)) {
                    isBinding = true;
                    binding.unitDropdown.setText(unitText, false);
                    isBinding = false;
                }
            }
        }

        public void bindCheckBoxState(RecipeIngredientDraft draft) {
            this.currentDraft = draft;

            boolean displayed = binding.unitLockedCheckbox.isChecked();

            if (displayed != draft.isUnitLocked()) {
                isBinding = true;
                binding.unitLockedCheckbox.setChecked(draft.isUnitLocked());
                isBinding = false;
            }
        }

        public void bindIngredientCatalog(List<Ingredient> ingredients) {
            ingredientDropDownAdapter.clear();
            ingredientDropDownAdapter.addAll(ingredients);
            ingredientDropDownAdapter.notifyDataSetChanged();
        }

        public void bindValidation(RecipeIngredientValidationState state) {
            if (state == null) {
                binding.ingredientInputLayout.setError(null);
                binding.ingredientQuantityLayout.setError(null);
                binding.ingredientUnitLayout.setError(null);
            }
            else {
                binding.ingredientInputLayout.setError(state.getIngredientError());
                binding.ingredientQuantityLayout.setError(state.getQuantityError());
                binding.ingredientUnitLayout.setError(state.getUnitError());
            }
        }
    }
}
