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

package eu.socialedge.hermes.backend.application.api.mapping;

import eu.socialedge.hermes.backend.application.api.dto.AvailabilityDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.application.api.mapping.util.Entities;
import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper implements Mapper<Schedule, ScheduleDTO> {

    private final Mapper<Availability, AvailabilityDTO> availabilityMapper;
    private final Mapper<Trip, TripDTO> tripMapper;

    @Autowired
    public ScheduleMapper(Mapper<Availability, AvailabilityDTO> availabilityMapper,
                          Mapper<Trip, TripDTO> tripMapper) {
        this.availabilityMapper = availabilityMapper;
        this.tripMapper = tripMapper;
    }

    @Override
    public ScheduleDTO toDTO(Schedule schedule) {
        if (schedule == null)
            return null;

        // inbound and outbound trips are write only
        return new ScheduleDTO()
            .id(schedule.getId())
            .description(schedule.getDescription())
            .lineId(schedule.getLine().getId())
            .availability(availabilityMapper.toDTO(schedule.getAvailability()));
    }

    @Override
    public Schedule toDomain(ScheduleDTO dto) {
        if (dto == null)
            return null;

        try {
            return new Schedule.Builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .line(Entities.proxy(Line.class, dto.getLineId()))
                .availability(availabilityMapper.toDomain(dto.getAvailability()))
                .outboundTrips(tripMapper.toDomain(dto.getOutboundTrips()))
                .inboundTrips(tripMapper.toDomain(dto.getInboundTrips()))
                .build();
        } catch (ReflectiveOperationException e) {
            throw new MappingException("Failed to create proxy Line entity", e);
        }
    }
}
