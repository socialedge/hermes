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
import eu.socialedge.hermes.domain.v2.infrastructure.StationId;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@AggregateRoot
public class Route implements Iterable<StationId> {

    private static final int INITIAL_WP_POSITION = 0;

    private final RouteId routeId;

    private final NavigableSet<Waypoint> waypoints;

    public Route(RouteId routeId) {
        this.routeId = notNull(routeId);
        this.waypoints = new TreeSet<>();
    }

    public Route(RouteId routeId, Collection<StationId> waypoints) {
        this(routeId);
        refillWaypointsWithStationIds(notEmpty(waypoints));
    }

    public RouteId routeId() {
        return routeId;
    }

    public Optional<StationId> firstStation() {
        try {
            return Optional.of(waypoints.first().stationId);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public Optional<StationId> lastStation() {
        try {
            return Optional.of(waypoints.last().stationId);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    public void removeAllStations() {
        waypoints.clear();
    }

    public boolean hasStation(StationId stationId) {
        if (stationId == null)
            return false;

        return findWaypointByStationId(stationId).isPresent();
    }

    public Optional<StationId> station(int position) {
        if (position < 0)
            throw new IllegalArgumentException("Position must be bigger than 0");

        return waypoints.stream()
                .filter(wp -> wp.position == position)
                .map(wp -> wp.stationId)
                .findFirst();
    }

    public void addStationAfter(StationId predecessor, StationId stationId) {
        Optional<Waypoint> predecessorWpOpt = findWaypointByStationId(predecessor);

        if (!predecessorWpOpt.isPresent())
            throw new IllegalArgumentException("Failed to find predecessor station id = " + predecessor);

        Waypoint predecessorWp = predecessorWpOpt.get();
        SortedSet<Waypoint> successorWps = waypoints.tailSet(predecessorWp);

        if (successorWps.isEmpty()) { // append
            appendStation(stationId);
        } else {
            List<Waypoint> shiftedSuccessorWps = successorWps.stream()
                    .map(wp -> new Waypoint(wp.stationId, wp.position + 1))
                    .collect(Collectors.toList());

            successorWps.clear();
            successorWps.add(new Waypoint(stationId, predecessorWp.position + 1));
            successorWps.addAll(shiftedSuccessorWps);
        }
    }

    public void addStationBefore(StationId successor, StationId stationId) {
        Optional<Waypoint> successorWpOpt = findWaypointByStationId(successor);

        if (!successorWpOpt.isPresent())
            throw new IllegalArgumentException("Failed to find successor station id = " + successor);

        Waypoint successorWp = successorWpOpt.get();
        SortedSet<Waypoint> successorPredecessors = this.waypoints.headSet(successorWp);

        if (successorPredecessors.isEmpty()) { // prepend
            prependStation(stationId);
        } else {
            addStationAfter(successorPredecessors.last().stationId, stationId);
        }
    }

    public void appendStation(StationId stationId) {
        if (waypoints.isEmpty()) {
            waypoints.add(new Waypoint(stationId, INITIAL_WP_POSITION));
        } else {
            int stationPos = waypoints.last().position + 1;
            waypoints.add(new Waypoint(stationId, stationPos));
        }
    }

    public void prependStation(StationId stationId) {
        if (waypoints.isEmpty()) {
            appendStation(stationId);
        } else {
            Collection<Waypoint> waypointsClone = new ArrayList<>(waypoints);

            waypoints.clear();
            waypoints.add(new Waypoint(stationId, INITIAL_WP_POSITION));

            int wpPointer = INITIAL_WP_POSITION + 1;
            for (Waypoint clone : waypointsClone)
                waypoints.add(new Waypoint(clone.stationId, wpPointer++));
        }
    }

    public void removeStation(StationId stationId) {
        Optional<Waypoint> waypointToRemoveOpt = findWaypointByStationId(stationId);

        if (!waypointToRemoveOpt.isPresent())
            throw new IllegalArgumentException("No such station found. Station id = " + stationId);

        Waypoint waypointToRemove = waypointToRemoveOpt.get();
        SortedSet<Waypoint> outOfDateWaypoints = this.waypoints.tailSet(waypointToRemove, false);

        List<StationId> stationIdsToReAdd = outOfDateWaypoints.stream()
                .map(wp -> wp.stationId)
                .collect(Collectors.toList());

        waypoints.remove(waypointToRemove);
        outOfDateWaypoints.clear();

        stationIdsToReAdd.forEach(this::appendStation);
    }

    private Optional<Waypoint> findWaypointByStationId(StationId stationId) {
        return waypoints.stream().filter(wp -> wp.stationId.equals(stationId)).findFirst();
    }

    private void refillWaypointsWithStationIds(Collection<StationId> stationIds) {
        waypoints.clear();

        int posPointer = INITIAL_WP_POSITION;
        for (StationId stationId : stationIds) {
            waypoints.add(new Waypoint(stationId, posPointer++));
        }
    }

    @Override
    public Iterator<StationId> iterator() {
        return waypoints.stream().map(wp -> wp.stationId).iterator();
    }

    @Override
    public Spliterator<StationId> spliterator() {
        return waypoints.stream().map(wp -> wp.stationId).spliterator();
    }

    public Stream<StationId> stream() {
        return waypoints.stream().map(wp -> wp.stationId);
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
                "routeId=" + routeId +
                ", waypoints=" + waypoints +
                '}';
    }

    private static class Waypoint implements Comparable<Waypoint>, Serializable {
        final StationId stationId;
        final int position;

        Waypoint(StationId stationId, int position) {
            if (position < 0)
                throw new IllegalArgumentException("Position must be greater than 0");

            this.stationId = notNull(stationId);
            this.position = position;
        }

        @Override
        public int compareTo(Waypoint o) {
            return Integer.compare(this.position, o.position);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Waypoint)) return false;
            Waypoint stop = (Waypoint) o;
            return position == stop.position &&
                    Objects.equals(stationId, stop.stationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stationId, position);
        }

        @Override
        public String toString() {
            return "{" +
                    "stationId=" + stationId +
                    ", position=" + position +
                    '}';
        }
    }
}
