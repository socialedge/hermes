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

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.util.*;
import java.util.stream.Stream;

import static eu.socialedge.hermes.domain.shared.util.Objects.requireNotNull;

/**
 * Describes one run/journey taken by a vehicle according to
 * defined {@link eu.socialedge.hermes.domain.transit.Route}
 * through sequence of {@link Stop}s.
 *
 * <p>Trips are time-specific — they are defined as a sequence
 * of {@link Stop}s, so a single Trip represents one journey
 * along a transit route</p>
 *
 * @see <a href="https://goo.gl/6M5qhC">Google Transit APIs
 * > Static Transit > trips.txt File</a>
 */
@ValueObject
public class Trip implements Iterable<Stop> {

    private final Set<Stop> stops;

    public Trip() {
        this(new HashSet<>());
    }

    public Trip(Collection<Stop> stops) {
        this(new HashSet<>(stops));
    }

    public Trip(Set<Stop> stops) {
        this.stops = requireNotNull(stops);
    }

    public boolean hasStop(Stop stop) {
        return stops.contains(stop);
    }

    public boolean addStop(Stop stop) {
        return stops.add(stop);
    }

    public boolean removeStop(Stop stop) {
        return stops.remove(stop);
    }

    public void removeAllStops() {
        stops.clear();
    }

    public int size() {
        return stops.size();
    }

    public boolean isEmpty() {
        return stops.isEmpty();
    }

    @Override
    public Iterator<Stop> iterator() {
        return stops.iterator();
    }

    @Override
    public Spliterator<Stop> spliterator() {
        return stops.spliterator();
    }

    public Stream<Stop> stream() {
        return stops.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip stops1 = (Trip) o;
        return Objects.equals(stops, stops1.stops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stops);
    }

    @Override
    public String toString() {
        return "{" +
                "stops=" + stops +
                '}';
    }
}
