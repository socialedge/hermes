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

import eu.socialedge.hermes.domain.ext.AggregateRoot;

import java.util.NavigableSet;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a journey taken by a vehicle through sequence of {@link Stop}
 * that define road trip.
 *
 * @see <a href="https://goo.gl/6M5qhC">Google Transit APIs
 * > Static Transit > trips.txt File</a>
 */
@AggregateRoot
public class Trip {

    private final TripId tripId;

    private TripAvailability tripAvailability;

    private final Stops stops;

    public Trip(TripId tripId, TripAvailability tripAvailability) {
        this.tripId = notNull(tripId);
        this.tripAvailability = notNull(tripAvailability);
        this.stops = new Stops();
    }

    public Trip(TripId tripId, TripAvailability tripAvailability, NavigableSet<Stop> stops) {
        this.tripId = notNull(tripId);
        this.tripAvailability = notNull(tripAvailability);
        this.stops = !isNull(stops) ? new Stops(stops) : new Stops();
    }

    public Trip(TripId tripId, TripAvailability tripAvailability, Stops stops) {
        this.tripId = notNull(tripId);
        this.tripAvailability = notNull(tripAvailability);
        this.stops = !isNull(stops) ? stops : new Stops();
    }

    public TripId tripId() {
        return tripId;
    }

    public TripAvailability tripAvailability() {
        return tripAvailability;
    }

    public void tripAvailability(TripAvailability tripAvailability) {
        this.tripAvailability = tripAvailability;
    }

    public Stops stops() {
        return stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip trip = (Trip) o;
        return Objects.equals(tripId, trip.tripId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", tripAvailability=" + tripAvailability +
                ", stops=" + stops +
                '}';
    }
}
