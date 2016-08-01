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
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents a sequence of {@link Stop}s that defines
 * a vehicles {@link Trip}s.
 *
 * @see <a href="https://goo.gl/AMnYYy">Google Transit APIs
 * > Static Transit > stop_times.txt File</a>
 */
@ValueObject
public class Stops extends AbstractSet<Stop>
        implements NavigableSet<Stop>, Serializable {

    private final NavigableSet<Stop> stops;

    public Stops() {
        this.stops = new TreeSet<>();
    }

    public Stops(NavigableSet<Stop> stops) {
        this.stops = notNull(stops);
    }

    public Stops(Collection<Stop> stops) {
        this.stops = new TreeSet<>(notNull(stops));
    }

    private Optional<Stop> waypointByStationId(StationId stationId) {
        return this.stops.stream()
                .filter(wp -> wp.stationId().equals(stationId))
                .findFirst();
    }

    public void addAfter(StationId predecessor, StationId stationId, LocalTime arrival, LocalTime departure) {
        Optional<Stop> predecessorWpOtp = waypointByStationId(predecessor);

        if (!predecessorWpOtp.isPresent())
            throw new IllegalArgumentException("Stop with such station id not found");

        Stop predecessorWp = predecessorWpOtp.get();
        addAfter(predecessorWp, stationId, arrival, departure);
    }

    public void addAfter(Stop predecessor, StationId stationId, LocalTime arrival, LocalTime departure) {
        SortedSet<Stop> successors = this.stops.tailSet(notNull(predecessor), false);
        List<Stop> shiftedSuccessors = successors.stream()
                .map(st -> Stop.of(st.stationId(),
                                   st.arrival(),
                                   st.departure(),
                                   st.position() + 1))
                .collect(Collectors.toList());

        successors.clear();
        successors.add(Stop.of(stationId, arrival, departure, predecessor.position() + 1));
        successors.addAll(shiftedSuccessors);
    }

    public void addBefore(Stop successor, StationId stationId, LocalTime arrival, LocalTime departure) {
        Stop predecessor = this.stops.headSet(successor, false).last();
        addAfter(predecessor, stationId, arrival, departure);
    }

    public void addBefore(StationId successor, StationId stationId, LocalTime arrival, LocalTime departure) {
        Optional<Stop> successorWpOtp = waypointByStationId(successor);

        if (!successorWpOtp.isPresent())
            throw new IllegalArgumentException("Stop with such station id not found");

        Stop successorWp = successorWpOtp.get();
        addBefore(successorWp, stationId, arrival, departure);
    }

    public int indexOf(StationId stationId) {
        for(Stop wp : this.stops) {
            if (wp.stationId().equals(stationId))
                return wp.position();
        }

        return -1;
    }

    @Override
    public Comparator<? super Stop> comparator() {
        return this.stops.comparator();
    }

    @Override
    public SortedSet<Stop> subSet(Stop fromElement, Stop toElement) {
        return this.stops.subSet(fromElement, toElement);
    }

    @Override
    public SortedSet<Stop> headSet(Stop toElement) {
        return this.stops.headSet(toElement);
    }

    @Override
    public SortedSet<Stop> tailSet(Stop fromElement) {
        return this.stops.tailSet(fromElement);
    }

    @Override
    public Iterator<Stop> iterator() {
        return this.stops.iterator();
    }

    @Override
    public NavigableSet<Stop> descendingSet() {
        return stops.descendingSet();
    }

    @Override
    public Iterator<Stop> descendingIterator() {
        return stops.descendingIterator();
    }

    @Override
    public NavigableSet<Stop> subSet(Stop fromElement, boolean fromInclusive, Stop toElement, boolean toInclusive) {
        return stops.subSet(fromElement, fromInclusive, toElement, toInclusive);
    }

    @Override
    public NavigableSet<Stop> headSet(Stop toElement, boolean inclusive) {
        return stops.headSet(toElement, inclusive);
    }

    @Override
    public NavigableSet<Stop> tailSet(Stop fromElement, boolean inclusive) {
        return stops.tailSet(fromElement, inclusive);
    }

    @Override
    public int size() {
        return this.stops.size();
    }

    @Override
    public Stop first() {
        return this.stops.first();
    }

    @Override
    public Stop last() {
        return this.stops.last();
    }

    @Override
    public Stop lower(Stop stop) {
        return stops.lower(stop);
    }

    @Override
    public Stop floor(Stop stop) {
        return stops.floor(stop);
    }

    @Override
    public Stop ceiling(Stop stop) {
        return stops.ceiling(stop);
    }

    @Override
    public Stop higher(Stop stop) {
        return stops.higher(stop);
    }

    @Override
    public Stop pollFirst() {
        return stops.pollFirst();
    }

    @Override
    public Stop pollLast() {
        return stops.pollLast();
    }
}
