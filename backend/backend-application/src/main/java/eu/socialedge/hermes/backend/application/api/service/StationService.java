/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.application.api.service;

import eu.socialedge.hermes.backend.application.api.StationsApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
import eu.socialedge.hermes.backend.application.api.mapping.StationMapper;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.infra.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService extends PagingAndSortingService<Station, String, StationDTO> implements StationsApiDelegate {

    @Autowired
    public StationService(StationRepository repository, StationMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public ResponseEntity<List<StationDTO>> listStations(Integer size, Integer page, String sort, String filtering) {
        return list(size, page, sort, filtering);
    }

    @Override
    public ResponseEntity<Void> deleteStation(String id) {
        return delete(id);
    }

    @Override
    public ResponseEntity<StationDTO> getStation(String id) {
        return get(id);
    }

    @Override
    public ResponseEntity<StationDTO> replaceStation(String id, StationDTO body) {
        body.setId(id);
        return update(id, body);
    }

    @Override
    public ResponseEntity<StationDTO> createStation(StationDTO body) {
        return save(body);
    }
}
