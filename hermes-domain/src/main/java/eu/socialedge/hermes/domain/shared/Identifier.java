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
package eu.socialedge.hermes.domain.shared;

import java.io.Serializable;
import java.util.Objects;

import static eu.socialedge.hermes.domain.shared.util.Strings.reqNotBlank;

/**
 * Represents the short name or code of an Entity that uniquely
 * identifies it.
 */
public abstract class Identifier implements Serializable {

    private final String value;

    public Identifier(String value) {
        this.value = reqNotBlank(value);
    }

    protected String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o.getClass().isAssignableFrom(String.class)) {
            String that = (String) o;
            return Objects.equals(value, that);
        } else if (o instanceof Identifier) {
            Identifier that = (Identifier) o;
            return Objects.equals(value, that.value);
        } else {
            return false;
        }
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
