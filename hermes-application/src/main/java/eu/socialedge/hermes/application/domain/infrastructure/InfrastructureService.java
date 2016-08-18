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
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
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
    public InfrastructureService(StationRepository stationRepository, StationSpecificationMapper stationSpecificationMapper) {
        this.stationRepository = stationRepository;
        this.stationSpecificationMapper = stationSpecificationMapper;
    }

    public Collection<StationSpecification> fetchAllStations() {
        return stationRepository.list().stream().map(stationSpecificationMapper::toDto).collect(Collectors.toList());
    }

    public StationSpecification fetchStation(StationId stationId) {
        Station station = stationRepository.get(stationId).orElseThrow(()
                -> new NotFoundException("Station not found. Id = " + stationId));

        return stationSpecificationMapper.toDto(station);
    }

    public void createStation(StationSpecification data) {
        stationRepository.add(stationSpecificationMapper.fromDto(data));
    }

    public void updateStation(StationId stationId, StationSpecification data) {
        StationSpecification persistedStationSpecification = fetchStation(stationId);

        if (isNotBlank(data.name))
            persistedStationSpecification.name = data.name;

        if (isNotNull(data.location.latitude)) {
            persistedStationSpecification.location.latitude = data.location.latitude;
        }

        if (isNotNull(data.location.longitude)) {
            persistedStationSpecification.location.longitude = data.location.longitude;
        }

        if (isNotEmpty(data.vehicleTypes)) {
            persistedStationSpecification.vehicleTypes = data.vehicleTypes;
        }

        stationRepository.update(stationSpecificationMapper.fromDto(persistedStationSpecification));
    }

    public void deleteStation(StationId stationId) {
        boolean wasRemoved = stationRepository.remove(stationId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find station to delete. Id = " + stationId);
    }
}
