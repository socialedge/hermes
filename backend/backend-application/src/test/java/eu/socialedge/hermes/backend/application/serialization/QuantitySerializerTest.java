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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Before;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class QuantitySerializerTest {

    private Writer jsonWriter;
    private JsonGenerator jsonGenerator;
    private SerializerProvider serializerProvider;

    private final QuantitySerializer serializer = new QuantitySerializer();

    @Before
    public void setUp() throws Exception {
        jsonWriter = new StringWriter();
        jsonGenerator = new JsonFactory().createGenerator(jsonWriter);
        serializerProvider = new ObjectMapper().getSerializerProvider();
    }

    @Test
    public void shouldSerializeToStringRepresentation() throws Exception {
        serializer.serialize(Quantities.getQuantity(10, Units.METRE), jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        assertEquals("\"10 m\"", jsonWriter.toString());
    }

    @Test
    public void shouldSerializeNullAsNull() throws Exception {
        serializer.serialize(null, jsonGenerator, serializerProvider);
        jsonGenerator.flush();

        assertEquals("null", jsonWriter.toString());
    }
}
