/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.domain.v2.routing;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;

/**
 * Represents the short name or code of a {@link Route} that uniquely
 * identifies it. This will often be a short, abstract identifier like
 * "LN1-R20", "R1-20", or "Green Route" that riders use to identify
 * a {@link Route}.
 */
@ValueObject
public class RouteId implements Serializable {
    private final String value;

    public RouteId(String value) {
        this.value = notBlank(value);
    }

    public static RouteId of(String value) {
        return new RouteId(value);
    }

    public static RouteId random() {
        String randomUUID = UUID.randomUUID().toString();
        return new RouteId(randomUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteId)) return false;
        RouteId routeId = (RouteId) o;
        return Objects.equals(value, routeId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
