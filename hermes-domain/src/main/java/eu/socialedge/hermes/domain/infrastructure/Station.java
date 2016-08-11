/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.transport.VehicleType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Iterables.requireNotEmpty;
import static eu.socialedge.hermes.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.util.Values.requireNotNull;

/**
 * Describes a bus/tram/train/trolley station (stop).
 *
 * <p>Every Station can by uniquely identified by station's {@link Station#id}.
 * In addition, {@link Station} has {@link Station#name}, defined {@link Station#location}
 * and {@link Station#vehicleTypes}s it services.</p>
 *
 * <p>One station can serve several types of transport (e.g. {@link VehicleType#BUS}
 * and {@link VehicleType#TROLLEYBUS}).</p>
 *
 * @see <a href="https://goo.gl/GAROgU">Google Transit APIs
 * > Static Transit > stops.txt File</a>
 */
@AggregateRoot
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Entity @Table(name = "stations")
public class Station implements Identifiable<StationId> {

    @EmbeddedId
    private final StationId id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private Location location;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "station_vehicle_types", joinColumns = @JoinColumn(name = "station_id"))
    @Column(name = "vehicle_type", nullable = false)
    private Set<VehicleType> vehicleTypes;

    public Station(StationId id, String name, Location location,
                   Set<VehicleType> vehicleTypes) {
        this.id = requireNotNull(id);
        this.name = requireNotBlank(name);
        this.location = requireNotNull(location);
        this.vehicleTypes = requireNotEmpty(vehicleTypes);
    }

    public Station(StationId id, String name, Location location,
                   VehicleType... vehicleTypes) {
        this(id, name, location, new HashSet<>(Arrays.asList(vehicleTypes)));
    }

    @Override
    public StationId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = requireNotBlank(name);
    }

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = location;
    }

    public Set<VehicleType> vehicleTypes() {
        return vehicleTypes;
    }
}
