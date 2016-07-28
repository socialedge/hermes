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

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.v2.infrastructure.RouteId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a journey taken by a vehicle through Stations. Trips are
 * time-specific — they are defined as a sequence of {@link Stop}s, so
 * a single Trip represents one journey along a transit line or route.
 *
 * <p>In addition to {@link Stop}, Trips use {@link TripAvailability}
 * calendar to define the days when a Trip is available to passengers.</p>
 */
@AggregateRoot
public class Trip {

    private final TripId tripId;

    private final RouteId routeId;

    private Collection<Stop> stops;

    private TripAvailability tripAvailability;

    public Trip(TripId tripId, RouteId routeId, TripAvailability tripAvailability) {
        this.tripId = notNull(tripId);
        this.routeId = notNull(routeId);
        this.tripAvailability = notNull(tripAvailability);
        this.stops = new ArrayList<>();
    }

    public Trip(String tripId, RouteId routeId, TripAvailability tripAvailability) {
        this(TripId.of(tripId), routeId, tripAvailability);
    }

    public Trip(TripId tripId, RouteId routeId,
                TripAvailability tripAvailability, Collection<Stop> stops) {
        this.tripId = notNull(tripId);
        this.routeId = notNull(routeId);
        this.tripAvailability = notNull(tripAvailability);
        this.stops = !isNull(stops) ? stops : Collections.emptySet();
    }

    public Trip(String tripId, RouteId routeId,
                TripAvailability tripAvailability, Collection<Stop> stops) {
        this(TripId.of(tripId), routeId, tripAvailability, stops);
    }

    public TripId tripId() {
        return tripId;
    }

    public RouteId routeId() {
        return routeId;
    }

    public Collection<Stop> stops() {
        return stops;
    }

    public TripAvailability serviceAvailability() {
        return tripAvailability;
    }

    public void serviceAvailability(TripAvailability servicePeriod) {
        this.tripAvailability = notNull(servicePeriod);
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
                ", routeId=" + routeId +
                ", stops=" + stops +
                ", tripAvailability=" + tripAvailability +
                '}';
    }
}
