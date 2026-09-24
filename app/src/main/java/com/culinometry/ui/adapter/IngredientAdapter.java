package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemIngredientBinding;
import com.culinometry.model.Ingredient;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientHolder> {

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }
    private final List<Ingredient> ingredientList;
    private final OnIngredientClickListener listener;

    public IngredientAdapter(List<Ingredient> ingredientList, OnIngredientClickListener listener) {
        this.ingredientList = ingredientList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);

        return new IngredientHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientHolder holder, int position) {
        holder.bind(ingredientList.get(position));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public static class IngredientHolder extends RecyclerView.ViewHolder {

        private Ingredient ingredient;
        private final ItemIngredientBinding binding;

        public IngredientHolder(ItemIngredientBinding binding, OnIngredientClickListener listener) {
            super(binding.getRoot());

            this.binding = binding;

            binding.getRoot().setOnClickListener(
                    view -> listener.onIngredientClick(ingredient)
            );
        }

        public void bind(Ingredient ingredient) {
            this.ingredient = ingredient;

            binding.cardName.setText(ingredient.getName());
        }
    }
}