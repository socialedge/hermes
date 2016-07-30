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
import eu.socialedge.hermes.domain.v2.infrastructure.StationId;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a stop on the {@link Trip} and it position
 * in relation to others.
 */
@ValueObject
public class Stop implements Comparable<Stop>, Serializable {

    private final StationId stationId;

    private final LocalTime arrival;
    private final LocalTime departure;

    private final int position;

    public Stop(StationId stationId, LocalTime arrival, LocalTime departure, int position) {
        if (position < 0)
            throw new IllegalArgumentException("Position must be greater than 0");

        this.arrival = notNull(arrival);
        this.departure = notNull(departure);
        this.stationId = notNull(stationId);
        this.position = position;
    }

    public static Stop of(StationId stationId, LocalTime arrival, LocalTime departure, int position) {
        return new Stop(stationId, arrival, departure, position);
    }

    public StationId stationId() {
        return stationId;
    }

    public LocalTime arrival() {
        return arrival;
    }

    public LocalTime departure() {
        return departure;
    }

    public int position() {
        return position;
    }

    @Override
    public int compareTo(Stop o) {
        return Integer.compare(this.position(), o.position());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop stop = (Stop) o;
        return position == stop.position &&
                Objects.equals(stationId, stop.stationId) &&
                Objects.equals(arrival, stop.arrival) &&
                Objects.equals(departure, stop.departure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, arrival, departure, position);
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stationId=" + stationId +
                ", arrival=" + arrival +
                ", departure=" + departure +
                ", position=" + position +
                '}';
    }
}
