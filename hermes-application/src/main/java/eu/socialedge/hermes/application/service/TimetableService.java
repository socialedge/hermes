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
import eu.socialedge.hermes.application.resource.spec.TripSpecification;
import eu.socialedge.hermes.domain.timetable.*;
import eu.socialedge.hermes.domain.transit.Line;
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
public class TimetableService {

    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;

    @Inject
    public TimetableService(ScheduleRepository scheduleRepository, TripRepository tripRepository) {
        this.scheduleRepository = scheduleRepository;
        this.tripRepository = tripRepository;
    }

    public Collection<Schedule> fetchAllSchedules() {
        return scheduleRepository.list();
    }

    public Collection<Trip> fetchAllTrips(ScheduleId scheduleId) {
        return fetchSchedule(scheduleId).tripIds().stream()
                .map(this::fetchTrip)
                .collect(Collectors.toList());
    }

    public Collection<Schedule> fetchAllSchedulesByRouteId(RouteId routeId) {
        return scheduleRepository.findSchedulesByRouteId(routeId);
    }

    public Schedule fetchSchedule(ScheduleId scheduleId) {
        return scheduleRepository.get(scheduleId).orElseThrow(()
                    -> new NotFoundException("Schedule not found. Id = " + scheduleId));
    }

    public Trip fetchTrip(ScheduleId scheduleId, TripId tripId) {
        if (!fetchSchedule(scheduleId).tripIds().contains(tripId))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        return tripRepository.get(tripId).orElseThrow(()
                -> new NotFoundException("Trip not found. Id = " + tripId));
    }

    private Trip fetchTrip(TripId tripId) {
        return tripRepository.get(tripId).orElseThrow(()
                -> new NotFoundException("Trip not found. Id = " + tripId));
    }

    public void createSchedule(ScheduleSpecification spec) {
        Set<TripId> tripIds = spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet());

        Schedule schedule = new Schedule(ScheduleId.of(spec.scheduleId), RouteId.of(spec.routeId),
                                            spec.description, spec.scheduleAvailability, tripIds);
        scheduleRepository.add(schedule);
    }

    public void createTrip(ScheduleId scheduleId, TripSpecification spec) {
        Trip trip = new Trip(TripId.of(spec.tripId), spec.stops);

        tripRepository.add(trip);

        Schedule schedule = fetchSchedule(scheduleId);
        boolean wasAttached = schedule.tripIds().add(trip.id());

        if (!wasAttached)
            throw new AlreadyFoundException("Schedule already attached");

        scheduleRepository.update(schedule);
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

    public void updateTrip(ScheduleId scheduleId, TripId tripId, TripSpecification spec) {
        Trip persistedTrip = fetchTrip(scheduleId, tripId);

        if (isNotEmpty(spec.stops)) {
            persistedTrip.stops().clear();
            persistedTrip.stops().addAll(spec.stops);
        }

        tripRepository.update(persistedTrip);
    }

    public void deleteSchedule(ScheduleId scheduleId) {
        boolean wasRemoved = scheduleRepository.remove(scheduleId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find schedule to delete. Id = " + scheduleId);
    }

    public void deleteTrip(ScheduleId scheduleId, TripId tripId) {
        Schedule schedule = fetchSchedule(scheduleId);

        if (!schedule.tripIds().contains(tripId))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        boolean wasRemoved = tripRepository.remove(tripId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find trip to delete. Id = " + tripId);

        schedule.tripIds().remove(tripId);
        scheduleRepository.update(schedule);
    }
}
