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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Entity // Because of https://hibernate.atlassian.net/browse/HHH-4313. Must be a @ValueObject.
@Table(name = "schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int scheduleId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "timetable_id")
    private Timetable timetable;

    @NotNull
    @Size(min = 3)
    @Column(name = "name")
    private String name;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "departures", joinColumns = @JoinColumn(name = "schedule_id"))
    private Set<Departure> departures;

    public Schedule() {}

    public Schedule(String name) {
        this.name = name;
        this.departures = Collections.emptySet();
    }

    public Schedule(String name, Set<Departure> departures) {
        this.name = name;
        this.departures = departures;
    }

    public String getName() {
        return name;
    }

    public Set<Departure> getDepartures() {
        return departures;
    }

    public void addDeparture(Departure departure) {
        this.departures.add(Objects.requireNonNull(departure));
    }

    public void removeDeparture(Departure departure) {
        this.departures.remove(departure);
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
}
