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
package eu.socialedge.hermes.infrastructure.persistence.jpa.entity;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "schedules")
public class JpaSchedule {

    @Id
    @Column(name = "schedule_id")
    private String scheduleId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private JpaRoute route;

    @OneToMany
    @JoinColumn(name="schedule_id", referencedColumnName="schedule_id")
    private Collection<JpaTrip> trips;

    @Embedded
    private JpaScheduleAvailability scheduleAvailability;

    public JpaSchedule() {}

    public String scheduleId() {
        return scheduleId;
    }

    public void scheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public JpaRoute route() {
        return route;
    }

    public void route(JpaRoute route) {
        this.route = route;
    }

    public Collection<JpaTrip> trips() {
        return trips;
    }

    public void trips(Collection<JpaTrip> trips) {
        this.trips = trips;
    }

    public JpaScheduleAvailability scheduleAvailability() {
        return scheduleAvailability;
    }

    public void scheduleAvailability(JpaScheduleAvailability scheduleAvailability) {
        this.scheduleAvailability = scheduleAvailability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaSchedule)) return false;
        JpaSchedule that = (JpaSchedule) o;
        return Objects.equals(scheduleId, that.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scheduleId);
    }
}
