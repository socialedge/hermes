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

import javax.persistence.*;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A stop is a location where vehicles stop to pick up or drop off passengers.
 *
 * TODO: Add support for nesting (stop type Station (gtfs))
 */
@ToString
@Entity @Access(AccessType.FIELD)
@Getter @Setter @Accessors(fluent = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Stop extends Identifiable<Long> {

    @Column(name = "code")
    private String code;

    @Column(name = "name", nullable = false)
    private @NotBlank String name;

    @Column(name = "description")
    private String description;

    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Embedded
    private Location location;

    @Column(name = "is_hail", nullable = false)
    private boolean isHailStop = false;

    public Stop(String code, String name, String description, VehicleType vehicleType, Location location, Boolean isHailStop) {
        this.code = code;
        this.name = notBlank(name);
        this.description = description;
        this.vehicleType = notNull(vehicleType);
        this.location = notNull(location);

        if (nonNull(isHailStop))
            this.isHailStop = isHailStop;
    }

    public Stop(String name, VehicleType vehicleType, Location location) {
        this(null, name, null, vehicleType, location, null);
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public void location(Location location) {
        this.location = notNull(location);
    }
}
