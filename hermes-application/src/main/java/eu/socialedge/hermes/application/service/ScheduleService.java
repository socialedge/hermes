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
package eu.socialedge.hermes.application.service;

import eu.socialedge.hermes.application.resource.spec.ScheduleSpecification;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Inject
    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Collection<Schedule> fetchAllSchedules() {
        return scheduleRepository.list();
    }

    public Collection<Schedule> fetchAllSchedulesByRouteId(RouteId routeId) {
        return scheduleRepository.findSchedulesByRouteId(routeId);
    }

    public Schedule fetchSchedule(ScheduleId scheduleId) {
        return scheduleRepository.get(scheduleId).orElseThrow(()
                    -> new NotFoundException("Schedule not found. Id = " + scheduleId));
    }

    public void createSchedule(ScheduleSpecification spec) {
        Set<TripId> tripIds = spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet());

        Schedule schedule = new Schedule(ScheduleId.of(spec.scheduleId), RouteId.of(spec.routeId),
                                            spec.description, spec.scheduleAvailability, tripIds);
        scheduleRepository.add(schedule);
    }

    public void updateSchedule(ScheduleId scheduleId, ScheduleSpecification spec) {
        Schedule persistedSchedule = fetchSchedule(scheduleId);

        if (isNotEmpty(spec.tripIds)) {
            persistedSchedule.tripIds().clear();

            spec.tripIds.stream()
                    .map(TripId::of)
                    .forEach(trip -> persistedSchedule.tripIds().add(trip));
        }

        if (isNotBlank(spec.description)) {
            persistedSchedule.name(spec.description);
        }

        scheduleRepository.update(persistedSchedule);
    }

    public void deleteSchedule(ScheduleId scheduleId) {
        boolean wasRemoved = scheduleRepository.remove(scheduleId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find schedule to delete. Id = " + scheduleId);
    }
}
