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
package eu.socialedge.hermes.application.v2.service;

import eu.socialedge.hermes.application.v2.resource.spec.ScheduleSpecification;
import eu.socialedge.hermes.domain.v2.timetable.Schedule;
import eu.socialedge.hermes.domain.v2.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.v2.timetable.ScheduleId;
import eu.socialedge.hermes.domain.v2.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.v2.timetable.Trip;
import eu.socialedge.hermes.domain.v2.transit.RouteId;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

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

    public Optional<Schedule> fetchSchedule(ScheduleId scheduleId) {
        return scheduleRepository.get(scheduleId);
    }

    public void createSchedule(ScheduleSpecification spec) {
        ScheduleId scheduleId = ScheduleId.of(spec.scheduleId);
        RouteId routeId = RouteId.of(spec.routeId);
        ScheduleAvailability scheduleAvailability = spec.scheduleAvailability;
        Collection<Trip> trips = spec.trips.stream()
                .filter(stops -> !stops.isEmpty())
                .map(Trip::new)
                .collect(Collectors.toList());

        Schedule schedule = new Schedule(scheduleId, routeId, scheduleAvailability, trips);

        scheduleRepository.save(schedule);
    }

    public void updateSchedule(ScheduleSpecification spec) {
        ScheduleId scheduleId = ScheduleId.of(spec.scheduleId);

        Optional<Schedule> persistedScheduleOpt = fetchSchedule(scheduleId);
        if (!persistedScheduleOpt.isPresent())
            throw new ServiceException("Failed to find Schedule to update. Id = " + scheduleId);

        Schedule persistedSchedule = persistedScheduleOpt.get();

        if (isNotEmpty(spec.trips)) {
            persistedSchedule.removeAllTrips();

            spec.trips.stream()
                    .filter(stops -> !stops.isEmpty())
                    .map(Trip::new)
                    .forEach(persistedSchedule::addTrip);
        }

        scheduleRepository.save(persistedSchedule);
    }

    public boolean deleteSchedule(ScheduleId scheduleId) {
        return scheduleRepository.remove(scheduleId);
    }
}
