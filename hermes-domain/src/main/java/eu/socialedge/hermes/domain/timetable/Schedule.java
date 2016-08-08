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

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.*;
import java.util.stream.Stream;

import static eu.socialedge.hermes.domain.shared.util.Values.requireNotNull;

/**
 * Schedules define timetables of vehicle {@link Trip}s for a
 * defined {@link eu.socialedge.hermes.domain.transit.Route}.
 *
 * <p>Every schedule is effective only on certain days, defined
 * by {@link ScheduleAvailability}.</p>
 */
@AggregateRoot
public class Schedule implements Identifiable<ScheduleId>, Iterable<Trip> {

    private final ScheduleId scheduleId;

    private final RouteId routeId;

    private ScheduleAvailability scheduleAvailability;

    private final Set<Trip> trips;

    public Schedule(ScheduleId scheduleId, RouteId routeId,
                    ScheduleAvailability scheduleAvailability) {
        this(scheduleId, routeId, scheduleAvailability, new HashSet<>());
    }

    public Schedule(ScheduleId scheduleId, RouteId routeId,
                    ScheduleAvailability scheduleAvailability, Collection<Trip> trips) {
        this(scheduleId, routeId, scheduleAvailability, new HashSet<>(trips));
    }

    public Schedule(ScheduleId scheduleId, RouteId routeId,
                    ScheduleAvailability scheduleAvailability, Set<Trip> trips) {
        this.scheduleId = requireNotNull(scheduleId);
        this.routeId = requireNotNull(routeId);
        this.scheduleAvailability = requireNotNull(scheduleAvailability);
        this.trips = requireNotNull(trips);
    }

    @Override
    public ScheduleId id() {
        return scheduleId;
    }

    public RouteId routeId() {
        return routeId;
    }

    public ScheduleAvailability scheduleAvailability() {
        return scheduleAvailability;
    }

    public void scheduleAvailability(ScheduleAvailability scheduleAvailability) {
        this.scheduleAvailability = requireNotNull(scheduleAvailability);
    }

    public boolean hasTrip(Trip trip) {
        return this.trips.contains(trip);
    }

    public void addTrip(Trip trip) {
        this.trips.add(trip);
    }

    public void removeTrip(Trip trip) {
        this.trips.remove(trip);
    }

    public void removeAllTrips() {
        this.trips.clear();
    }

    public boolean isEmpty() {
        return this.trips.isEmpty();
    }

    @Override
    public Iterator<Trip> iterator() {
        return trips.iterator();
    }

    @Override
    public Spliterator<Trip> spliterator() {
        return trips.spliterator();
    }

    public Stream<Trip> stream() {
        return trips.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule trips = (Schedule) o;
        return Objects.equals(scheduleId, trips.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + scheduleId +
                ", scheduleAvailability=" + scheduleAvailability +
                ", trips=" + trips +
                '}';
    }
}
