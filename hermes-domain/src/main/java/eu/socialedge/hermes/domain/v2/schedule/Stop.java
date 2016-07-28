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
package eu.socialedge.hermes.domain.v2.schedule;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.v2.routing.Waypoint;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Defines when a vehicle arrives at a location, how long it stays
 * there, and when it departs. StopTimes define schedule of {@link Trip}s.
 */
@ValueObject
public class Stop implements Serializable {

    private final Waypoint waypoint;
    private final LocalTime arrival;
    private final LocalTime departure;

    public Stop(Waypoint waypoint, LocalTime arrival, LocalTime departure) {
        this.waypoint = notNull(waypoint);
        this.arrival = notNull(arrival);
        this.departure = notNull(departure);
    }

    public Waypoint waypoint() {
        return waypoint;
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
        return Objects.equals(waypoint, stop.waypoint) &&
                Objects.equals(arrival, stop.arrival) &&
                Objects.equals(departure, stop.departure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(waypoint, arrival, departure);
    }

    @Override
    public String toString() {
        return "Stop{" +
                "waypoint=" + waypoint +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
