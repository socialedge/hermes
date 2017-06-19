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
package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.Validate.*;

/**
 * A stop is a location where vehicles stop to pick up or drop off passengers.
 *
 * TODO: Add support for nesting (stop type Station (gtfs))
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Station {

    @Getter
    private final String id;

    @Getter
    private @NotBlank String name;

    @Setter @Getter
    private String description;

    private final @NotEmpty Set<VehicleType> vehicleTypes = new HashSet<>();

    @Getter
    private @NotNull Location location;

    @Setter @Getter
    private boolean hailStop = false;

    public Station(String id, String name, String description, Set<VehicleType> vehicleTypes, Location location, Boolean hailStop) {
        this.id = defaultIfBlank(id, UUID.randomUUID().toString());
        this.name = notBlank(name);
        this.description = description;
        this.location = notNull(location);

        if (vehicleTypes != null)
            this.vehicleTypes.addAll(vehicleTypes);

        if (nonNull(hailStop))
            this.hailStop = hailStop;
    }

    public Station(String name, String description, Set<VehicleType> vehicleTypes, Location location, Boolean hailStop) {
        this(null, name, description, vehicleTypes, location, hailStop);
    }

    public Station(String name, Set<VehicleType> vehicleTypes, Location location) {
        this(null, name, null, vehicleTypes, location, null);
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
        return Collections.unmodifiableSet(vehicleTypes);
    }
}
