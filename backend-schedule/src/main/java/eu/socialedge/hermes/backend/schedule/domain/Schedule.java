/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.transit.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.ext.Identifiable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.Validate.*;

/**
 * Describes a complete set of trips for specific route and defines
 * the days when a Trips are available to passengers.
 */
@ToString
@Accessors(fluent = true)
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Schedule extends Identifiable<Long> {

    @Getter
    @Column(name = "description", nullable = false)
    private @NotBlank String description;

    @Getter
    @Embedded
    private @NotNull Availability availability;

    @ElementCollection
    @CollectionTable(name = "schedule_trips", joinColumns = @JoinColumn(name = "schedule_id"))
    private @NotEmpty Set<Trip> trips;

    public Schedule(String description, Availability availability, Set<Trip> trips) {
        this.description = notBlank(description);
        this.availability = notNull(availability);
        this.trips = new HashSet<>(notEmpty(trips));
    }

    public void description(String description) {
        this.description = notBlank(description);
    }

    public void availability(Availability availability) {
        this.availability = notNull(availability);
    }

    public boolean addTrip(Trip trip) {
        return trips.add(trip);
    }

    public void removeTrip(Trip trip) {
        trips.remove(trip);
    }

    public Collection<Trip> trips() {
        return Collections.unmodifiableSet(trips);
    }
}
