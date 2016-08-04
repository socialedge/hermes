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
package eu.socialedge.hermes.domain.transit;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.shared.Identifier;

/**
 * Represents the short name of a {@link Line} that uniquely identifies
 * it. This will often be a short, abstract identifier like "32",
 * "100X", or "Green" that riders use to identify a {@link Line}.
 */
@ValueObject
public class LineId extends Identifier {

    public LineId(String value) {
        super(value);
    }

    public static LineId of(String value) {
        return new LineId(value);
    }
}
