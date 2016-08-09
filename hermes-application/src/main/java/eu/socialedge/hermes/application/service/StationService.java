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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.resource.spec.StationSpecification;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.domain.shared.util.Strings.isNotBlank;
import static java.util.Objects.isNull;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Component
public class StationService {
    private final StationRepository stationRepository;

    @Inject
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Collection<Station> fetchAllStations() {
        return stationRepository.list();
    }

    public Optional<Station> fetchStation(StationId stationId) {
        return stationRepository.get(stationId);
    }

    public void createStation(StationSpecification spec) {
        StationId stationId = StationId.of(spec.stationId);
        String name = spec.name;
        Location location = Location.of(spec.locationLatitude, spec.locationLongitude);
        Set<VehicleType> vehicleTypes = spec.vehicleTypes.stream().map(VehicleType::valueOf).collect
                (Collectors.toSet());

        Station station = new Station(stationId, name, location, vehicleTypes);

        stationRepository.save(station);
    }

    public void updateStation(StationId stationId, StationSpecification spec) {
        Station persistedStation = fetchStation(stationId)
                .orElseThrow(() -> new NotFoundException("Failed to find Station to update. Id = " + stationId));

        if (isNotBlank(spec.name))
            persistedStation.name(spec.name);

        if (!isNull(spec.locationLatitude) && !isNull(spec.locationLongitude))
            persistedStation.location(Location.of(spec.locationLatitude, spec.locationLongitude));

        if (isNotEmpty(spec.vehicleTypes)) {
            List<VehicleType> vehicleTypes = spec.vehicleTypes.stream().map(VehicleType::valueOf)
                    .collect(Collectors.toList());

            persistedStation.vehicleTypes().clear();
            persistedStation.vehicleTypes().addAll(vehicleTypes);
        }

        stationRepository.save(persistedStation);
    }

    public boolean deleteStation(StationId stationId) {
        return stationRepository.remove(stationId);
    }
}
