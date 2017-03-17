package eu.socialedge.hermes.backend.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.val;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.io.IOException;

public class QuantityDeserializer extends StdDeserializer<Quantity> {

    public QuantityDeserializer() {
        super(Quantity.class);
    }

    @Override
    public Quantity deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        val quantityString = parser.getText();

        try {
            return Quantities.getQuantity(quantityString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse quantity value " + quantityString, e);
        }
    }
}
