package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemRecipeBinding;
import com.culinometry.model.Recipe;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    private final List<Recipe> recipeList;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Recipe> recipeList, OnRecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false);

        return new RecipeHolder(binding, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder holder, int position) {
        holder.bind(recipeList.get(position));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeHolder extends RecyclerView.ViewHolder {

        private Recipe recipe;
        private final ItemRecipeBinding binding;

        public RecipeHolder(ItemRecipeBinding binding, OnRecipeClickListener listener) {
            super(binding.getRoot());

            this.binding = binding;

            binding.getRoot().setOnClickListener(
                    view -> listener.onRecipeClick(recipe)
            );
        }

        public void bind(Recipe recipe) {
            this.recipe = recipe;

            binding.cardName.setText(recipe.getName());
            binding.cardDescription.setText(recipe.getDescription());
        }
    }
}