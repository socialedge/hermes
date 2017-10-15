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

import eu.socialedge.hermes.backend.application.api.SchedulesApiDelegate;
import eu.socialedge.hermes.backend.application.api.dto.CollisionDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.application.api.mapping.Mapper;
import eu.socialedge.hermes.backend.application.api.mapping.ScheduleMapper;
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
public class ScheduleService extends PagingAndSortingService<Schedule, String, ScheduleDTO>
        implements SchedulesApiDelegate {

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

    @Override
    public ResponseEntity<List<ScheduleDTO>> listSchedules(Integer size, Integer page, String sort) {
        return list(size, page, sort);
    }

    @Override
    public ResponseEntity<List<CollisionDTO>> listScheduleCollisions(String id, Integer size, Integer page, String sort) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> deleteSchedule(String id) {
        return delete(id);
    }

    @Override
    public ResponseEntity<ScheduleDTO> getSchedule(String id) {
        return get(id);
    }

    @Override
    public ResponseEntity<List<TripDTO>> listScheduleInboundTrips(String id) {

        return inboundTrips(id);
    }

    @Override
    public ResponseEntity<List<TripDTO>> listScheduleOutboundTrips(String id) {
        return outboundTrips(id);
    }

    @Override
    public ResponseEntity<ScheduleDTO> replaceSchedule(String id, ScheduleDTO body) {
        body.setId(id);
        return update(id, body);
    }

    @Override
    public ResponseEntity<ScheduleDTO> createSchedule(ScheduleDTO body) {
        return save(body);
    }
}
