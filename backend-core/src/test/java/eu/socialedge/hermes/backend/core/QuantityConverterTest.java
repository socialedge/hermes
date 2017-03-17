package eu.socialedge.hermes.backend.core;

import lombok.val;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class QuantityConverterTest {
    private static final String METERS_VALUE = "123 m";
    private static final Quantity QUANTITY_VALUE = Quantities.getQuantity(BigDecimal.valueOf(123), Units.METRE);

    private QuantityConverter converter = new QuantityConverter();

    @Test
    public void shouldConvertToDataBaseColumn() {
        val stringResult = converter.convertToDatabaseColumn(QUANTITY_VALUE);

        assertEquals(METERS_VALUE, stringResult);
    }

    @Test
    public void shouldConvertToEntityAttributeValue() {
        val quantityResult = converter.convertToEntityAttribute(METERS_VALUE);

        assertEquals(QUANTITY_VALUE.getValue(), quantityResult.getValue());
        assertEquals(QUANTITY_VALUE.getUnit(), quantityResult.getUnit());
    }

    @Test
    public void shouldReturnNullForNullAttribute() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void shouldReturnNullForNullDatabaseColumn() {
        assertNull(converter.convertToEntityAttribute(null));
    }
}
