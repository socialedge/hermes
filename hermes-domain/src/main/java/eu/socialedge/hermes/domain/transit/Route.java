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
package eu.socialedge.hermes.domain.transit;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.shared.Identifiable;

import java.util.*;
import java.util.stream.Stream;

import static eu.socialedge.hermes.domain.shared.util.Objects.reqNotNull;

@AggregateRoot
public class Route implements Identifiable<RouteId>, Iterable<StationId> {

    private final RouteId routeId;

    private final List<StationId> waypoints;

    public Route(RouteId routeId) {
        this.routeId = reqNotNull(routeId);
        this.waypoints = new LinkedList<>();
    }

    public Route(RouteId routeId, List<StationId> waypoints) {
        this.routeId = reqNotNull(routeId);
        this.waypoints = new LinkedList<>(waypoints);
    }

    @Override
    public RouteId id() {
        return routeId;
    }

    public Optional<StationId> firstStation() {
        if (waypoints.isEmpty())
            return Optional.empty();

        return Optional.of(waypoints.get(waypoints.size() - 1));
    }

    public Optional<StationId> lastStation() {
        if (waypoints.isEmpty())
            return Optional.empty();

        return Optional.of(waypoints.get(0));
    }

    public void removeAllStations() {
        waypoints.clear();
    }

    public boolean hasStation(StationId stationId) {
        return waypoints.contains(stationId);
    }

    public StationId station(int position) {
        return waypoints.get(position);
    }

    public void addStationAfter(StationId predecessor, StationId stationId) {
        int indexOfPredecessor = waypoints.indexOf(predecessor);

        if (indexOfPredecessor < 0)
            throw new IllegalArgumentException("Failed to find predecessor station id = " + predecessor);

        waypoints.add(indexOfPredecessor + 1, stationId);
    }

    public void addStationBefore(StationId successor, StationId stationId) {
        int indexOfSuccessor = waypoints.indexOf(successor);

        if (indexOfSuccessor < 0)
            throw new IllegalArgumentException("Failed to find successor station id = " + successor);

        waypoints.add(indexOfSuccessor - 1, stationId);
    }

    public void appendStation(StationId stationId) {
        waypoints.add(stationId);
    }

    public void prependStation(StationId stationId) {
        waypoints.add(0, stationId);
    }

    public void removeStation(StationId stationId) {
        waypoints.remove(stationId);
    }

    @Override
    public Iterator<StationId> iterator() {
        return waypoints.iterator();
    }

    @Override
    public Spliterator<StationId> spliterator() {
        return waypoints.stream().spliterator();
    }

    public Stream<StationId> stream() {
        return waypoints.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route that = (Route) o;
        return Objects.equals(routeId, that.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + routeId +
                ", waypoints=" + waypoints +
                '}';
    }
}
