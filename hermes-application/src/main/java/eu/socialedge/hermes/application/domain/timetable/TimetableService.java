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
import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecification;
import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecificationMapper;
import eu.socialedge.hermes.application.domain.timetable.dto.TripSpecification;
import eu.socialedge.hermes.application.domain.timetable.dto.TripSpecificationMapper;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.timetable.TripRepository;
import eu.socialedge.hermes.domain.transit.RouteId;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import static eu.socialedge.hermes.util.Iterables.isNotEmpty;
import static eu.socialedge.hermes.util.Strings.isNotBlank;

@Component
public class TimetableService {

    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;
    private final ScheduleSpecificationMapper scheduleSpecificationMapper;
    private final TripSpecificationMapper tripSpecificationMapper;

    @Inject
    public TimetableService(ScheduleRepository scheduleRepository, TripRepository tripRepository,
                            ScheduleSpecificationMapper scheduleSpecificationMapper, TripSpecificationMapper tripSpecificationMapper) {
        this.scheduleRepository = scheduleRepository;
        this.tripRepository = tripRepository;
        this.scheduleSpecificationMapper = scheduleSpecificationMapper;
        this.tripSpecificationMapper = tripSpecificationMapper;
    }

    public Collection<ScheduleSpecification> fetchAllSchedules() {
        return scheduleRepository.list().stream()
                .map(scheduleSpecificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<TripSpecification> fetchAllTrips(ScheduleId scheduleId) {
        return fetchSchedule(scheduleId).tripIds.stream()
                .map(TripId::of)
                .map(this::fetchTrip)
                .collect(Collectors.toList());
    }

    public Collection<ScheduleSpecification> fetchAllSchedulesByRouteId(RouteId routeId) {
        return scheduleRepository.findSchedulesByRouteId(routeId).stream()
                .map(scheduleSpecificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ScheduleSpecification fetchSchedule(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.get(scheduleId).orElseThrow(()
                    -> new NotFoundException("Schedule not found. Id = " + scheduleId));

        return scheduleSpecificationMapper.toDto(schedule);
    }

    public TripSpecification fetchTrip(ScheduleId scheduleId, TripId tripId) {
        if (!fetchSchedule(scheduleId).tripIds.contains(tripId.toString()))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        return fetchTrip(tripId);
    }

    private TripSpecification fetchTrip(TripId tripId) {
        Trip trip = tripRepository.get(tripId).orElseThrow(()
                -> new NotFoundException("Trip not found. Id = " + tripId));

        return tripSpecificationMapper.toDto(trip);
    }

    public void createSchedule(ScheduleSpecification data) {
        scheduleRepository.add(scheduleSpecificationMapper.fromDto(data));
    }

    public void createTrip(ScheduleId scheduleId, TripSpecification tripSpecification) {
        Trip trip = tripSpecificationMapper.fromDto(tripSpecification);

        tripRepository.add(trip);

        ScheduleSpecification scheduleSpecification = fetchSchedule(scheduleId);
        boolean wasAttached = scheduleSpecification.tripIds.add(trip.id().toString());

        if (!wasAttached)
            throw new AlreadyFoundException("Schedule already attached");

        scheduleRepository.update(scheduleSpecificationMapper.fromDto(scheduleSpecification));
    }

    public void updateSchedule(ScheduleId scheduleId, ScheduleSpecification data) {
        ScheduleSpecification persistedScheduleSpecification = fetchSchedule(scheduleId);

        if (isNotEmpty(data.tripIds)) {
            persistedScheduleSpecification.tripIds = data.tripIds;
        }

        if (isNotBlank(data.description)) {
            persistedScheduleSpecification.description = data.description;
        }

        scheduleRepository.update(scheduleSpecificationMapper.fromDto(persistedScheduleSpecification));
    }

    public void updateTrip(ScheduleId scheduleId, TripId tripId, TripSpecification tripSpecification) {
        TripSpecification persistedTripSpecification = fetchTrip(scheduleId, tripId);

        if (isNotEmpty(tripSpecification.stops)) {
            persistedTripSpecification.stops = tripSpecification.stops;
        }

        tripRepository.update(tripSpecificationMapper.fromDto(persistedTripSpecification));
    }

    public void deleteSchedule(ScheduleId scheduleId) {
        boolean wasRemoved = scheduleRepository.remove(scheduleId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find schedule to delete. Id = " + scheduleId);
    }

    public void deleteTrip(ScheduleId scheduleId, TripId tripId) {
        ScheduleSpecification scheduleSpecification = fetchSchedule(scheduleId);

        if (!scheduleSpecification.tripIds.contains(tripId.toString()))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        boolean wasRemoved = tripRepository.remove(tripId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find trip to delete. Id = " + tripId);

        scheduleSpecification.tripIds.remove(tripId.toString());
        scheduleRepository.update(scheduleSpecificationMapper.fromDto(scheduleSpecification));
    }
}
