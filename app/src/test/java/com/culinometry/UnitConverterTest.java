package com.culinometry;

import static org.junit.Assert.assertEquals;

import com.culinometry.measurement.Unit;
import com.culinometry.measurement.UnitConverter;

import org.junit.Test;

public class UnitConverterTest {

    @Test
    public void convert_LB_To_Ounce() {
        double result = UnitConverter.convert(1.0, Unit.POUND, Unit.OUNCE);
        assertEquals(16.0, result, 0.000001);
    }

    @Test
    public void convert_Cup_To_Tbsp() {
        double result = UnitConverter.convert(1.0, Unit.US_CUP, Unit.US_TABLESPOON);
        assertEquals(16.0, result, 0.000001);
    }

    @Test
    public void convert_KG_To_G() {
        double result = UnitConverter.convert(1.0, Unit.KILOGRAM, Unit.GRAM);
        assertEquals(1000.0, result, 0.000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegal_Conversion_Test() {

        UnitConverter.convert(100.0, Unit.GRAM, Unit.US_CUP);
    }

}
