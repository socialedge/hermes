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
import eu.socialedge.hermes.backend.application.api.dto.*;
import eu.socialedge.hermes.backend.application.api.mapping.Mapper;
import eu.socialedge.hermes.backend.application.api.mapping.ScheduleMapper;
import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.DwellTimeResolver;
import eu.socialedge.hermes.backend.schedule.repository.ScheduleRepository;
import eu.socialedge.hermes.backend.transit.domain.service.LineRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleService extends PagingAndSortingService<Schedule, String, ScheduleDTO>
    implements SchedulesApiDelegate {

    private final LineRepository lineRepository;

    private final DwellTimeResolver dwellTimeResolver;

    private final Mapper<Trip, TripDTO> tripMapper;
    private final Mapper<Availability, AvailabilityDTO> availabilityMapper;

    @Autowired
    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper, LineRepository lineRepository,
                           DwellTimeResolver dwellTimeResolver, Mapper<Trip, TripDTO> tripMapper,
                           Mapper<Availability, AvailabilityDTO> availabilityMapper) {
        super(repository, mapper);
        this.lineRepository = lineRepository;
        this.dwellTimeResolver = dwellTimeResolver;
        this.tripMapper = tripMapper;
        this.availabilityMapper = availabilityMapper;
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
    public ResponseEntity<List<ScheduleDTO>> listSchedules(Integer size, Integer page, String sort, String filtering) {
        return list(size, page, sort, filtering);
    }

    @Override
    public ResponseEntity<List<CollisionDTO>> listScheduleCollisions(String id, Integer size, Integer page, String sort, String filtering) {
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
    public ResponseEntity<ScheduleDTO> generateSchedule(ScheduleSpecificationDTO spec) {
        val line = lineRepository.findOne(spec.getLineId());
        if (line == null) {
            return ResponseEntity.notFound().build();
        }

        val scheduleBuilder = BasicScheduleGenerator.builder()
            .dwellTimeResolver(dwellTimeResolver)
            .line(line)
            .startTimeInbound(LocalTime.parse(spec.getStartTimeInbound()))
            .endTimeInbound(LocalTime.parse(spec.getEndTimeInbound()))
            .startTimeOutbound(LocalTime.parse(spec.getStartTimeOutbound()))
            .endTimeOutbound(LocalTime.parse(spec.getEndTimeOutbound()))
            .averageSpeed(Quantities.getQuantity(spec.getAverageSpeed()).asType(Speed.class))
            .headway(Duration.parse(spec.getHeadway()))
            .minLayover(Duration.parse(spec.getMinLayover()))
            .availability(availabilityMapper.toDomain(spec.getAvailability()))
            .description(spec.getDescription())
            .build();

        val generatedSchedule = scheduleBuilder.generate();
        val persistedSchedule = repository.save(generatedSchedule);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(persistedSchedule));
    }
}
