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

package eu.socialedge.hermes.backend.application.api.v2.service;

import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.application.api.v2.mapping.Mapper;
import eu.socialedge.hermes.backend.application.api.v2.mapping.ScheduleMapper;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService extends PagingAndSortingService<Schedule, String, ScheduleDTO> {

    private final Mapper<Trip, TripDTO> tripMapper;

    @Autowired
    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper, Mapper<Trip, TripDTO> tripMapper) {
        super(repository, mapper);
        this.tripMapper = tripMapper;
    }

    public ResponseEntity<List<TripDTO>> outboundTrips(String id) {
        val entity = repository.findOne(id);
        if (entity == null)
            return ResponseEntity.notFound().build();

        val outboundTrips = entity.getOutboundTrips();
        return new ResponseEntity<>(tripMapper.toDTO(outboundTrips), HttpStatus.OK);
    }

    public ResponseEntity<List<TripDTO>> inboundTrips(String id) {
        val entity = repository.findOne(id);
        if (entity == null)
            return ResponseEntity.notFound().build();

        val inboundTrips = entity.getInboundTrips();
        return new ResponseEntity<>(tripMapper.toDTO(inboundTrips), HttpStatus.OK);
    }
}
