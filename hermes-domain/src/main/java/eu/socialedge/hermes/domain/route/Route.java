/**
 * Hermes - a Public Transport Management System
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
package eu.socialedge.hermes.domain.route;

import eu.socialedge.hermes.domain.line.Line;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "routes")
public class Route implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private int routeId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @NotNull
    @Size(min = 2)
    @Column(name = "code")
    private String code;

    @NotNull
    @OneToMany(mappedBy = "route")
    private Set<Waypoint> waypoints = new HashSet<>();

    Route() {}

    public Route(Line line, String code) {
        this.line = line;
        this.code = code;
    }

    public Route(Line line, String code, Set<Waypoint> waypoints) {
        this.line = line;
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
        return this.waypoints.stream().filter(w -> w.getOrder() == orderPosition).findFirst();
    }

    public Waypoint appendWaypoint(Waypoint waypoint) {
        int maxOrder = this.waypoints.stream().max(Waypoint::compareTo).get().getOrder();
        Waypoint wpClone = new Waypoint(waypoint.getRoute(), waypoint.getStation(), ++maxOrder);

        this.waypoints.add(wpClone);
        return wpClone;
    }

    public Waypoint prependWaypoint(Waypoint waypoint) {
        Waypoint wpClone = new Waypoint(waypoint.getRoute(), waypoint.getStation(), 1);

        insertWaypoint(wpClone);
        return wpClone;
    }

    public boolean insertWaypoint(Waypoint waypoint) {
        int newWpOrderPosition = waypoint.getOrder();
        List<Waypoint> shiftedWaypoints = new ArrayList<>();

        for (Waypoint wp : this.waypoints){
            int wpOrderPosition = wp.getOrder();
            if (wpOrderPosition >= newWpOrderPosition) {
                shiftedWaypoints.add(new Waypoint(wp.getRoute(), wp.getStation(), ++wpOrderPosition));
                this.waypoints.remove(wp);
            }
        }

        if (shiftedWaypoints.size() == 0)
            return false;

        this.waypoints.add(waypoint);
        this.waypoints.addAll(shiftedWaypoints);
        return true;
    }

    public boolean removeWaypoint(Waypoint waypoint) {
        int oldWpOrderPosition = waypoint.getOrder();
        List<Waypoint> shiftedWaypoints = new ArrayList<>();

        if (this.waypoints.remove(waypoint)) {
            this.waypoints.forEach(wp -> {
                int wpOrderPosition = wp.getOrder();
                if (wpOrderPosition > oldWpOrderPosition) {
                    shiftedWaypoints.add(new Waypoint(wp.getRoute(), wp.getStation(), --wpOrderPosition));
                    this.waypoints.remove(wp);
                }
            });
        }

        if (shiftedWaypoints.size() == 0)
            return false;

        this.waypoints.remove(waypoint);
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
