package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemDetailRecipeIngredientBinding;
import com.culinometry.measurement.Unit;
import com.culinometry.model.RecipeIngredientDisplay;

import java.util.ArrayList;
import java.util.Objects;

public class RecipeIngredientDetailAdapter extends ListAdapter<RecipeIngredientDisplay, RecipeIngredientDetailAdapter.IngredientDetailHolder> {
    public interface OnUnitSelectedListener {
        void onUnitSelected(long recipeIngredientId, Unit unit);
    }

    private static final DiffUtil.ItemCallback<RecipeIngredientDisplay> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecipeIngredientDisplay>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecipeIngredientDisplay oldItem, @NonNull RecipeIngredientDisplay newItem) {
            return oldItem.getRecipeIngredientId() == newItem.getRecipeIngredientId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecipeIngredientDisplay oldItem, @NonNull RecipeIngredientDisplay newItem) {
            return oldItem.getQuantity().equals(newItem.getQuantity()) &&
                    Objects.equals(oldItem.getSelectedUnit(), newItem.getSelectedUnit()) &&
                    oldItem.getIngredientName().equals(newItem.getIngredientName()) &&
                    oldItem.isConversionEnabled() == newItem.isConversionEnabled() &&
                    oldItem.getAvailableUnits().equals(newItem.getAvailableUnits());
        }
    };

    private final OnUnitSelectedListener listener;

    public RecipeIngredientDetailAdapter(OnUnitSelectedListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;

    }

    @NonNull
    @Override
    public IngredientDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailRecipeIngredientBinding binding = ItemDetailRecipeIngredientBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false
        );
        return new IngredientDetailHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientDetailHolder holder, int position) {
        holder.bind(getItem(position));
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    public static class IngredientDetailHolder extends RecyclerView.ViewHolder {

        private RecipeIngredientDisplay recipeIngredientDisplay;
        private final ItemDetailRecipeIngredientBinding binding;
        private final ArrayAdapter<Unit> unitDropDownAdapter;


        public IngredientDetailHolder(ItemDetailRecipeIngredientBinding binding, OnUnitSelectedListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            unitDropDownAdapter = new ArrayAdapter<>(
                    binding.getRoot().getContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    new ArrayList<>());

            binding.unitDropdown.setAdapter(unitDropDownAdapter);

            binding.unitDropdown.setOnItemClickListener(
                    (parent, view, position, id) -> {
                        if (recipeIngredientDisplay == null) {
                            return;
                        }

                        Unit selected = (Unit) parent.getItemAtPosition(position);

                        listener.onUnitSelected(recipeIngredientDisplay.getRecipeIngredientId(), selected);
                    }
            );
        }

        public void bind(RecipeIngredientDisplay recipeIngredientDisplay) {
            this.recipeIngredientDisplay = recipeIngredientDisplay;

            String stepNumber = (recipeIngredientDisplay.getSortOrder() + 1) + ".";
            binding.ingredientNumberTextView.setText(stepNumber);

            binding.ingredientQuantityTextView.setText(recipeIngredientDisplay.getQuantity());

            binding.ingredientNameTextView.setText(recipeIngredientDisplay.getIngredientName());

            unitDropDownAdapter.clear();
            unitDropDownAdapter.addAll(recipeIngredientDisplay.getAvailableUnits());
            unitDropDownAdapter.notifyDataSetChanged();

            binding.unitDropdown.setEnabled(recipeIngredientDisplay.isConversionEnabled());

            binding.unitDropdown.setText(recipeIngredientDisplay.getSelectedUnit().toString(), false);
        }
    }
}