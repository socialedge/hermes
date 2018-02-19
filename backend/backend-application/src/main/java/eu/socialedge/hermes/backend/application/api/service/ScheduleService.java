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
import eu.socialedge.hermes.backend.schedule.domain.gen.StaticTripFactory;
import eu.socialedge.hermes.backend.schedule.domain.gen.StopFactory;
import eu.socialedge.hermes.backend.schedule.domain.gen.TransitConstraints;
import eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
public class ScheduleService extends PagingAndSortingService<Schedule, String, ScheduleDTO>
    implements SchedulesApiDelegate {

    private final LineRepository lineRepository;

    private final StopFactory stopFactory;

    private final Mapper<Trip, TripDTO> tripMapper;
    private final Mapper<Availability, AvailabilityDTO> availabilityMapper;

    @Autowired
    public ScheduleService(ScheduleRepository repository, ScheduleMapper mapper, LineRepository lineRepository,
                           StopFactory stopFactory, Mapper<Trip, TripDTO> tripMapper,
                           Mapper<Availability, AvailabilityDTO> availabilityMapper) {
        super(repository, mapper);
        this.lineRepository = lineRepository;
        this.stopFactory = stopFactory;
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
    public ResponseEntity<List<ScheduleDTO>> listSchedules(String lineId, String stationId, Integer size,
                                                           Integer page, String sort, String filtering) {
        val conditions = new ArrayList<Predicate<ScheduleDTO>>();
        if (isNotBlank(lineId)) {
            conditions.add(scheduleDTO -> lineId.equals(scheduleDTO.getLineId()));
        }
        if (isNotBlank(stationId)) {
            conditions.add(scheduleDTO -> containsStation(scheduleDTO, stationId));
        }
        ResponseEntity<List<ScheduleDTO>> result = list(size, page, sort, filtering, conditions);
        for (ScheduleDTO scheduleDTO : result.getBody()) {
            stripTrips(scheduleDTO);
        }
        return result;
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
    public ResponseEntity<ScheduleDTO> getSchedule(String id, Boolean full) {
        ResponseEntity<ScheduleDTO> result = get(id);
        if (!TRUE.equals(full)) {
            stripTrips(result.getBody());
        }
        return result;
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
        ResponseEntity<ScheduleDTO> result = update(id, body);
        stripTrips(result.getBody());
        return result;
    }

    @Override
    public ResponseEntity<ScheduleDTO> generateSchedule(ScheduleSpecificationDTO spec) {
        val line = lineRepository.findOne(spec.getLineId());
        if (line == null) {
            return ResponseEntity.notFound().build();
        }

        val averageSpeed = Quantities.getQuantity(spec.getAverageSpeed()).asType(Speed.class);
        val tripFactory = new StaticTripFactory(stopFactory, averageSpeed);
        val scheduleGenerator = new BasicScheduleGenerator(tripFactory);

        val availability = availabilityMapper.toDomain(spec.getAvailability());
        val schedule = scheduleGenerator.generate(line, availability, spec.getDescription(), toTransitConstraint(spec));

        val persistedSchedule = repository.save(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(stripTrips(mapper.toDTO(persistedSchedule)));
    }

    private static TransitConstraints toTransitConstraint(ScheduleSpecificationDTO spec) {
        val startTimeInbound = LocalTime.parse(spec.getStartTimeInbound());
        val endTimeInbound = LocalTime.parse(spec.getEndTimeInbound());
        val startTimeOutbound = LocalTime.parse(spec.getStartTimeOutbound());
        val endTimeOutbound = LocalTime.parse(spec.getEndTimeOutbound());
        val headway = Duration.ofSeconds(spec.getHeadway());
        val minLayover = Duration.ofSeconds(spec.getMinLayover());
        return new TransitConstraints(startTimeInbound, endTimeInbound,
            startTimeOutbound, endTimeOutbound, headway, minLayover);
    }

    private static ScheduleDTO stripTrips(ScheduleDTO scheduleDTO) {
        return scheduleDTO.inboundTrips(null).outboundTrips(null);
    }

    private static boolean containsStation(ScheduleDTO scheduleDTO, String stationId) {
        val trips = new ArrayList<TripDTO>(scheduleDTO.getInboundTrips());
        trips.addAll(scheduleDTO.getOutboundTrips());
        return trips.stream()
            .map(TripDTO::getStops)
            .flatMap(List::stream)
            .anyMatch(stopDTO -> stationId.equals(stopDTO.getStationId()));
    }
}
