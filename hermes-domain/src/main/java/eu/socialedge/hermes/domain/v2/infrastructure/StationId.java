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
package eu.socialedge.hermes.domain.v2.infrastructure;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;

/**
 * Represents the short name or code of a {@link Station} that uniquely
 * identifies it. This will often be a short, abstract identifier like
 * "ST15", "FOI15", or "station_15" that riders use to identify a
 * {@link Station}.
 */
@ValueObject
public class StationId implements Serializable {

    private final String value;

    public StationId(String value) {
        this.value = notBlank(value);
    }

    public static StationId of(String value) {
        return new StationId(value);
    }

    public static StationId random() {
        String randomUUID = UUID.randomUUID().toString();
        return new StationId(randomUUID);
    }

    @Override
    public String toString() {
        return value;
    }
}
