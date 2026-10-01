package com.culinometry.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemDetailRecipeInstructionBinding;
import com.culinometry.model.RecipeInstruction;

import java.util.List;

public class InstructionDetailAdapter extends RecyclerView.Adapter<InstructionDetailAdapter.InstructionDetailHolder> {
    private final List<RecipeInstruction> recipeInstructionList;

    public InstructionDetailAdapter(List<RecipeInstruction> recipeInstructionList) {
        this.recipeInstructionList = recipeInstructionList;
    }

    @NonNull
    @Override
    public InstructionDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailRecipeInstructionBinding binding = ItemDetailRecipeInstructionBinding.inflate(LayoutInflater.from(
                parent.getContext()),
                parent,
                false
        );
        return new InstructionDetailHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionDetailHolder holder, int position) {
        holder.bind(recipeInstructionList.get(position));
    }

    @Override
    public int getItemCount() {
        return recipeInstructionList.size();
    }




    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    public static class InstructionDetailHolder extends RecyclerView.ViewHolder {

        private RecipeInstruction recipeInstruction;
        private final ItemDetailRecipeInstructionBinding binding;

        public InstructionDetailHolder(ItemDetailRecipeInstructionBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(RecipeInstruction recipeInstruction) {
            this.recipeInstruction = recipeInstruction;

            String instructionNum = recipeInstruction.getSortOrder() + 1 + ".";
            binding.instructionNumberTextView.setText(instructionNum);

            binding.instructionDetailTextView.setText(recipeInstruction.getInstruction());
        }
    }
}

