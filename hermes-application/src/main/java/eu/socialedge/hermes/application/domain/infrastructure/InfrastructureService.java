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
import static java.util.Objects.nonNull;

@Component
public class InfrastructureService {
    private final StationRepository stationRepository;

    @Inject
    public InfrastructureService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Collection<StationData> fetchAllStations() {
        return stationRepository.list().stream().map(StationDataMapper::toData).collect(Collectors.toList());
    }

    public StationData fetchStation(StationId stationId) {
        Station station = stationRepository.get(stationId).orElseThrow(()
                -> new NotFoundException("Station not found. Id = " + stationId));

        return StationDataMapper.toData(station);
    }

    public void createStation(StationData data) {
        stationRepository.add(StationDataMapper.fromData(data));
    }

    public void updateStation(StationId stationId, StationData data) {
        StationData persistedStationData = fetchStation(stationId);

        if (isNotBlank(data.name))
            persistedStationData.name = data.name;

        if (nonNull(data.locationLatitude)) {
            persistedStationData.locationLatitude = data.locationLatitude;
        }

        if (nonNull(data.locationLongitude)) {
            persistedStationData.locationLongitude = data.locationLongitude;
        }

        if (isNotEmpty(data.vehicleTypes)) {
            persistedStationData.vehicleTypes = data.vehicleTypes;
        }

        stationRepository.update(StationDataMapper.fromData(persistedStationData));
    }

    public void deleteStation(StationId stationId) {
        boolean wasRemoved = stationRepository.remove(stationId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find station to delete. Id = " + stationId);
    }
}
