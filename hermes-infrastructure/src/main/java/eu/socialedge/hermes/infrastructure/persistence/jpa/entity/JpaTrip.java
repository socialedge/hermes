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
package eu.socialedge.hermes.infrastructure.persistence.jpa.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "trips")
public class JpaTrip {

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trip_stops", joinColumns = @JoinColumn(name = "trip_id"))
    private Set<JpaStop> stops = new HashSet<>();

    public JpaTrip() {}

    public String tripId() {
        return tripId;
    }

    public void tripId(String tripId) {
        this.tripId = tripId;
    }

    public Set<JpaStop> stops() {
        return stops;
    }

    public void stops(SortedSet<JpaStop> stops) {
        this.stops = stops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaTrip)) return false;
        JpaTrip jpaTrip = (JpaTrip) o;
        return Objects.equals(tripId, jpaTrip.tripId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId);
    }
}
