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
package eu.socialedge.hermes.domain.routing;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.infrastructure.Route;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Entity
@AggregateRoot
@Table(name = "timetables")
public class Timetable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private int timetableId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @NotNull
    @OneToMany(mappedBy = "timetable")
    private Set<Schedule> schedules = Collections.emptySet();

    @NotNull
    @Column(name = "creation_date")
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "expiration_date")
    private LocalDate expirationDate = LocalDate.MAX;

    Timetable() {}

    public Timetable(Route route, Set<Schedule> schedules) {
        this.route = route;
        this.schedules = schedules;
    }

    public Timetable(Route route, Set<Schedule> schedules, LocalDate creationDate, LocalDate expirationDate) {
        this.route = route;
        this.schedules = schedules;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public Route getRoute() {
        return route;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        if (this.creationDate.isBefore(expirationDate))
            throw new IllegalArgumentException("creationDate must not be before the expirationDate");
        this.expirationDate = expirationDate;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public boolean addSchedule(Schedule schedule) {
        return this.schedules.add(Objects.requireNonNull(schedule));
    }

    public boolean removeSchedule(Schedule schedule) {
        return this.schedules.remove(schedule);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timetable timetable = (Timetable) o;
        return Objects.equals(creationDate, timetable.creationDate) &&
                Objects.equals(expirationDate, timetable.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creationDate, expirationDate);
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "timetableId=" + timetableId +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
