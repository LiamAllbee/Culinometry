package com.culinometry.measurement;

public enum Unit {
    // METRIC MASS
    GRAM(MeasurementType.MASS, "g"),
    KILOGRAM(MeasurementType.MASS, "kg"),

    // METRIC VOLUME
    MILLILITER(MeasurementType.VOLUME, "mL"),
    LITER(MeasurementType.VOLUME, "L"),

    // US CUSTOMARY MASS
    OUNCE(MeasurementType.MASS, "oz"),
    POUND(MeasurementType.MASS, "lb"),

    // US CUSTOMARY VOLUME
    US_TEASPOON(MeasurementType.VOLUME, "tsp"),
    US_TABLESPOON(MeasurementType.VOLUME, "tbsp"),
    US_FLUID_OUNCE(MeasurementType.VOLUME, "fl oz"),
    US_CUP(MeasurementType.VOLUME, "cup"),
    US_PINT(MeasurementType.VOLUME, "pt"),
    US_QUART(MeasurementType.VOLUME, "qt"),
    US_GALLON(MeasurementType.VOLUME, "gal");

    private final MeasurementType type;
    private final String displayName;

    Unit(MeasurementType type, String displayName) {
        this.type = type;
        this.displayName = displayName;
    }

    public MeasurementType getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}
