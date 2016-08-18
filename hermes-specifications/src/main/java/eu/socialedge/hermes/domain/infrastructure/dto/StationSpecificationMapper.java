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
package eu.socialedge.hermes.domain.infrastructure.dto;

import eu.socialedge.hermes.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StationSpecificationMapper
        implements SpecificationMapper<StationSpecification, Station> {

    public StationSpecification toDto(Station station) {
        StationSpecification data = new StationSpecification();

        data.id = station.id().toString();
        data.name = station.name();
        data.location.latitude = station.location().latitude();
        data.location.longitude = station.location().longitude();
        data.vehicleTypes = station.vehicleTypes().stream()
                .map(VehicleType::name).collect(Collectors.toSet());

        return data;
    }

    public Station fromDto(StationSpecification data) {
        StationId stationId = StationId.of(data.id);
        String name = data.name;
        Location location = Location.of(data.location.latitude, data.location.longitude);
        Set<VehicleType> vehicleTypes = data.vehicleTypes.stream()
                .map(VehicleType::valueOf).collect(Collectors.toSet());

        return new Station(stationId, name, location, vehicleTypes);
    }
}
