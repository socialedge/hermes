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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.mapping;

import eu.socialedge.hermes.domain.v2.infrastructure.Station;
import eu.socialedge.hermes.domain.v2.shared.transport.VehicleType;
import eu.socialedge.hermes.domain.v2.shared.geo.Location;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaStation;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

@Component
public class StationEntityManager implements EntityMapper<Station, JpaStation> {

    private final LocationEntityMapper locationEntityMapper;

    @Inject
    public StationEntityManager(LocationEntityMapper locationEntityMapper) {
        this.locationEntityMapper = locationEntityMapper;
    }

    @Override
    public JpaStation mapToEntity(Station station) {
        JpaStation jpaStation = new JpaStation();

        jpaStation.stationId(station.stationId().toString());
        jpaStation.name(station.name());

        jpaStation.location(locationEntityMapper.mapToEntity(station.location()));
        jpaStation.vehicleTypes(station.vehicleTypes());

        return jpaStation;
    }

    @Override
    public Station mapToDomain(JpaStation jpaStation) {
        String stationId = jpaStation.stationId();
        String name = jpaStation.name();
        Location location = locationEntityMapper.mapToDomain(jpaStation.location());
        Set<VehicleType> vehicleTypes = jpaStation.vehicleTypes();

        return new Station(stationId, name, location, vehicleTypes);
    }
}
