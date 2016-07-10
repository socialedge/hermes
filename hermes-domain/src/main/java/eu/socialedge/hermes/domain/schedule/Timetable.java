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

import eu.socialedge.hermes.domain.route.Route;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
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
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    Timetable() {}

    public Timetable(Route route, LocalDate creationDate) {
        this.route = route;
        this.creationDate = creationDate;
    }

    public Timetable(Route route, LocalDate creationDate, LocalDate expirationDate) {
        this.route = route;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
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
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timetable)) return false;
        Timetable timetable = (Timetable) o;
        return Objects.equals(getRoute(), timetable.getRoute()) &&
                Objects.equals(getCreationDate(), timetable.getCreationDate()) &&
                Objects.equals(getExpirationDate(), timetable.getExpirationDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoute(), getCreationDate(), getExpirationDate());
    }

    @Override
    public String toString() {
        return "Timetable{" +
                "timetableId=" + timetableId +
                ", route=" + route +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
