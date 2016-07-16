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
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@AggregateRoot
@Table(name = "schedules")
public class Schedule implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int scheduleId;

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

    public Schedule(String name) {
        this.name = Validate.notBlank(name);
    }

    public Schedule(String name, Set<Departure> departures) {
        this(name);
        this.departures = Validate.notEmpty(departures);
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public String getName() {
        return name;
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
        return this.departures.add(Validate.notNull(departure));
    }

    public boolean removeDeparture(Departure departure) {
        return this.departures.remove(departure);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(getName(), schedule.getName()) &&
                Objects.equals(getDepartures(), schedule.getDepartures());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDepartures());
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", name='" + name + '\'' +
                ", departures=" + departures +
                ", creationDate=" + creationDate +
                ", expirationDate=" + expirationDate +
                '}';
    }
}
