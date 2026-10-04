package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemIngredientBinding;
import com.culinometry.model.Ingredient;

import org.jspecify.annotations.NonNull;

public class IngredientAdapter extends ListAdapter<Ingredient, IngredientAdapter.IngredientHolder> {

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }
    private final OnIngredientClickListener listener;

    private static final DiffUtil.ItemCallback<Ingredient> DIFF_CALLBACK = new DiffUtil.ItemCallback<Ingredient>() {
        @Override
        public boolean areItemsTheSame(@androidx.annotation.NonNull Ingredient oldItem, @androidx.annotation.NonNull Ingredient newItem) {
            return oldItem.getIngredientId() == newItem.getIngredientId();
        }

        @Override
        public boolean areContentsTheSame(@androidx.annotation.NonNull Ingredient oldItem, @androidx.annotation.NonNull Ingredient newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    public IngredientAdapter(OnIngredientClickListener listener) {
        super(DIFF_CALLBACK);
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
        holder.bind(getItem(position));
    }





    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // Beginning of IngredientHolder

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