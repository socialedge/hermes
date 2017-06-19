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

import eu.socialedge.hermes.backend.transit.domain.Line;
import eu.socialedge.hermes.backend.transit.domain.Trip;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a complete set of trips for specific route and defines
 * the days when a Trips are available to passengers.
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Schedule {

    @Getter
    private final String id;

    @Getter
    private String description;

    @Getter
    private @NotNull Availability availability;

    @Getter
    @DBRef
    private @NotNull Line line;

    private @NotEmpty List<Trip> trips;

    public Schedule(String id, String description, Availability availability, Line line, List<Trip> trips) {
        this.id = notBlank(id);
        this.description = description;
        this.availability = notNull(availability);
        this.line = notNull(line);
        this.trips = new ArrayList<>(notEmpty(trips));
    }

    public Schedule(String description, Availability availability, Line line, List<Trip> trips) {
        this(UUID.randomUUID().toString(), description, availability, line, trips);
    }

    public Schedule(Availability availability, Line line, List<Trip> trips) {
        this(UUID.randomUUID().toString(), null, availability, line, trips);
    }

    public void setAvailability(Availability availability) {
        this.availability = notNull(availability);
    }

    public boolean addTrip(Trip trip) {
        return trips.add(trip);
    }

    public void removeTrip(Trip trip) {
        trips.remove(trip);
    }

    public List<Trip> getTrips() {
        return Collections.unmodifiableList(trips);
    }
}
