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

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "waypoints")
public class Waypoint implements Serializable, Comparable<Waypoint> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waypoint_id")
    private int waypointId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @Min(1)
    @Column(name = "position")
    private int order;

    Waypoint() {}

    public Waypoint(Route route, Station station, int order) {
        this.route = route;
        this.station = station;
        this.order = order;
    }

    public int getWaypointId() {
        return waypointId;
    }

    public Route getRoute() {
        return route;
    }

    public Station getStation() {
        return station;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(Waypoint o) {
        if (this.order < o.order)
            return -1;
        else if (this.order > o.order)
            return 1;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint waypoint = (Waypoint) o;
        return getOrder() == waypoint.getOrder() &&
                Objects.equals(getRoute(), waypoint.getRoute()) &&
                Objects.equals(getStation(), waypoint.getStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoute(), getStation(), getOrder());
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "waypointId=" + waypointId +
                ", route=" + route +
                ", station=" + station +
                ", order=" + order +
                '}';
    }
}
