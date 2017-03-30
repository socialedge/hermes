package eu.socialedge.hermes.backend.transit.infrastructure.persistence.jpa.convert;

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
    private static final String KILOGRAM_VALUE = "123 kg";
    private static final Quantity QUANTITY_METERS = Quantities.getQuantity(BigDecimal.valueOf(123), Units.METRE);
    private static final Quantity QUANTITY_KILOGRAM = Quantities.getQuantity(BigDecimal.valueOf(123), Units.KILOGRAM);

    private QuantityConverter converter = new QuantityConverter();

    @Test
    public void shouldConvertToDataBaseColumn() {
        val stringResult = converter.convertToDatabaseColumn(QUANTITY_METERS);

        assertEquals(METERS_VALUE, stringResult);
    }

    @Test
    public void shouldConvertToEntityAttributeValue() {
        val quantityResult = converter.convertToEntityAttribute(METERS_VALUE);

        assertEquals(QUANTITY_METERS.getValue(), quantityResult.getValue());
        assertEquals(QUANTITY_METERS.getUnit(), quantityResult.getUnit());
    }
    @Test
    public void shouldConvertToDataBaseColumnFromAnyUnit() {
        val stringResult = converter.convertToDatabaseColumn(QUANTITY_KILOGRAM);

        assertEquals(KILOGRAM_VALUE, stringResult);
    }

    @Test
    public void shouldConvertToEntityAttributeValueFromAnyUnit() {
        val quantityResult = converter.convertToEntityAttribute(KILOGRAM_VALUE);

        assertEquals(QUANTITY_KILOGRAM.getValue(), quantityResult.getValue());
        assertEquals(QUANTITY_KILOGRAM.getUnit(), quantityResult.getUnit());
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
