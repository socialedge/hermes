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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@Entity
@AggregateRoot
@Table(name = "routes")
public class Route implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private int routeId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @NotNull
    @Size(min = 3)
    @Column(name = "code")
    private String code;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "waypoints", joinColumns = @JoinColumn(name = "route_id"))
    private Set<Waypoint> waypoints = new HashSet<>();

    Route() {}

    public Route(String code) {
        this.code = code;
    }

    public Route(String code, Set<Waypoint> waypoints) {
        this.code = code;
        this.waypoints = waypoints;
    }

    public int getRouteId() {
        return routeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Optional<Waypoint> getWaypoint(int orderPosition) {
        return this.waypoints.stream().filter(w -> w.getPosition() == orderPosition).findFirst();
    }

    public Waypoint appendWaypoint(Station station) {
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
        Objects.requireNonNull(waypoint);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return Objects.equals(line, route.line) &&
               Objects.equals(code, route.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, code);
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId=" + routeId +
                ", line=" + line +
                ", code='" + code + '\'' +
                ", waypoints=" + waypoints +
                '}';
    }

}
