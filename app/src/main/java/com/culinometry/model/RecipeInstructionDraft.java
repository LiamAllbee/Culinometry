package com.culinometry.model;

public class RecipeInstructionDraft {
    private final long draftId;
    private String instruction;

    public RecipeInstructionDraft(long draftId) {
        this.draftId = draftId;
        this.instruction = "";
    }

    public RecipeInstructionDraft(long draftId, String instruction) {
        this.draftId = draftId;
        this.instruction = instruction;
    }

    public long getDraftId() {
        return draftId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
