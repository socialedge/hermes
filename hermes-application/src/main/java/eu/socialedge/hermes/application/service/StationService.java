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

import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Position;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.infrastructure.TransportType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;

import static org.apache.commons.lang3.Validate.notNull;

@Component
@Transactional(readOnly = true)
public class StationService {
    private final StationRepository stationRepository;

    @Inject
    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Transactional
    public Station createStation(String stationCode, String name, TransportType type, Position position) {
        return stationRepository.store(new Station(stationCode, name, type, position));
    }

    public Collection<Station> fetchAllStations() {
        return stationRepository.list();
    }

    public Station fetchStation(String stationCodeId) {
        return stationRepository.get(notNull(stationCodeId)).orElseThrow(()
                ->  new NotFoundException("No station found with code id = " + stationCodeId));
    }

    @Transactional
    public void updateStation(String stationCodeId, String name) {
        Station stationToPatch = fetchStation(stationCodeId);

        stationToPatch.setName(name);
        stationRepository.store(stationToPatch);
    }

    @Transactional
    public void removeStation(String stationCodeId) {
        stationRepository.remove(fetchStation(stationCodeId));
    }
}
