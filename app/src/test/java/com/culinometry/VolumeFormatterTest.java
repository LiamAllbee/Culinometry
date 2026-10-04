package com.culinometry;

import static org.junit.Assert.assertEquals;

import com.culinometry.measurement.Unit;
import com.culinometry.measurement.formatters.FormattedMeasurement;
import com.culinometry.measurement.formatters.MeasurementPart;
import com.culinometry.measurement.formatters.VolumeFormatter;

import org.junit.Test;

public class VolumeFormatterTest {
    @Test
    public void MilliliterRoundTest100_orMore() {
        VolumeFormatter formatter = new VolumeFormatter();

        FormattedMeasurement result = formatter.format(226.797, Unit.MILLILITER);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("227", part.getQuantity());
        assertEquals(Unit.MILLILITER, part.getUnit());
    }

    @Test
    public void MilliliterRoundTest10_orMore() {
        VolumeFormatter formatter = new VolumeFormatter();

        FormattedMeasurement result = formatter.format(12.36, Unit.MILLILITER);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("12.4", part.getQuantity());
        assertEquals(Unit.MILLILITER, part.getUnit());
    }

    @Test
    public void MilliliterRoundTest1_orMore() {
        VolumeFormatter formatter = new VolumeFormatter();

        FormattedMeasurement result = formatter.format(4.273, Unit.MILLILITER);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("4.27", part.getQuantity());
        assertEquals(Unit.MILLILITER, part.getUnit());
    }

    @Test
    public void MilliliterRoundTestLessThan1() {
        VolumeFormatter formatter = new VolumeFormatter();

        FormattedMeasurement result = formatter.format(0.2734, Unit.MILLILITER);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("0.273", part.getQuantity());
        assertEquals(Unit.MILLILITER, part.getUnit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_negativeMeasurement_throwsException() {
        VolumeFormatter formatter = new VolumeFormatter();

        formatter.format(-5, Unit.MILLILITER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_nan_throwsException() {
        VolumeFormatter formatter = new VolumeFormatter();

        formatter.format(Double.NaN, Unit.MILLILITER);
    }
}
