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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.io.IOException;

public class QuantityDeserializer extends StdDeserializer<Quantity> {

    public QuantityDeserializer() {
        super(Quantity.class);
    }

    @Override
    public Quantity deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Quantities.getQuantity(parser.getText());
    }
}
