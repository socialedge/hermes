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

import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
import eu.socialedge.hermes.backend.application.api.mapping.LineMapper;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineService extends PagingAndSortingService<Line, String, LineDTO> {

    @Autowired
    public LineService(LineRepository repository, LineMapper mapper) {
        super(repository, mapper);
    }
}
