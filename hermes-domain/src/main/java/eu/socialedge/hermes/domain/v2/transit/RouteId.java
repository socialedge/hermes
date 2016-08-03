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
package eu.socialedge.hermes.domain.v2.transit;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.v2.shared.EntityCode;

/**
 * Represents the short name or code of a {@link Route} that uniquely
 * identifies it. This will often be a short, abstract identifier like
 * "LN1-R20", "R1-20", or "Green Route" that riders use to identify
 * a {@link Route}.
 */
@ValueObject
public class RouteId extends EntityCode {

    public RouteId(String value) {
        super(value);
    }

    public static RouteId of(String value) {
        return new RouteId(value);
    }
}
