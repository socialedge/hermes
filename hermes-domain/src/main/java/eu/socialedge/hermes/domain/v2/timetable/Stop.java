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
package eu.socialedge.hermes.domain.v2.timetable;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.v2.infrastructure.StationId;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a stop on the {@link Trip} and defines when a
 * vehicle arrives at a location, how long it stays there,
 * and when it departs.
 */
@ValueObject
public class Stop implements Serializable {

    private final StationId stationId;

    private final LocalTime arrival;
    private final LocalTime departure;

    public Stop(StationId stationId, LocalTime arrival, LocalTime departure) {
        this.arrival = notNull(arrival);
        this.departure = notNull(departure);
        this.stationId = notNull(stationId);
    }

    public static Stop of(StationId stationId, LocalTime arrival, LocalTime departure) {
        return new Stop(stationId, arrival, departure);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stop)) return false;
        Stop stop = (Stop) o;
        return Objects.equals(stationId, stop.stationId) &&
                Objects.equals(arrival, stop.arrival) &&
                Objects.equals(departure, stop.departure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId, arrival, departure);
    }

    @Override
    public String toString() {
        return "{" +
                "stationId=" + stationId +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
