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
package eu.socialedge.hermes.domain.timetable.dto;

import eu.socialedge.hermes.domain.SpecificationMapper;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduleSpecificationMapper
        implements SpecificationMapper<ScheduleSpecification, Schedule> {

    public ScheduleSpecification toDto(Schedule schedule) {
        ScheduleSpecification spec = new ScheduleSpecification();

        spec.id = schedule.id().toString();
        spec.routeId = schedule.routeId().toString();
        spec.name = schedule.name();
        spec.scheduleAvailability = schedule.scheduleAvailability();
        spec.tripIds = schedule.tripIds().stream()
                .map(TripId::toString).collect(Collectors.toSet());

        return spec;
    }

    public Schedule fromDto(ScheduleSpecification spec) {
        ScheduleId scheduleId = ScheduleId.of(spec.id);
        RouteId routeId = RouteId.of(spec.routeId);
        Set<TripId> tripIds = spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet());

        return new Schedule(scheduleId, routeId, spec.name,
                            spec.scheduleAvailability, tripIds);
    }
}
