package com.culinometry;

import static org.junit.Assert.assertEquals;

import com.culinometry.measurement.Unit;
import com.culinometry.measurement.formatters.FormattedMeasurement;
import com.culinometry.measurement.formatters.MassFormatter;
import com.culinometry.measurement.formatters.MeasurementPart;

import org.junit.Test;

public class MassFormatterTest {

    @Test
    public void GramRoundTest100_orMore() {
        MassFormatter formatter = new MassFormatter();

        FormattedMeasurement result = formatter.format(226.797);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("227", part.getQuantity());
        assertEquals(Unit.GRAM, part.getUnit());
    }

    @Test
    public void GramRoundTest10_orMore() {
        MassFormatter formatter = new MassFormatter();

        FormattedMeasurement result = formatter.format(12.36);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("12.4", part.getQuantity());
        assertEquals(Unit.GRAM, part.getUnit());
    }

    @Test
    public void GramRoundTest1_orMore() {
        MassFormatter formatter = new MassFormatter();

        FormattedMeasurement result = formatter.format(4.273);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("4.27", part.getQuantity());
        assertEquals(Unit.GRAM, part.getUnit());
    }

    @Test
    public void GramRoundTestLessThan1() {
        MassFormatter formatter = new MassFormatter();

        FormattedMeasurement result = formatter.format(0.2734);

        MeasurementPart part = result.getParts().get(0);

        assertEquals("0.273", part.getQuantity());
        assertEquals(Unit.GRAM, part.getUnit());
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_negativeMeasurement_throwsException() {
        MassFormatter formatter = new MassFormatter();

        formatter.format(-5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void format_nan_throwsException() {
        MassFormatter formatter = new MassFormatter();

        formatter.format(Double.NaN);
    }
}
