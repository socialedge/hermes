/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a complete set of trips for specific line and defines
 * the days when a Trips are available to passengers.
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Schedule {

    @Id @Getter
    private final String id;

    @Getter
    private String description;

    @Getter
    private @NotNull Availability availability;

    @DBRef @Getter
    private @NotNull Line line;

    /**
     * @deprecated trips must be separated into outbound and inbound,
     * since {@link #getInboundTrips()} and {@link #getOutboundTrips()} are
     * just time wasting operations. Moreover, since one {@link Trip} naturally
     * can contain stops on any stations (not only from given {@link #line}), the
     * {@link #matchesRoute(Route, Trip)} will fail to match route for such Trips
     */
    @Deprecated
    private final @NotEmpty List<Trip> trips = new ArrayList<>();

    public Schedule(String id, String description, Availability availability, Line line, List<Trip> trips) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.description = description;
        this.availability = notNull(availability);
        this.line = notNull(line);
        this.trips.addAll(notEmpty(trips));
    }

    public Schedule(String description, Availability availability, Line line, List<Trip> trips) {
        this(null, description, availability, line, trips);
    }

    public Schedule(Availability availability, Line line, List<Trip> trips) {
        this(null, null, availability, line, trips);
    }

    private Schedule(Builder builder) {
        this(builder.id, builder.description, builder.availability, builder.line, builder.trips);
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

    @JsonIgnore
    public List<Trip> getInboundTrips() {
        return trips.stream()
            .filter(trip -> matchesRoute(line.getInboundRoute(), trip))
            .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Trip> getOutboundTrips() {
        if (line.isBidirectionalLine()) {
            return trips.stream()
                .filter(trip -> matchesRoute(line.getOutboundRoute(), trip))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Deprecated
    private static boolean matchesRoute(Route route, Trip trip) {
        return trip.getStops().stream()
            .map(Stop::getStation)
            .allMatch(route.getStations()::contains);
    }

    public static final class Builder {

        private String id;

        private String description;

        private Availability availability;

        private Line line;

        private final List<Trip> trips = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder availability(Availability availability) {
            this.availability = availability;
            return this;
        }

        public Builder line(Line line) {
            this.line = line;
            return this;
        }

        @Deprecated
        public Builder addTrip(Trip trip) {
            this.trips.add(trip);
            return this;
        }

        public Builder addOutboundTrip(Trip trip) {
            return this.addTrip(trip);
        }

        public Builder addInboundTrip(Trip trip) {
            return this.addTrip(trip);
        }

        public Builder outboundTrips(Collection<Trip> trips) {
            if (trips == null)
                return this;

            trips.forEach(this::addOutboundTrip);
            return this;
        }

        public Builder inboundTrips(Collection<Trip> trips) {
            if (trips == null)
                return this;

            trips.forEach(this::addInboundTrip);
            return this;
        }

        public Schedule build() {
            return new Schedule(this);
        }
    }
}
