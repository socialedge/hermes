package eu.socialedge.hermes.backend.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class QuantityDeserializerTest {

    private ObjectMapper mapper;
    private QuantityDeserializer deserializer;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
        deserializer = new QuantityDeserializer();
    }

    @Test
    public void shouldDeserializeFromStringToCorrectQuantity() throws Exception {
        val json = "{\"value\": \"1123 m\"";
        val result = deserializeQuantity(json);

        assertEquals(1123, result.getValue().intValue());
        assertEquals(Units.METRE, result.getUnit());
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowExceptionForNullInput() throws Exception {
        val json = "{\"value\": null";
        deserializeQuantity(json);
    }

    @Test(expected = NumberFormatException.class)
    public void shouldThrowExceptionForInvalidInput() throws Exception {
        val json = "{\"value\": \"some input\"";
        deserializeQuantity(json);
    }

    private Quantity deserializeQuantity(String json) throws Exception {
        val stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        val parser = mapper.getFactory().createParser(stream);
        val context = mapper.getDeserializationContext();
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        return deserializer.deserialize(parser, context);
    }
}
