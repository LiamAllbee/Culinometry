package com.culinometry.measurement.formatters;

import com.culinometry.measurement.MeasurementType;
import com.culinometry.measurement.Unit;

public abstract class MeasurementFormatter {
    public final FormattedMeasurement format(double value, Unit unit) {
        validate(value, unit);
        return formatMeasurement(value, unit);
    }

    protected abstract MeasurementType getMeasurementType();

    protected abstract FormattedMeasurement formatMeasurement(double value, Unit unit);

    private void validate(double value, Unit unit) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    "Measurement must be a finite number."
            );
        }

        if (value < 0) {
            throw new IllegalArgumentException(
                    "Measurement cannot be negative."
            );
        }

        if (unit == null) {
            throw new IllegalArgumentException(
                    "Unit cannot be null."
            );
        }

        if (unit.getType() != getMeasurementType()) {
            throw new IllegalArgumentException(
                    "Unit does not match formatter type."
            );
        }
    }
}
