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
package eu.socialedge.hermes.backend.core;

import eu.socialedge.hermes.backend.core.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_ide")
    private @NotNull Route route;

    @ElementCollection
    @CollectionTable(name = "schedule_trips", joinColumns = @JoinColumn(name = "schedule_id"))
    private @NotEmpty List<Trip> trips;

    @Getter
    @Embedded
    private @NotNull Availability availability;

    public Schedule(String description, Route route, List<Trip> trips, Availability availability) {
        this.description = notBlank(description);
        this.route = notNull(route);
        this.trips = new ArrayList<>(notEmpty(trips));
        this.availability = notNull(availability);
    }

    public void description(String description) {
        this.description = notBlank(description);
    }

    public void route(Route route) {
        this.route = notNull(route);
    }

    public void availability(Availability availability) {
        this.availability = notNull(availability);
    }

    public List<Trip> trips() {
        return Collections.unmodifiableList(trips);
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public void removeTrip(Trip trip) {
        trips.remove(trip);
    }

}
