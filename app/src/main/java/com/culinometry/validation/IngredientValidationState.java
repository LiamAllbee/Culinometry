package com.culinometry.validation;

import androidx.lifecycle.LiveData;

public class IngredientValidationState {
    private final String nameError;
    private final String massError;
    private final String massUnitError;
    private final String volumeError;
    private final String volumeUnitError;

    public IngredientValidationState(String nameError,
                                     String massError,
                                     String massUnitError,
                                     String volumeError,
                                     String volumeUnitError) {
        this.nameError = nameError;
        this.massError = massError;
        this.massUnitError = massUnitError;
        this.volumeError = volumeError;
        this.volumeUnitError = volumeUnitError;
    }

    public String getNameError() {
        return nameError;
    }

    public String getMassError() {
        return massError;
    }

    public String getMassUnitError() {
        return massUnitError;
    }

    public String getVolumeError() {
        return volumeError;
    }

    public String getVolumeUnitError() {
        return volumeUnitError;
    }

    // If all are null everything is valid
    public boolean isAllValid() {
        return nameError == null
                && massError == null
                && massUnitError == null
                && volumeError == null
                && volumeUnitError == null;
    }

    public boolean isNameValid() {
        return nameError == null;
    }
}
