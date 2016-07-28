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
import eu.socialedge.hermes.domain.v2.operator.Location;

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
 * and {@link Station#transportTypes}s it services.</p>
 *
 * <p>One station can serve several types of transport (e.g. {@link TransportType#BUS}
 * and {@link TransportType#TROLLEY}).</p>
 */
@AggregateRoot
public class Station {

    private final StationId stationId;

    private String name;

    private Location location;

    private Set<TransportType> transportTypes;

    public Station(StationId stationId, String name, Location location,
                   Set<TransportType> transportTypes) {
        this.stationId = notNull(stationId);
        this.name = notBlank(name);
        this.location = notNull(location);
        this.transportTypes = notNull(transportTypes);
    }

    public Station(String stationId, String name, Location location,
                   Set<TransportType> transportTypes) {
        this(StationId.of(stationId), name, location, transportTypes);
    }

    public Station(StationId stationId, String name, Location location,
                   TransportType... transportTypes) {
        this(stationId, name, location, new HashSet<>(Arrays.asList(transportTypes)));
    }

    public Station(String stationId, String name, Location location,
                   TransportType... transportTypes) {
        this(StationId.of(stationId), name, location, transportTypes);
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

    public Set<TransportType> transportTypes() {
        return transportTypes;
    }

    public void transportTypes(Set<TransportType> transportTypes) {
        this.transportTypes = notNull(transportTypes);
    }

    public void transportTypes(TransportType... transportTypes) {
        this.transportTypes = new HashSet<>(Arrays.asList(notNull(transportTypes)));
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
