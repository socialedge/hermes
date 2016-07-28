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
package eu.socialedge.hermes.domain.v2.infrastructure;

import eu.socialedge.hermes.domain.ext.AggregateRoot;

import java.util.NavigableSet;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a journey taken by a vehicle through sequence of {@link Waypoint}
 * that define road trip.
 */
@AggregateRoot
public class Route {

    private final RouteId routeId;

    private final Waypoints waypoints;

    public Route(RouteId routeId) {
        this.routeId = notNull(routeId);
        this.waypoints = new Waypoints();
    }

    public Route(String routeId) {
        this(RouteId.of(routeId));
    }

    public Route(RouteId routeId, NavigableSet<Waypoint> waypoints) {
        this.routeId = notNull(routeId);
        this.waypoints = new Waypoints(notNull(waypoints));
    }

    public Route(String routeId, NavigableSet<Waypoint> waypoints) {
        this(RouteId.of(routeId), waypoints);
    }

    public Route(RouteId routeId, Waypoints waypoints) {
        this.routeId = notNull(routeId);
        this.waypoints = notNull(waypoints);
    }

    public Route(String routeId, Waypoints waypoints) {
        this(RouteId.of(routeId), waypoints);
    }

    public RouteId routeId() {
        return routeId;
    }

    public Waypoints waypoints() {
        return new Waypoints(waypoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(routeId, route.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }
}
