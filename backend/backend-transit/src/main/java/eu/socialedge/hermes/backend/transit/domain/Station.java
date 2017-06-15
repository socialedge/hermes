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

import eu.socialedge.hermes.backend.transit.domain.ext.Identifiable;
import lombok.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.Validate.*;

/**
 * A stop is a location where vehicles stop to pick up or drop off passengers.
 *
 * TODO: Add support for nesting (stop type Station (gtfs))
 */
@ToString
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Station extends Identifiable<Long> {

    @Setter @Getter
    @Column(name = "code")
    private String code;

    @Getter
    @Column(name = "name", nullable = false)
    private @NotBlank String name;

    @Setter @Getter
    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "vehicle_type")
    @CollectionTable(name = "station_vehicle_type", joinColumns = @JoinColumn(name = "station_id"))
    @Enumerated(EnumType.STRING)
    private Set<VehicleType> vehicleTypes;

    @Getter
    @Embedded
    private Location location;

    @Setter @Getter
    @Column(name = "is_hail", nullable = false)
    private boolean hailStop = false;

    public Station(String code, String name, String description, Set<VehicleType> vehicleTypes, Location location, Boolean hailStop) {
        this.code = code;
        this.name = notBlank(name);
        this.description = description;
        this.vehicleTypes = new HashSet<>(notEmpty(vehicleTypes));
        this.location = notNull(location);

        if (nonNull(hailStop))
            this.hailStop = hailStop;
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
