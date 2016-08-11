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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.shared.Identifier;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Represents the short name or code of a {@link Trip} that uniquely
 * identifies it. This will often be a short, abstract identifier like
 * "TRP-R20", "TR-20", or "Green Trip"
 */
@ValueObject
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@Embeddable
@AttributeOverride(name = "value", column = @Column(name = "trip_id"))
public class TripId extends Identifier {

    public TripId(String value) {
        super(value);
    }

    public static TripId of(String value) {
        return new TripId(value);
    }
}
