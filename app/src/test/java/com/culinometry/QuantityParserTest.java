package com.culinometry;

import static org.junit.Assert.assertEquals;

import com.culinometry.measurement.QuantityClassification;
import com.culinometry.measurement.QuantityParser;

import org.junit.Test;

public class QuantityParserTest {
    @Test
    public void mixedFractionsTests() {
        QuantityClassification mixedFraction = QuantityParser.classify("1 1/3");
        QuantityClassification mixedFractionInvalidNumerator = QuantityParser.classify("1 4/3");
        QuantityClassification mixedFractionInvalidDenominator = QuantityParser.classify("1 1/0");

        assertEquals(QuantityClassification.MIXED_FRACTION, mixedFraction);
        assertEquals(QuantityClassification.INVALID_NUMERATOR, mixedFractionInvalidNumerator);
        assertEquals(QuantityClassification.INVALID_DENOMINATOR, mixedFractionInvalidDenominator);
    }

    @Test
    public void fractionsTests() {
        QuantityClassification fraction = QuantityParser.classify("1/3");
        QuantityClassification fractionInvalidNumerator = QuantityParser.classify("4/3");
        QuantityClassification fractionInvalidDenominator = QuantityParser.classify("1/0");

        assertEquals(QuantityClassification.FRACTION, fraction);
        assertEquals(QuantityClassification.INVALID_NUMERATOR, fractionInvalidNumerator);
        assertEquals(QuantityClassification.INVALID_DENOMINATOR, fractionInvalidDenominator);
    }

    @Test
    public void decimalAndWholeNumberTests() {
        QuantityClassification decimal = QuantityParser.classify("1.003");
        QuantityClassification decimalInvalid = QuantityParser.classify(".003");
        QuantityClassification whole = QuantityParser.classify("1");
        QuantityClassification invalidEmpty = QuantityParser.classify(" ");

        assertEquals(QuantityClassification.DECIMAL_OR_WHOLE, decimal);
        assertEquals(QuantityClassification.INVALID, decimalInvalid);
        assertEquals(QuantityClassification.DECIMAL_OR_WHOLE, whole);
        assertEquals(QuantityClassification.INVALID_EMPTY, invalidEmpty);
    }
}
