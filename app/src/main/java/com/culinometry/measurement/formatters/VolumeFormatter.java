package com.culinometry.measurement.formatters;

import com.culinometry.measurement.MeasurementType;
import com.culinometry.measurement.Unit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

public class VolumeFormatter extends MeasurementFormatter {

    @Override
    protected MeasurementType getMeasurementType() {
        return MeasurementType.VOLUME;
    }

    @Override
    protected FormattedMeasurement formatMeasurement(double milliliters, Unit unit) {

        int decimalPlaces = determineDecimalPlaces(milliliters);

        BigDecimal roundedValue = BigDecimal
                .valueOf(milliliters)
                .setScale(decimalPlaces, RoundingMode.HALF_UP)
                .stripTrailingZeros();

        String quantity = roundedValue.toPlainString();

        MeasurementPart part = new MeasurementPart(
                quantity,
                unit
        );

        return new FormattedMeasurement(Collections.singletonList(part));
    }

    private int determineDecimalPlaces(double grams) {

        if (grams >= 100) {
            return 0;
        }

        if (grams >= 10) {
            return 1;
        }

        if (grams >= 1) {
            return 2;
        }

        return 3;
    }
}
