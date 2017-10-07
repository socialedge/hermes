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
package eu.socialedge.hermes.backend.transit.domain.infra;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A stop is a location where vehicles stop to pick up or drop off passengers.
 *
 * @see <a href="https://goo.gl/cNqn5j">
 *     Google Static Transit (GTFS) - stops.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode(of = "id")
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Station {

    @Id @Getter
    private final String id;

    @Getter
    private @NotBlank String name;

    @Setter @Getter
    private String description;

    private final @NotEmpty Set<VehicleType> vehicleTypes = new HashSet<>();

    @Getter
    private @NotNull Location location;

    @Getter
    private @NotNull Duration dwell;

    public Station(String id, String name, String description, Set<VehicleType> vehicleTypes,
                   Location location, Duration dwell) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.name = notBlank(name);
        this.description = description;
        this.location = notNull(location);

        if (vehicleTypes != null)
            this.vehicleTypes.addAll(vehicleTypes);

        if (dwell.isNegative() || dwell.isZero())
            throw new IllegalArgumentException("Dwell duration can't be zero or negative");
        this.dwell = dwell;
    }

    public Station(String name, String description, Set<VehicleType> vehicleTypes,
                   Location location, Duration dwell) {
        this(null, name, description, vehicleTypes, location, dwell);
    }

    public Station(String name, Set<VehicleType> vehicleTypes, Location location, Duration dwell) {
        this(null, name, null, vehicleTypes, location, dwell);
    }

    private Station(Builder builder) {
        this(builder.id, builder.name, builder.description, builder.vehicleTypes, builder.location, builder.dwell);
    }

    public void setName(String name) {
        this.name = notBlank(name);
    }

    public void setLocation(Location location) {
        this.location = notNull(location);
    }

    public boolean addVehicleType(VehicleType vehicleType) {
        return vehicleTypes.add(vehicleType);
    }

    public void removeVehicleType(VehicleType vehicleType) {
        vehicleTypes.remove(vehicleType);
    }

    public Collection<VehicleType> getVehicleTypes() {
        return Collections.unmodifiableCollection(vehicleTypes);
    }

    public void setDwell(Duration duration) {
        if (duration.isNegative() || duration.isZero())
            throw new IllegalArgumentException("Dwell duration can't be zero or negative");

        this.dwell = duration;
    }

    public static final class Builder {

        private String id;

        private String name;

        private String description;

        private final Set<VehicleType> vehicleTypes = new HashSet<>();

        private Location location;

        private Duration dwell;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addVehicleType(VehicleType vehicleType) {
            this.vehicleTypes.add(vehicleType);
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        public Builder location(double latitude, double longitude) {
            this.location = new Location(latitude, longitude);
            return this;
        }

        public Builder dwell(Duration dwell) {
            this.dwell = dwell;
            return this;
        }

        public Station build() {
            return new Station(this);
        }
    }
}
