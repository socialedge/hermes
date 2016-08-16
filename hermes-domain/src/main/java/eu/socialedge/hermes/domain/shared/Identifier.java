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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static eu.socialedge.hermes.util.Strings.requireNotBlank;

/**
 * Represents the short name or code of an Entity that uniquely
 * identifies it.
 */
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@MappedSuperclass @Access(AccessType.FIELD)
public abstract class Identifier implements Serializable {

    private final String value;

    public Identifier(String value) {
        this.value = requireNotBlank(value);
    }

    protected String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
