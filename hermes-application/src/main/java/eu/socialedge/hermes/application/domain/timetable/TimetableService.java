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

import eu.socialedge.hermes.application.domain.AlreadyFoundException;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.timetable.Stop;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.timetable.TripRepository;
import eu.socialedge.hermes.domain.timetable.dto.ScheduleSpecification;
import eu.socialedge.hermes.domain.timetable.dto.ScheduleSpecificationMapper;
import eu.socialedge.hermes.domain.timetable.dto.StopSpecificationMapper;
import eu.socialedge.hermes.domain.timetable.dto.TripSpecification;
import eu.socialedge.hermes.domain.timetable.dto.TripSpecificationMapper;
import eu.socialedge.hermes.domain.transit.RouteId;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class TimetableService {

    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;
    private final ScheduleSpecificationMapper scheduleSpecMapper;
    private final TripSpecificationMapper tripSpecMapper;
    private final StopSpecificationMapper stopSpecMapper;

    @Inject
    public TimetableService(ScheduleRepository scheduleRepository, TripRepository tripRepository,
                            ScheduleSpecificationMapper scheduleSpecMapper,
                            TripSpecificationMapper tripSpecMapper,
                            StopSpecificationMapper stopSpecMapper) {
        this.scheduleRepository = scheduleRepository;
        this.tripRepository = tripRepository;
        this.scheduleSpecMapper = scheduleSpecMapper;
        this.tripSpecMapper = tripSpecMapper;
        this.stopSpecMapper = stopSpecMapper;
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

    public void createSchedule(ScheduleSpecification data) {
        scheduleRepository.add(scheduleSpecMapper.fromDto(data));
    }

    public void createTrip(ScheduleId scheduleId, TripSpecification tripSpecification) {
        Trip trip = tripSpecMapper.fromDto(tripSpecification);

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

        if (isNotBlank(spec.name)) {
            persistedSchedule.name(spec.name);
        }

        scheduleRepository.update(persistedSchedule);
    }

    public void updateTrip(ScheduleId scheduleId, TripId tripId, TripSpecification spec) {
        Trip persistedTrip = fetchTrip(scheduleId, tripId);

        if (isNotEmpty(spec.stops)) {
            persistedTrip.stops().clear();

            List<Stop> stops = spec.stops.stream()
                    .map(stopSpecMapper::fromDto).collect(Collectors.toList());
            persistedTrip.stops().addAll(stops);
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
