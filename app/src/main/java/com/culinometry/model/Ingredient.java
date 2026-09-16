package com.culinometry.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.culinometry.measurement.IngredientMode;

@Entity
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "reference_mass_grams")
    private Double referenceMassGrams;

    @Nullable
    @ColumnInfo(name = "reference_volume_ml")
    private Double referenceVolumeML;

    @NonNull
    @ColumnInfo(name = "mode")
    private IngredientMode mode;

    @ColumnInfo(name = "is_soft_deleted")
    private boolean isSoftDeleted;

    public Ingredient(@NonNull String name,
                      @Nullable Double referenceMassGrams,
                      @Nullable Double referenceVolumeML,
                      @NonNull IngredientMode mode,
                      boolean isSoftDeleted) {
        this.name = name;
        this.referenceMassGrams = referenceMassGrams;
        this.referenceVolumeML = referenceVolumeML;
        this.mode = mode;
        this.isSoftDeleted = isSoftDeleted;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @Nullable
    public Double getReferenceMassGrams() {
        return referenceMassGrams;
    }

    public void setReferenceMassGrams(@Nullable Double referenceMassGrams) {
        this.referenceMassGrams = referenceMassGrams;
    }

    @Nullable
    public Double getReferenceVolumeML() {
        return referenceVolumeML;
    }

    public void setReferenceVolumeML(@Nullable Double referenceVolumeML) {
        this.referenceVolumeML = referenceVolumeML;
    }

    @NonNull
    public IngredientMode getMode() {
        return mode;
    }

    public void setMode(@NonNull IngredientMode mode) {
        this.mode = mode;
    }


    public boolean isSoftDeleted() {
        return isSoftDeleted;
    }
    public void setIsSoftDeleted(boolean isSoftDeleted) {
        this.isSoftDeleted = isSoftDeleted;
    }
}
