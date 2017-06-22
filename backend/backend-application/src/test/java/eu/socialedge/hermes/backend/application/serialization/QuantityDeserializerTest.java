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

package eu.socialedge.hermes.backend.application.serialization;

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
