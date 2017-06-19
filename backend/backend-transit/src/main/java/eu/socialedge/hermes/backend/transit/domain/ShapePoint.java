/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import static org.apache.commons.lang3.Validate.notNull;

@Deprecated
@Document
@EqualsAndHashCode @ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class ShapePoint {

    @Getter
    private Location location;

    @Getter
    private Quantity<Length> distanceTraveled;

    public ShapePoint(Location location, Quantity<Length> distanceTraveled) {
        this.location = notNull(location);
        this.distanceTraveled = notNull(distanceTraveled);
    }
}
