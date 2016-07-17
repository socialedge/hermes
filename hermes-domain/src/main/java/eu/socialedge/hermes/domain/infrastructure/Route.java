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
package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@AggregateRoot
@Table(name = "routes")
public class Route implements Serializable {
    @Id
    @Column(name = "route_code")
    private String codeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "waypoints", joinColumns = @JoinColumn(name = "route_id"))
    private Set<Waypoint> waypoints = new HashSet<>();

    protected Route() {}

    public Route(String codeId) {
        this.codeId = Validate.notBlank(codeId);
    }

    public Route(String codeId, Set<Waypoint> waypoints) {
        this(codeId);
        this.waypoints = Validate.notEmpty(waypoints);
    }

    public String getCodeId() {
        return codeId;
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Optional<Waypoint> getWaypoint(int orderPosition) {
        return this.waypoints.stream().filter(w -> w.getPosition() == orderPosition).findFirst();
    }

    public Waypoint appendWaypoint(Station station) {
        Validate.notNull(station);

        Optional<Waypoint> maxOrderWaypointOpt = this.waypoints.stream().max(Waypoint::compareTo);
        int maxOrder = maxOrderWaypointOpt.isPresent() ? maxOrderWaypointOpt.get().getPosition() : 0;
        Waypoint wp = new Waypoint(station, ++maxOrder);

        this.waypoints.add(wp);
        return wp;
    }

    public Waypoint prependWaypoint(Station station) {
        return insertWaypoint(station, 1);
    }

    public Waypoint insertWaypoint(Station station, int orderPosition) {
        Validate.notNull(station);

        List<Waypoint> shiftedWaypoints = new ArrayList<>();

        for (Waypoint wp : this.waypoints){
            int wpOrderPosition = wp.getPosition();
            if (wpOrderPosition >= orderPosition) {
                shiftedWaypoints.add(Waypoint.of(wp.getStation(), ++wpOrderPosition));
                this.waypoints.remove(wp);
            }
        }

        if (shiftedWaypoints.size() == 0)
            return appendWaypoint(station);

        Waypoint wp = Waypoint.of(station, orderPosition);

        this.waypoints.add(wp);
        this.waypoints.addAll(shiftedWaypoints);

        return wp;
    }

    public boolean removeWaypoint(Waypoint waypoint) {
        Validate.notNull(waypoint);

        int oldWpOrderPosition = waypoint.getPosition();
        List<Waypoint> shiftedWaypoints = new ArrayList<>();

        if (this.waypoints.remove(waypoint)) {
            this.waypoints.forEach(wp -> {
                int wpOrderPosition = wp.getPosition();
                if (wpOrderPosition > oldWpOrderPosition) {
                    shiftedWaypoints.add(new Waypoint(wp.getStation(), --wpOrderPosition));
                    this.waypoints.remove(wp);
                }
            });
        } else {
            return false;
        }

        this.waypoints.addAll(shiftedWaypoints);
        return true;
    }

    public boolean removeWaypoint(Station station) {
        Validate.notNull(station);
        Optional<Waypoint> waypointOpt = this.waypoints.stream().filter(w -> w.getStation().equals(station)).findFirst();
        return waypointOpt.isPresent() && removeWaypoint(waypointOpt.get());
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = Validate.notEmpty(waypoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(getCodeId(), route.getCodeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCodeId());
    }

    @Override
    public String toString() {
        return "Route{" +
                "codeId='" + codeId + '\'' +
                ", waypoints=" + waypoints +
                '}';
    }
}
