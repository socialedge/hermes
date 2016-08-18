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
package eu.socialedge.hermes.application.domain.infrastructure;

import eu.socialedge.hermes.application.domain.infrastructure.dto.StationSpecification;
import eu.socialedge.hermes.application.domain.infrastructure.dto.StationSpecificationMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;
import static eu.socialedge.hermes.util.Values.isNotNull;

@Component
public class InfrastructureService {

    private final StationRepository stationRepository;
    private final StationSpecificationMapper stationSpecificationMapper;

    @Inject
    public InfrastructureService(StationRepository stationRepository,
                                 StationSpecificationMapper stationSpecificationMapper) {
        this.stationRepository = stationRepository;
        this.stationSpecificationMapper = stationSpecificationMapper;
    }

    public Collection<Station> fetchAllStations() {
        return stationRepository.list();
    }

    public Station fetchStation(StationId stationId) {
        return stationRepository.get(stationId).orElseThrow(()
                -> new NotFoundException("Station not found. Id = " + stationId));
    }

    public void createStation(StationSpecification data) {
        stationRepository.add(stationSpecificationMapper.fromDto(data));
    }

    public void updateStation(StationId stationId, StationSpecification data) {
        Station persistedStation = fetchStation(stationId);

        if (isNotBlank(data.name))
            persistedStation.name(data.name);

        if (isNotNull(data.location.latitude) && isNotNull(data.location.longitude))
            persistedStation.location(Location.of(data.location.latitude, data.location.longitude));

        if (isNotEmpty(data.vehicleTypes)) {
            Set<VehicleType> vehicleTypes = data.vehicleTypes.stream()
                    .map(VehicleType::valueOf).collect(Collectors.toSet());

            persistedStation.vehicleTypes().clear();
            persistedStation.vehicleTypes().addAll(vehicleTypes);

        }

        stationRepository.update(persistedStation);
    }

    public void deleteStation(StationId stationId) {
        boolean wasRemoved = stationRepository.remove(stationId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find station to delete. Id = " + stationId);
    }
}
