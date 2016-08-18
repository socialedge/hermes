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
package eu.socialedge.hermes.application.domain.timetable;

import eu.socialedge.hermes.application.domain.DtoMapper;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ScheduleMapper implements DtoMapper<ScheduleData, Schedule> {

    public ScheduleData toDto(Schedule schedule) {
        ScheduleData data = new ScheduleData();

        data.scheduleId = schedule.id().toString();
        data.routeId = schedule.routeId().toString();
        data.description = schedule.name();
        data.scheduleAvailability = schedule.scheduleAvailability();
        data.tripIds = schedule.tripIds().stream().map(TripId::toString).collect(Collectors.toSet());

        return data;
    }

    public Schedule fromDto(ScheduleData data) {
        ScheduleId scheduleId = ScheduleId.of(data.scheduleId);
        RouteId routeId = RouteId.of(data.routeId);
        Set<TripId> tripIds = data.tripIds.stream().map(TripId::of).collect(Collectors.toSet());

        return new Schedule(scheduleId, routeId, data.description, data.scheduleAvailability, tripIds);
    }
}
