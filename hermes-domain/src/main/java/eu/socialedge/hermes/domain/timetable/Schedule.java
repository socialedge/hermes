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
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.util.Values.requireNotNull;

/**
 * Schedules define timetables of vehicle {@link Trip}s for a
 * defined {@link eu.socialedge.hermes.domain.transit.Route}.
 *
 * <p>Every schedule is effective only on certain days, defined
 * by {@link ScheduleAvailability}.</p>
 */
@AggregateRoot
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Entity @Table(name = "schedules")
public class Schedule implements Identifiable<ScheduleId> {

    @EmbeddedId
    private final ScheduleId id;

    @Embedded
    private final RouteId routeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private ScheduleAvailability scheduleAvailability;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "schedule_trips", joinColumns = @JoinColumn(name = "schedule_id"))
    private final Set<TripId> tripIds;

    public Schedule(ScheduleId id, RouteId routeId,
                    ScheduleAvailability scheduleAvailability) {
        this(id, routeId, scheduleAvailability, new HashSet<>());
    }

    public Schedule(ScheduleId id, RouteId routeId, String name,
                    ScheduleAvailability scheduleAvailability) {
        this(id, routeId, name, scheduleAvailability, new HashSet<>());
    }

    public Schedule(ScheduleId id, RouteId routeId,
                    ScheduleAvailability scheduleAvailability, Collection<TripId> tripIds) {
        this(id, routeId, scheduleAvailability, new HashSet<>(tripIds));
    }

    public Schedule(ScheduleId id, RouteId routeId,
                    ScheduleAvailability scheduleAvailability, Set<TripId> tripIds) {
        this.id = requireNotNull(id);
        this.routeId = requireNotNull(routeId);
        this.scheduleAvailability = requireNotNull(scheduleAvailability);
        this.tripIds = requireNotNull(tripIds);
    }

    public Schedule(ScheduleId id, RouteId routeId, String name,
                    ScheduleAvailability scheduleAvailability, Set<TripId> tripIds) {
        this.id = requireNotNull(id);
        this.routeId = requireNotNull(routeId);
        this.name = requireNotBlank(name);
        this.scheduleAvailability = requireNotNull(scheduleAvailability);
        this.tripIds = requireNotNull(tripIds);
    }

    @Override
    public ScheduleId id() {
        return id;
    }

    public RouteId routeId() {
        return routeId;
    }

    public ScheduleAvailability scheduleAvailability() {
        return scheduleAvailability;
    }

    public void scheduleAvailability(ScheduleAvailability scheduleAvailability) {
        this.scheduleAvailability = requireNotNull(scheduleAvailability);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Set<TripId> tripIds() {
        return tripIds;
    }
}
