package com.culinometry.measurement.formatters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormattedMeasurement {
    List<MeasurementPart> parts;
    public FormattedMeasurement(List<MeasurementPart> parts) {
        this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
    }

    public List<MeasurementPart> getParts() {
        return parts;
    }
}
