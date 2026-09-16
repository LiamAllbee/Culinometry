package com.culinometry.measurement;

public enum Unit {
    // METRIC MASS
    GRAM(MeasurementType.MASS),
    KILOGRAM(MeasurementType.MASS),

    // METRIC VOLUME
    MILLILITER(MeasurementType.VOLUME),
    LITER(MeasurementType.VOLUME),

    // US CUSTOMARY MASS
    OUNCE(MeasurementType.MASS),
    POUND(MeasurementType.MASS),

    // US CUSTOMARY VOLUME
    US_TEASPOON(MeasurementType.VOLUME),
    US_TABLESPOON(MeasurementType.VOLUME),
    US_FLUID_OUNCE(MeasurementType.VOLUME),
    US_CUP(MeasurementType.VOLUME),
    US_PINT(MeasurementType.VOLUME),
    US_QUART(MeasurementType.VOLUME),
    US_GALLON(MeasurementType.VOLUME);

    private final MeasurementType type;

    Unit(MeasurementType type) {
        this.type = type;
    }

    public MeasurementType getType() {
        return type;
    }
}
