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
import eu.socialedge.hermes.domain.v2.infrastructure.StationId;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a stop on the {@link Route} and it position
 * in relation to others.
 */
@ValueObject
public class Waypoint implements Comparable<Waypoint>, Serializable {

    private final StationId stationId;

    private final int position;

    public Waypoint(StationId stationId, int position) {
        if (position < 0)
            throw new IllegalArgumentException("Position must be greater than 0");

        this.stationId = notNull(stationId);
        this.position = position;
    }

    public static Waypoint of(StationId stationId, int position) {
        return new Waypoint(stationId, position);
    }

    public StationId stationId() {
        return stationId;
    }

    public int position() {
        return position;
    }

    @Override
    public int compareTo(Waypoint o) {
        return Integer.compare(this.position, o.position());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint waypoint = (Waypoint) o;
        return position == waypoint.position && Objects.equals(stationId, waypoint.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, position);
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "stationId=" + stationId +
                ", position=" + position +
                '}';
    }
}
