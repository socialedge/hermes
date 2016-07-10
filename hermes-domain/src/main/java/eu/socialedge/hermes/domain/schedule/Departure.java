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
package eu.socialedge.hermes.domain.schedule;

import eu.socialedge.hermes.domain.route.Waypoint;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "departures")
public class Departure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departure_id")
    private int departureId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "waypoint_id")
    private Waypoint waypoint;

    @NotNull
    @Column(name = "time")
    private LocalTime time;

    Departure() {}

    public Departure(Schedule schedule, Waypoint waypoint, LocalTime time) {
        this.schedule = schedule;
        this.waypoint = waypoint;
        this.time = time;
    }

    public int getDepartureId() {
        return departureId;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Departure)) return false;
        Departure departure = (Departure) o;
        return Objects.equals(getSchedule(), departure.getSchedule()) &&
                Objects.equals(getWaypoint(), departure.getWaypoint()) &&
                Objects.equals(getTime(), departure.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchedule(), getWaypoint(), getTime());
    }

    @Override
    public String toString() {
        return "Departure{" +
                "departureId=" + departureId +
                ", schedule=" + schedule +
                ", waypoint=" + waypoint +
                ", time=" + time +
                '}';
    }
}
