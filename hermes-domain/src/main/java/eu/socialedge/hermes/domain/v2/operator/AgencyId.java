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
package eu.socialedge.hermes.domain.v2.operator;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * <p>Describes LEI (Legal Entity Identifier) unique ID associated with a single corporate
 * entity in country or world.</p>
 *
 * <p>This implementation doesn't follow ISO-17442 or any other standard, although it imposes
 * some restrictions on LEI length and allowed characters.</p>
 *
 * <ul>
 *     <li><strong>Minimal AgencyId length:</strong> 3 symbols</li>
 *     <li><strong>Pattern:</strong> ^[a-zA-Z0-9]*$</li>
 * </ul>
 */
@ValueObject
public class AgencyId implements Serializable {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]*$");
    private static final int MIN_SYMBOLS = 3;

    private final String value;

    public AgencyId(String value) {
        if (notNull(value).length() < 5)
            throw new IllegalArgumentException("Minimal AgencyId length is " + MIN_SYMBOLS);
        else if (!PATTERN.matcher(value).matches())
            throw new IllegalArgumentException("AgencyId doesn't match pattern = " + PATTERN);

        this.value = value;
    }

    public static AgencyId of(String code) {
        return new AgencyId(code);
    }

    public static AgencyId random() {
        String randomUUID = UUID.randomUUID().toString();
        return new AgencyId(randomUUID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgencyId)) return false;
        AgencyId agencyId1 = (AgencyId) o;
        return Objects.equals(value, agencyId1.value);
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
