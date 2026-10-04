package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.R;
import com.culinometry.databinding.ItemDetailRecipeIngredientBinding;
import com.culinometry.measurement.Unit;
import com.culinometry.model.RecipeIngredientDisplay;

import java.util.List;
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

        public IngredientDetailHolder(ItemDetailRecipeIngredientBinding binding, OnUnitSelectedListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.unitButton.setOnClickListener(view -> {
                if (recipeIngredientDisplay == null || !recipeIngredientDisplay.isConversionEnabled()) {
                    return;
                }

                PopupMenu popupMenu = new PopupMenu(view.getContext(), binding.unitButton);

                List<Unit> availableUnits = recipeIngredientDisplay.getAvailableUnits();

                for (int i = 0; i < availableUnits.size(); i++) {
                    popupMenu.getMenu().add(
                            Menu.NONE,
                            i,
                            i,
                            availableUnits.get(i).toString()
                    );
                }

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    Unit selected = availableUnits.get(menuItem.getItemId());

                    listener.onUnitSelected(recipeIngredientDisplay.getRecipeIngredientId(), selected);

                    return true;
                });

                popupMenu.show();
            });
        }

        public void bind(RecipeIngredientDisplay recipeIngredientDisplay) {
            this.recipeIngredientDisplay = recipeIngredientDisplay;

            String stepNumber = (recipeIngredientDisplay.getSortOrder() + 1) + ".";
            binding.ingredientNumberTextView.setText(stepNumber);

            binding.ingredientQuantityTextView.setText(recipeIngredientDisplay.getQuantity());

            binding.ingredientNameTextView.setText(recipeIngredientDisplay.getIngredientName());

            binding.unitButton.setText(recipeIngredientDisplay.getSelectedUnit().toString());

            if (recipeIngredientDisplay.isConversionEnabled()) {
                binding.unitButton.setEnabled(true);
                binding.unitButton.setIconResource(R.drawable.arrow_drop_down_24px);
            }
            else {
                binding.unitButton.setEnabled(false);
                binding.unitButton.setIcon(null);
            }

        }
    }
}