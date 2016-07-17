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

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.Waypoint;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@AggregateRoot
@Table(name = "schedules")
public class Schedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "route_code")
    private Route route;

    @Column(name = "name")
    private String name;

    @ElementCollection
    @CollectionTable(name = "departures", joinColumns = @JoinColumn(name = "schedule_id"))
    private Set<Departure> departures = new HashSet<>();

    @Column(name = "creation_date")
    private final LocalDate creationDate = LocalDate.now();

    @Column(name = "expiration_date")
    private LocalDate expirationDate = LocalDate.MAX;

    protected Schedule() {}

    public Schedule(Route route, String name) {
        this.name = Validate.notBlank(name);
        this.route = route;
    }

    public Schedule(Route route, String name, Set<Departure> departures) {
        this(route, name);
        this.departures = Validate.notEmpty(departures);
    }

    public int getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Validate.notBlank(name);
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        if (this.creationDate.isBefore(expirationDate))
            throw new IllegalArgumentException("creationDate must not be before the expirationDate");
        this.expirationDate = expirationDate;
    }

    public Set<Departure> getDepartures() {
        return departures;
    }

    public boolean addDeparture(Departure departure) {
        Station depStation = Validate.notNull(departure).getStation();
        if (!hasStationOnRoute(depStation))
            throw new IllegalArgumentException("Station {" + depStation + "} doesn't belong to any route's waypoint");

        return this.departures.add(departure);
    }

    public boolean removeDeparture(Departure departure) {
        return this.departures.remove(departure);
    }

    public void setDepartures(Set<Departure> departures) {
        Collection<Station> depStations = Validate.notNull(departures).stream()
                .map(Departure::getStation).collect(Collectors.toList());

        if (!hasStationsOnRoute(depStations))
            throw new IllegalArgumentException("Some of departure stations aren't on the route, schedule attached to." +
                    "Route's waypoints = " + route.getWaypoints() + ", your dep's stations = " + depStations);

        this.departures = Validate.notEmpty(departures);
    }

    private boolean hasStationOnRoute(Station station) {
        return this.route.getWaypoints().stream()
                         .map(Waypoint::getStation)
                         .anyMatch(s -> s.equals(station));
    }

    private boolean hasStationsOnRoute(Collection<Station> stations) {
        return this.route.getWaypoints().stream()
                         .map(Waypoint::getStation)
                         .allMatch(stations::contains);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(getRoute().getCodeId(), schedule.getRoute().getCodeId()) &&
               Objects.equals(getName(), schedule.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoute().getCodeId(), getName());
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", route=" + route +
                ", name='" + name + '\'' +
                ", departures=" + departures +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
