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

import eu.socialedge.hermes.domain.shared.Identifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Values.isNotNull;
import static eu.socialedge.hermes.util.Values.requireNotNull;

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
@Entity
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Table(name = "trips")
public class Trip implements Identifiable<TripId> {

    @EmbeddedId
    private final TripId id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trip_stops", joinColumns = @JoinColumn(name = "trip_id"))
    private final Set<Stop> stops;

    public Trip(TripId id) {
        this(id, new HashSet<>());
    }

    public Trip(TripId id, Collection<Stop> stops) {
        this(id, new HashSet<>(stops));
    }

    public Trip(TripId id, Set<Stop> stops) {
        this.id = requireNotNull(id);
        this.stops = isNotNull(stops) ? stops : new HashSet<>();
    }

    @Override
    public TripId id() {
        return id;
    }

    public Set<Stop> stops() {
        return stops;
    }
}
