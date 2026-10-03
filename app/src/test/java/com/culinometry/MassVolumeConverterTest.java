package com.culinometry;

import static org.junit.Assert.assertEquals;

import com.culinometry.measurement.IngredientMode;
import com.culinometry.measurement.MassVolumeConverter;
import com.culinometry.measurement.Unit;
import com.culinometry.model.Ingredient;

import org.junit.Test;

public class MassVolumeConverterTest {
    private Ingredient createTestFlour() {

        return new Ingredient(
                "Flour",
                "120",
                Unit.GRAM,
                "1",
                Unit.US_CUP,
                IngredientMode.MASS_AND_VOLUME,
                false
        );
    }

    @Test
    public void convert_Grams_To_Cups() {

        Ingredient flour = createTestFlour();

        double result = MassVolumeConverter.convert(
                        60,
                        Unit.GRAM,
                        Unit.US_CUP,
                        flour
        );

        assertEquals(0.5, result, 0.000001);
    }

    @Test
    public void convert_Cup_To_Grams() {

        Ingredient flour = createTestFlour();

        double result = MassVolumeConverter.convert(
                        0.5,
                        Unit.US_CUP,
                        Unit.GRAM,
                        flour
        );

        assertEquals(60.0, result, 0.000001);
    }

    @Test
    public void convert_NonDefinedUnit_ToCup() {

        Ingredient flour = new Ingredient(
                "Flour",
                "4.232875",
                Unit.OUNCE,
                "1",
                Unit.US_CUP,
                IngredientMode.MASS_AND_VOLUME,
                false
        );

        double result = MassVolumeConverter.convert(
                        0.5,
                        Unit.US_CUP,
                        Unit.GRAM,
                        flour
        );

        assertEquals(60.0, result, 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void Conversion_Exception() {

        Ingredient chicken = new Ingredient(
                "Chicken",
                null,
                null,
                null,
                null,
                IngredientMode.MASS_ONLY,
                false
        );

        MassVolumeConverter.convert(
                100,
                Unit.GRAM,
                Unit.US_CUP,
                chicken
        );
    }
}
