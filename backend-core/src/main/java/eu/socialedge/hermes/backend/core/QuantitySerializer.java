package eu.socialedge.hermes.backend.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import javax.measure.Quantity;
import java.io.IOException;
import java.util.Objects;

public class QuantitySerializer extends StdSerializer<Quantity> {

    public QuantitySerializer() {
        super(Quantity.class);
    }

    @Override
    public void serialize(Quantity value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(Objects.toString(value, null));
    }
}
