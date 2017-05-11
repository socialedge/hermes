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
import eu.socialedge.hermes.backend.transit.domain.ext.Identifiable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @Column(name = "description")
    private String description;

    @Getter
    @Embedded
    private @NotNull Availability availability;

    @Getter
    @ManyToOne
    @JoinColumn(name = "line_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private @NotNull Line line;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id")
    private @NotEmpty List<Trip> trips;

    public Schedule(String description, Availability availability, Line line, List<Trip> trips) {
        this.description = description;
        this.availability = notNull(availability);
        this.line = notNull(line);
        this.trips = new ArrayList<>(notEmpty(trips));
    }

    public Schedule(Availability availability, Line line, List<Trip> trips) {
        this(null, availability, line, trips);
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

    public List<Trip> trips() {
        return Collections.unmodifiableList(trips);
    }
}
