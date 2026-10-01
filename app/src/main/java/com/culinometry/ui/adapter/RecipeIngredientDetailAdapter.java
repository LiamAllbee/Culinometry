package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemDetailRecipeIngredientBinding;
import com.culinometry.model.Ingredient;
import com.culinometry.model.RecipeIngredient;
import com.culinometry.model.RecipeIngredientWithIngredient;

import java.util.List;

public class RecipeIngredientDetailAdapter extends RecyclerView.Adapter<RecipeIngredientDetailAdapter.IngredientDetailHolder> {
    private final List<RecipeIngredientWithIngredient> recipeIngredientWithIngredientList;

    public RecipeIngredientDetailAdapter(List<RecipeIngredientWithIngredient> recipeIngredientWithIngredientList) {
        this.recipeIngredientWithIngredientList = recipeIngredientWithIngredientList;

    }

    @NonNull
    @Override
    public IngredientDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailRecipeIngredientBinding binding = ItemDetailRecipeIngredientBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false
        );
        return new IngredientDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientDetailHolder holder, int position) {
        holder.bind(recipeIngredientWithIngredientList.get(position));
    }

    @Override
    public int getItemCount() {
        return recipeIngredientWithIngredientList.size();
    }



    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    public static class IngredientDetailHolder extends RecyclerView.ViewHolder {

        private RecipeIngredientWithIngredient recipeIngredientWithIngredient;
        private final ItemDetailRecipeIngredientBinding binding;

        public IngredientDetailHolder(ItemDetailRecipeIngredientBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(RecipeIngredientWithIngredient recipeIngredientWithIngredient) {
            this.recipeIngredientWithIngredient = recipeIngredientWithIngredient;
            RecipeIngredient recipeIngredient = recipeIngredientWithIngredient.getRecipeIngredient();
            Ingredient ingredient = recipeIngredientWithIngredient.getIngredient();

            String ingredientNum = recipeIngredient.getSortOrder() + 1 + ".";
            binding.ingredientNumberTextView.setText(ingredientNum);

            String ingredientInfo = recipeIngredient.getQuantity() + " " + recipeIngredient.getUnit().toString() + " " + ingredient.getName();

            binding.ingredientDetailTextView.setText(ingredientInfo);
        }
    }
}