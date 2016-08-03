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

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "routes")
public class JpaRoute {

    @Id
    @Column(name = "route_id")
    private String routeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_waypoints", joinColumns = @JoinColumn(name = "route_id"))
    @OrderBy("position")
    private SortedSet<JpaWaypoint> waypoints = new TreeSet<>();

    public JpaRoute() {}

    public String routeId() {
        return routeId;
    }

    public void routeId(String routeId) {
        this.routeId = routeId;
    }

    public SortedSet<JpaWaypoint> waypoints() {
        return waypoints;
    }

    public void waypoints(SortedSet<JpaWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaRoute)) return false;
        JpaRoute jpaRoute = (JpaRoute) o;
        return Objects.equals(routeId, jpaRoute.routeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeId);
    }
}
