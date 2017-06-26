/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package eu.socialedge.hermes.backend.transit.infrastructure.persistence;

import lombok.val;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QuantityConvertersTest {
    private static final String METERS_VALUE = "123 m";
    private static final String KILOGRAM_VALUE = "123 kg";
    private static final Quantity QUANTITY_METERS = Quantities.getQuantity(BigDecimal.valueOf(123), Units.METRE);
    private static final Quantity QUANTITY_KILOGRAM = Quantities.getQuantity(BigDecimal.valueOf(123), Units.KILOGRAM);

    private Converter<Quantity<?>, String> toString = new QuantityConverters.QuantityToStringConverter();
    private Converter<String, Quantity<?>> fromString = new QuantityConverters.StringToQuantity();

    @Test
    public void shouldConvertToDataBaseColumn() {
        val stringResult = toString.convert((QUANTITY_METERS));

        assertEquals(METERS_VALUE, stringResult);
    }

    @Test
    public void shouldConvertToEntityAttributeValue() {
        val quantityResult = fromString.convert(METERS_VALUE);

        assertEquals(QUANTITY_METERS.getValue(), quantityResult.getValue());
        assertEquals(QUANTITY_METERS.getUnit(), quantityResult.getUnit());
    }
    @Test
    public void shouldConvertToDataBaseColumnFromAnyUnit() {
        val stringResult = toString.convert(QUANTITY_KILOGRAM);

        assertEquals(KILOGRAM_VALUE, stringResult);
    }

    @Test
    public void shouldConvertToEntityAttributeValueFromAnyUnit() {
        val quantityResult = fromString.convert(KILOGRAM_VALUE);

        assertEquals(QUANTITY_KILOGRAM.getValue(), quantityResult.getValue());
        assertEquals(QUANTITY_KILOGRAM.getUnit(), quantityResult.getUnit());
    }

    @Test
    public void shouldReturnNullForNullAttribute() {
        assertNull(toString.convert(null));
    }

    @Test
    public void shouldReturnNullForNullDatabaseColumn() {
        assertNull(fromString.convert(null));
    }
}
