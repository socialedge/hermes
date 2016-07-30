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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "trips")
public class JpaTrip {

    @Id
    @Column(name = "trip_id")
    private String tripId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private JpaRoute route;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "waypoints", joinColumns = @JoinColumn(name = "trip_id"))
    private Collection<JpaStop> stops = new HashSet<>();

    @Embedded
    private JpaTripAvailability tripAvailability;

    JpaTrip() {}

    public String tripId() {
        return tripId;
    }

    public void tripId(String tripId) {
        this.tripId = tripId;
    }

    public JpaRoute route() {
        return route;
    }

    public void route(JpaRoute route) {
        this.route = route;
    }

    public Collection<JpaStop> stops() {
        return stops;
    }

    public void stops(Collection<JpaStop> stops) {
        this.stops = stops;
    }

    public JpaTripAvailability tripAvailability() {
        return tripAvailability;
    }

    public void tripAvailability(JpaTripAvailability tripAvailability) {
        this.tripAvailability = tripAvailability;
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
