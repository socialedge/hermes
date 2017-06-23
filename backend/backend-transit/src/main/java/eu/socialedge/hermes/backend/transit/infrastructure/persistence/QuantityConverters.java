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

package eu.socialedge.hermes.backend.transit.infrastructure.persistence;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

public class QuantityConverters {

    @Component
    public static class QuantityToStringConverter implements Converter<Quantity<?>, String> {

        @Override
        public String convert(Quantity<?> source) {
            return source != null ? source.toString() : null;
        }
    }

    @Component
    public static class StringToQuantity implements Converter<String, Quantity<?>> {
        @Override
        public Quantity<?> convert(String source) {
            return source != null ? Quantities.getQuantity(source) : null;
        }
    }
}
