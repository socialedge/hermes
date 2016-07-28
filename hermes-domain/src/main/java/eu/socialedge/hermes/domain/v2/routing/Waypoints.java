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
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a sequence of {@link Waypoint}s that defines
 * a vehicles {@link Route}s.
 */
@ValueObject
public class Waypoints extends AbstractSet<Waypoint>
        implements NavigableSet<Waypoint>, Serializable {

    private final NavigableSet<Waypoint> waypoints;

    public Waypoints() {
        this.waypoints = new TreeSet<>();
    }

    public Waypoints(NavigableSet<Waypoint> waypoints) {
        this.waypoints = notNull(waypoints);
    }

    public Waypoints(Collection<Waypoint> waypoints) {
        this.waypoints = new TreeSet<>(notNull(waypoints));
    }

    private Optional<Waypoint> waypointByStationId(StationId stationId) {
        return this.waypoints.stream()
                .filter(wp -> wp.stationId().equals(stationId))
                .findFirst();
    }

    public void addAfter(StationId predecessor, StationId stationId) {
        Optional<Waypoint> predecessorWpOtp = waypointByStationId(predecessor);

        if (!predecessorWpOtp.isPresent())
            throw new IllegalArgumentException("Waypoint with such station id not found");

        Waypoint predecessorWp = predecessorWpOtp.get();
        addAfter(predecessorWp, stationId);
    }

    public void addAfter(Waypoint predecessor, StationId stationId) {
        SortedSet<Waypoint> successors = this.waypoints.tailSet(notNull(predecessor), false);
        List<Waypoint> shiftedSuccessors = successors.stream()
                .map(wp -> Waypoint.of(wp.stationId(), wp.position() + 1))
                .collect(Collectors.toList());

        successors.clear();
        successors.add(Waypoint.of(stationId, predecessor.position() + 1));
        successors.addAll(shiftedSuccessors);
    }

    public void addBefore(Waypoint successor, StationId stationId) {
        Waypoint predecessor = this.waypoints.headSet(successor, false).last();
        addAfter(predecessor, stationId);
    }

    public void addBefore(StationId successor, StationId stationId) {
        Optional<Waypoint> successorWpOtp = waypointByStationId(successor);

        if (!successorWpOtp.isPresent())
            throw new IllegalArgumentException("Waypoint with such station id not found");

        Waypoint successorWp = successorWpOtp.get();
        addBefore(successorWp, stationId);
    }

    public int indexOf(StationId stationId) {
        for(Waypoint wp : this.waypoints) {
            if (wp.stationId().equals(stationId))
                return wp.position();
        }

        return -1;
    }

    @Override
    public Comparator<? super Waypoint> comparator() {
        return this.waypoints.comparator();
    }

    @Override
    public SortedSet<Waypoint> subSet(Waypoint fromElement, Waypoint toElement) {
        return this.waypoints.subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<Waypoint> headSet(Waypoint toElement) {
        return this.waypoints.headSet(toElement);
    }

    @Override
    public SortedSet<Waypoint> tailSet(Waypoint fromElement) {
        return this.waypoints.tailSet(fromElement);
    }

    @Override
    public Iterator<Waypoint> iterator() {
        return this.waypoints.iterator();
    }

    @Override
    public NavigableSet<Waypoint> descendingSet() {
        return waypoints.descendingSet();
    }

    @Override
    public Iterator<Waypoint> descendingIterator() {
        return waypoints.descendingIterator();
    }

    @Override
    public NavigableSet<Waypoint> subSet(Waypoint fromElement, boolean fromInclusive, Waypoint toElement, boolean toInclusive) {
        return waypoints.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<Waypoint> headSet(Waypoint toElement, boolean inclusive) {
        return waypoints.headSet(toElement, inclusive);
    }

    @Override
    public NavigableSet<Waypoint> tailSet(Waypoint fromElement, boolean inclusive) {
        return waypoints.tailSet(fromElement, inclusive);
    }

    @Override
    public int size() {
        return this.waypoints.size();
    }

    @Override
    public Waypoint first() {
        return this.waypoints.first();
    }

    @Override
    public Waypoint last() {
        return this.waypoints.last();
    }

    @Override
    public Waypoint lower(Waypoint waypoint) {
        return waypoints.lower(waypoint);
    }

    @Override
    public Waypoint floor(Waypoint waypoint) {
        return waypoints.floor(waypoint);
    }

    @Override
    public Waypoint ceiling(Waypoint waypoint) {
        return waypoints.ceiling(waypoint);
    }

    @Override
    public Waypoint higher(Waypoint waypoint) {
        return waypoints.higher(waypoint);
    }

    @Override
    public Waypoint pollFirst() {
        return waypoints.pollFirst();
    }

    @Override
    public Waypoint pollLast() {
        return waypoints.pollLast();
    }
}
