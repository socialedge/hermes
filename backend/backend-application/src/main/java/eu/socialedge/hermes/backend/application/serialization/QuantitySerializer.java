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
