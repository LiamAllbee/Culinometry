package com.culinometry.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.Unit;

@Entity
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "reference_mass")
    private String referenceMass;

    @Nullable
    @ColumnInfo(name = "mass_unit")
    private Unit massUnit;

    @Nullable
    @ColumnInfo(name = "reference_volume")
    private String referenceVolume;

    @Nullable
    @ColumnInfo(name = "volume_unit")
    private Unit volumeUnit;

    @NonNull
    @ColumnInfo(name = "mode")
    private IngredientMode mode;

    @ColumnInfo(name = "is_soft_deleted")
    private boolean isSoftDeleted;

    public Ingredient(@NonNull String name,
                      @Nullable String referenceMass,
                      @Nullable Unit massUnit,
                      @Nullable String referenceVolume,
                      @Nullable Unit volumeUnit,
                      @NonNull IngredientMode mode,
                      boolean isSoftDeleted) {
        this.name = name;
        this.referenceMass = referenceMass;
        this.massUnit = massUnit;
        this.referenceVolume = referenceVolume;
        this.volumeUnit = volumeUnit;
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
    public String getReferenceMass() {
        return referenceMass;
    }

    public void setReferenceMass(@Nullable String referenceMass) {
        this.referenceMass = referenceMass;
    }

    @Nullable
    public Unit getMassUnit() {
        return massUnit;
    }

    public void setMassUnit(@Nullable Unit massUnit) {
        this.massUnit = massUnit;
    }

    @Nullable
    public String getReferenceVolume() {
        return referenceVolume;
    }

    public void setReferenceVolume(@Nullable String referenceVolume) {
        this.referenceVolume = referenceVolume;
    }

    @Nullable
    public Unit getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(@Nullable Unit volumeUnit) {
        this.volumeUnit = volumeUnit;
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

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
