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
        StationSpecification spec = new StationSpecification();

        spec.id = station.id().toString();
        spec.name = station.name();
        spec.location.latitude = station.location().latitude();
        spec.location.longitude = station.location().longitude();
        spec.vehicleTypes = station.vehicleTypes().stream()
                .map(VehicleType::name).collect(Collectors.toSet());

        return spec;
    }

    public Station fromDto(StationSpecification spec) {
        StationId stationId = StationId.of(spec.id);
        String name = spec.name;
        Location location = Location.of(spec.location.latitude, spec.location.longitude);
        Set<VehicleType> vehicleTypes = spec.vehicleTypes.stream()
                .map(VehicleType::valueOf).collect(Collectors.toSet());

        return new Station(stationId, name, location, vehicleTypes);
    }
}
