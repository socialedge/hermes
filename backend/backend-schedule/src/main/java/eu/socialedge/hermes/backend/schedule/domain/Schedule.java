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

import eu.socialedge.hermes.backend.transit.domain.service.Line;
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
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

    @Id
    private final String id;

    @Getter
    private String description;

    @Getter
    private @NotNull Availability availability;

    @DBRef @Getter
    private @NotNull Line line;

    private final @NotEmpty List<Trip> inboundTrips = new ArrayList<>();

    private final @NotEmpty List<Trip> outboundTrips = new ArrayList<>();

    public Schedule(String id, String description, Availability availability, Line line, List<Trip> inboundTrips, List<Trip> outboundTrips) {
        this.id = isNotBlank(id) ? id : UUID.randomUUID().toString();
        this.description = description;
        this.availability = notNull(availability);
        this.line = notNull(line);
        this.inboundTrips.addAll(notEmpty(inboundTrips));
        this.outboundTrips.addAll(notEmpty(outboundTrips));
    }

    public Schedule(String description, Availability availability, Line line, List<Trip> inboundTrips, List<Trip> outboundTrips) {
        this(null, description, availability, line, inboundTrips, outboundTrips);
    }

    public Schedule(Availability availability, Line line, List<Trip> inboundTrips, List<Trip> outboundTrips) {
        this(null, null, availability, line, inboundTrips, outboundTrips);
    }

    private Schedule(Builder builder) {
        this(builder.id, builder.description, builder.availability, builder.line, builder.inboundTrips, builder.outboundTrips);
    }

    public String getId() {
        return id;
    }

    public List<Trip> getInboundTrips() {
        return Collections.unmodifiableList(inboundTrips);
    }

    public List<Trip> getOutboundTrips() {
        return Collections.unmodifiableList(outboundTrips);
    }

    public void setAvailability(Availability availability) {
        this.availability = notNull(availability);
    }

    public static final class Builder {

        private String id;

        private String description;

        private Availability availability;

        private Line line;

        private final List<Trip> inboundTrips = new ArrayList<>();

        private final List<Trip> outboundTrips = new ArrayList<>();

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

        public Builder addOutboundTrip(Trip trip) {
            this.outboundTrips.add(trip);
            return this;
        }

        public Builder addInboundTrip(Trip trip) {
            this.inboundTrips.add(trip);
            return this;
        }

        public Builder outboundTrips(Collection<Trip> trips) {
            if (trips != null)
                trips.forEach(this::addOutboundTrip);

            return this;
        }

        public Builder inboundTrips(Collection<Trip> trips) {
            if (trips != null)
                trips.forEach(this::addInboundTrip);

            return this;
        }

        public Schedule build() {
            return new Schedule(this);
        }
    }
}
