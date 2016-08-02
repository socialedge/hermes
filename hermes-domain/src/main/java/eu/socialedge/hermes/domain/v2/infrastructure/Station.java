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
package eu.socialedge.hermes.domain.v2.infrastructure;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.v2.shared.geo.Location;
import eu.socialedge.hermes.domain.v2.shared.transport.VehicleType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Describes a bus/tram/train/trolley station (stop).
 *
 * <p>Every Station can by uniquely identified by station's {@link Station#stationId}.
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
public class Station {

    private final StationId stationId;

    private String name;

    private Location location;

    private Set<VehicleType> vehicleTypes;

    public Station(StationId stationId, String name, Location location,
                   Set<VehicleType> vehicleTypes) {
        this.stationId = notNull(stationId);
        this.name = notBlank(name);
        this.location = notNull(location);
        this.vehicleTypes = notNull(vehicleTypes);
    }

    public Station(StationId stationId, String name, Location location,
                   VehicleType... vehicleTypes) {
        this(stationId, name, location, new HashSet<>(Arrays.asList(vehicleTypes)));
    }

    public StationId stationId() {
        return stationId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
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

    public void vehicleTypes(Set<VehicleType> vehicleTypes) {
        this.vehicleTypes = notNull(vehicleTypes);
    }

    public void vehicleTypes(VehicleType... vehicleTypes) {
        this.vehicleTypes = new HashSet<>(Arrays.asList(notNull(vehicleTypes)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Objects.equals(stationId, station.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId);
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId=" + stationId +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
