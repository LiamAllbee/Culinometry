package com.culinometry.measurement.formatters;

import com.culinometry.measurement.Unit;

public class MeasurementPart {

    private final String quantity;
    private final Unit unit;

    public MeasurementPart(String quantity, Unit unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }
}