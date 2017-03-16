package eu.socialedge.hermes.backend.core;

import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class QuantityConverter implements AttributeConverter<Quantity, String> {

    @Override
    public String convertToDatabaseColumn(Quantity attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Quantity convertToEntityAttribute(String dbData) {
        return dbData != null ? Quantities.getQuantity(dbData) : null;
    }
}
