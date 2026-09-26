package com.culinometry.measurement;

public enum QuantityClassification {
    MIXED_FRACTION(true),
    FRACTION(true),
    DECIMAL_OR_WHOLE(true),
    INVALID(false),
    INVALID_EMPTY(false),
    INVALID_NUMERATOR(false),
    INVALID_DENOMINATOR(false);

    private final boolean valid;

    QuantityClassification(boolean valid) {
        this.valid = valid;
    }
    public boolean isValid() {
        return valid;
    }
}
