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

import eu.socialedge.hermes.backend.application.api.AgenciesApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.AgencyDTO;
import eu.socialedge.hermes.backend.application.api.mapping.AgencyMapper;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.provider.AgencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgencyService extends PagingAndSortingService<Agency, String, AgencyDTO> implements AgenciesApiDelegate {

    @Autowired
    public AgencyService(AgencyRepository repository, AgencyMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public ResponseEntity<List<AgencyDTO>> listAgencies(Integer size, Integer page, String sort, String filtering) {
        return list(size, page, sort, filtering);
    }

    @Override
    public ResponseEntity<Void> deleteAgency(String id) {
        return delete(id);
    }

    @Override
    public ResponseEntity<AgencyDTO> getAgency(String id) {
        return get(id);
    }

    @Override
    public ResponseEntity<AgencyDTO> replaceAgency(String id, AgencyDTO body) {
        body.setId(id);
        return update(id, body);
    }

    @Override
    public ResponseEntity<AgencyDTO> createAgency(AgencyDTO body) {
        return save(body);
    }
}
