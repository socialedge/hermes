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
package eu.socialedge.hermes.schedule;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
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
    @Size(min = 2)
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "schedule")
    private Set<Departure> departures = new HashSet<>();

    Schedule() {}

    public Schedule(Timetable timetable, String name) {
        this.timetable = timetable;
        this.name = name;
    }

    public Schedule(Timetable timetable, String name, Set<Departure> departures) {
        this.timetable = timetable;
        this.name = name;
        this.departures = departures;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public Timetable getTimetable() {
        return timetable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Departure> getDepartures() {
        return departures;
    }

    public void addDeparture(Departure departure) {
        this.departures.add(departure);
    }

    public void removeDeparture(Departure departure) {
        this.departures.remove(departure);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(getTimetable(), schedule.getTimetable()) &&
                Objects.equals(getName(), schedule.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimetable(), getName());
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", timetable=" + timetable +
                ", name='" + name + '\'' +
                ", departures=" + departures +
                '}';
    }
}
