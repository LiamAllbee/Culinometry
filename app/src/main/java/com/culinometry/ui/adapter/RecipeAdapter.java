package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemRecipeBinding;
import com.culinometry.model.Recipe;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.RecipeHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final OnRecipeClickListener listener;

    public static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK = new DiffUtil.ItemCallback<Recipe>() {
        @Override
        public boolean areItemsTheSame(@androidx.annotation.NonNull Recipe oldItem, @androidx.annotation.NonNull Recipe newItem) {
            return oldItem.getRecipeId() == newItem.getRecipeId();
        }

        @Override
        public boolean areContentsTheSame(@androidx.annotation.NonNull Recipe oldItem, @androidx.annotation.NonNull Recipe newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public RecipeAdapter(OnRecipeClickListener listener) {
        super(DIFF_CALLBACK);
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
        holder.bind(getItem(position));
    }




    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Beginning of RecipeHolder

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