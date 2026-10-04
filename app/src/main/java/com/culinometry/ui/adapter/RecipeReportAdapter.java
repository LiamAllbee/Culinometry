package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemRecipeReportRowBinding;
import com.culinometry.model.RecipeReportRow;

import java.util.List;

public class RecipeReportAdapter extends ListAdapter<RecipeReportRow, RecipeReportAdapter.RecipeReportHolder> {
    private static final DiffUtil.ItemCallback<RecipeReportRow> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecipeReportRow>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecipeReportRow oldItem, @NonNull RecipeReportRow newItem) {
            return oldItem.getRecipeId() == newItem.getRecipeId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecipeReportRow oldItem, @NonNull RecipeReportRow newItem) {
            return oldItem.getRecipeName().equals(newItem.getRecipeName()) &&
                    oldItem.getIngredientCount() == newItem.getIngredientCount() &&
                    oldItem.getInstructionCount() == newItem.getInstructionCount();
        }
    };

    //List<RecipeReportRow> recipeReportList;
    public RecipeReportAdapter() {
        super(DIFF_CALLBACK);
        //this.recipeReportList = recipeReportList;
    }

    @NonNull
    @Override
    public RecipeReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeReportRowBinding binding = ItemRecipeReportRowBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false
        );
        return new RecipeReportHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeReportHolder holder, int position) {
        holder.bind(getItem(position));
    }





    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    public static class RecipeReportHolder extends RecyclerView.ViewHolder {

        private final ItemRecipeReportRowBinding binding;

        public RecipeReportHolder(ItemRecipeReportRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(RecipeReportRow recipeReportRow) {
            binding.recipeNameTextView.setText(recipeReportRow.getRecipeName());
            binding.ingredientCountTextView.setText(String.valueOf(recipeReportRow.getIngredientCount()));
            binding.instructionCountTextView.setText(String.valueOf(recipeReportRow.getInstructionCount()));
        }
    }
}
