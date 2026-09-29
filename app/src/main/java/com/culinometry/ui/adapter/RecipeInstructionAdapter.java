package com.culinometry.ui.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.culinometry.databinding.ItemRecipeInstructionBinding;

import com.culinometry.model.RecipeInstruction;
import com.culinometry.model.RecipeInstructionDraft;

import org.jspecify.annotations.NonNull;

import java.util.List;

public class RecipeInstructionAdapter extends ListAdapter<RecipeInstructionDraft, RecipeInstructionAdapter.RecipeInstructionHolder> {

    public interface OnInstructionActionListener {
        void onInstructionChanged(long draftId,
                                  String instruction);

        void onDeleteInstruction(long draftId);
    }

    // Checks if the old draft item matches the new draft item to keep track of changes to items
    // in the list. How update and delete are tracked and managed.
    private static final DiffUtil.ItemCallback<RecipeInstructionDraft> DIFF_CALLBACK = new DiffUtil.ItemCallback<RecipeInstructionDraft>() {
        @Override
        public boolean areItemsTheSame(@NonNull RecipeInstructionDraft oldItem, @NonNull RecipeInstructionDraft newItem) {
            return oldItem.getDraftId() == newItem.getDraftId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecipeInstructionDraft oldItem, @NonNull RecipeInstructionDraft newItem) {
            return oldItem.getInstruction().equals(newItem.getInstruction());
        }

        // DiffUtil reports whether instruction item needs to be rebound based on instruction contents
        @Override
        public Object getChangePayload(@NonNull RecipeInstructionDraft oldItem,
                                       @NonNull RecipeInstructionDraft newItem) {
            if (!oldItem.getInstruction().equals(newItem.getInstruction())) {
                return INSTRUCTION_CHANGED;
            }

            return null;
        }
    };

    private final OnInstructionActionListener listener;
    private static final Object CHANGED_ROW = new Object();
    private static final Object INSTRUCTION_CHANGED = new Object();

    public RecipeInstructionAdapter(OnInstructionActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    // Inflate the item
    @NonNull
    @Override
    public RecipeInstructionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeInstructionBinding binding = ItemRecipeInstructionBinding.inflate(LayoutInflater.from(
                        parent.getContext()),
                parent,
                false);

        return new RecipeInstructionHolder(binding, listener);
    }

    // Binds the current item
    @Override
    public void onBindViewHolder(@NonNull RecipeInstructionHolder holder, int position) {
        holder.bind(getItem(position), position);
    }

    // Uses payload to perform a partial update and refresh the instruction numbers
    @Override
    public void onBindViewHolder(@NonNull RecipeInstructionHolder holder, int position, @NonNull List<Object> payload) {
        // Once payload contains the position of the CHANGED_ROW item Object it can update the number
        if (payload.contains(CHANGED_ROW)) {
            holder.bindStepNumber(position);
        }

        // If payload contains INSTRUCTION_CHANGED don't bind while user is typing
        if (payload.contains(INSTRUCTION_CHANGED)) {
            holder.bindInstructionState(getItem(position));
        }

        if (payload.isEmpty()) {
            onBindViewHolder(holder, position);
        }
    }

    // If current list has changed the payload is sent with an update to every row
    @Override
    public void onCurrentListChanged(@NonNull List<RecipeInstructionDraft> oldList,
                                     @NonNull List<RecipeInstructionDraft> currentList) {
        super.onCurrentListChanged(oldList, currentList);

        if (structureChanged(oldList, currentList)) {
            notifyItemRangeChanged(
                    0,
                    currentList.size(),
                    CHANGED_ROW
            );
        }
    }

    // Check if old list and current list are the same size and if their IDs match in every row
    // If they all match returns false because the structure is the same
    // If they don't match in 1 position returns true that the structure has changed
    private boolean structureChanged(List<RecipeInstructionDraft> oldList, List<RecipeInstructionDraft> currentList) {
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

    public static class RecipeInstructionHolder extends RecyclerView.ViewHolder {

        private RecipeInstructionDraft currentDraft;

        // Is the binding that's happening being used to attach the item? True if yes False if no
        private boolean isBinding;
        private final ItemRecipeInstructionBinding binding;
        private final OnInstructionActionListener listener;

        public RecipeInstructionHolder(ItemRecipeInstructionBinding binding, OnInstructionActionListener listener) {
            super(binding.getRoot());

            this.binding = binding;
            this.listener = listener;

            binding.instructionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                    // If we're not binding listen to the stuff being typed in the instruction
                    if (currentDraft != null && !isBinding) {
                        listener.onInstructionChanged(currentDraft.getDraftId(), editable.toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
            });

            binding.deleteButton.setOnClickListener(view -> {
                // Only deletes if there even is a draft set up
                if (currentDraft != null) {
                    listener.onDeleteInstruction(currentDraft.getDraftId());
                }
            });
        }

        // Fill in fields
        public void bind(RecipeInstructionDraft currentDraft, int position) {
            this.currentDraft = currentDraft;

            binding.instructionStep.setText("Step " + (position + 1) + ":");

            // Prevent re-binding the item when user is typing.
            String instruction = currentDraft.getInstruction();
            if (binding.instructionEditText.getText() != null) {
                String currentlyDisplayedText = binding.instructionEditText.getText().toString();

                // If draft text and display text don't match then it's because the item has not
                // been bound.
                if(!currentlyDisplayedText.equals(instruction)) {
                    isBinding = true;
                    binding.instructionEditText.setText(currentDraft.getInstruction());
                    isBinding = false;
                }

            }

        }

        // Isolates bind to just the instruction number
        public void bindStepNumber(int position) {
            binding.instructionStep.setText("Step " + (position + 1) + ":");
        }

        public void bindInstructionState(RecipeInstructionDraft draft) {
            currentDraft = draft;

            // Only when instructionEditText does NOT have focus can it update the bind.
            if (!binding.instructionEditText.hasFocus()) {
                String displayed = binding.instructionEditText.getText().toString();

                if (!displayed.equals(draft.getInstruction())) {
                    isBinding = true;
                    binding.instructionEditText.setText(draft.getInstruction());
                    isBinding = false;
                }
            }
        }
    }
}