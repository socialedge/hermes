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
import eu.socialedge.hermes.domain.timetable.*;
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
    private final ScheduleMapper scheduleMapper;
    private final TripMapper tripMapper;

    @Inject
    public TimetableService(ScheduleRepository scheduleRepository, TripRepository tripRepository,
                            ScheduleMapper scheduleMapper, TripMapper tripMapper) {
        this.scheduleRepository = scheduleRepository;
        this.tripRepository = tripRepository;
        this.scheduleMapper = scheduleMapper;
        this.tripMapper = tripMapper;
    }

    public Collection<ScheduleData> fetchAllSchedules() {
        return scheduleRepository.list().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<TripData> fetchAllTrips(ScheduleId scheduleId) {
        return fetchSchedule(scheduleId).tripIds.stream()
                .map(TripId::of)
                .map(this::fetchTrip)
                .collect(Collectors.toList());
    }

    public Collection<ScheduleData> fetchAllSchedulesByRouteId(RouteId routeId) {
        return scheduleRepository.findSchedulesByRouteId(routeId).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public ScheduleData fetchSchedule(ScheduleId scheduleId) {
        Schedule schedule = scheduleRepository.get(scheduleId).orElseThrow(()
                    -> new NotFoundException("Schedule not found. Id = " + scheduleId));

        return scheduleMapper.toDto(schedule);
    }

    public TripData fetchTrip(ScheduleId scheduleId, TripId tripId) {
        if (!fetchSchedule(scheduleId).tripIds.contains(tripId.toString()))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        return fetchTrip(tripId);
    }

    private TripData fetchTrip(TripId tripId) {
        Trip trip = tripRepository.get(tripId).orElseThrow(()
                -> new NotFoundException("Trip not found. Id = " + tripId));

        return tripMapper.toDto(trip);
    }

    public void createSchedule(ScheduleData data) {
        scheduleRepository.add(scheduleMapper.fromDto(data));
    }

    public void createTrip(ScheduleId scheduleId, TripData tripData) {
        Trip trip = new Trip(TripId.of(tripData.tripId), tripData.stops);

        tripRepository.add(trip);

        ScheduleData scheduleData = fetchSchedule(scheduleId);
        boolean wasAttached = scheduleData.tripIds.add(trip.id().toString());

        if (!wasAttached)
            throw new AlreadyFoundException("Schedule already attached");

        scheduleRepository.update(scheduleMapper.fromDto(scheduleData));
    }

    public void updateSchedule(ScheduleId scheduleId, ScheduleData data) {
        ScheduleData persistedScheduleData = fetchSchedule(scheduleId);

        if (isNotEmpty(data.tripIds)) {
            persistedScheduleData.tripIds = data.tripIds;
        }

        if (isNotBlank(data.description)) {
            persistedScheduleData.description = data.description;
        }

        scheduleRepository.update(scheduleMapper.fromDto(persistedScheduleData));
    }

    public void updateTrip(ScheduleId scheduleId, TripId tripId, TripData tripData) {
        TripData persistedTripData = fetchTrip(scheduleId, tripId);

        if (isNotEmpty(tripData.stops)) {
            persistedTripData.stops = tripData.stops;
        }

        tripRepository.update(tripMapper.fromDto(persistedTripData));
    }

    public void deleteSchedule(ScheduleId scheduleId) {
        boolean wasRemoved = scheduleRepository.remove(scheduleId);

        if (!wasRemoved)
            throw new NotFoundException("Failed to find schedule to delete. Id = " + scheduleId);
    }

    public void deleteTrip(ScheduleId scheduleId, TripId tripId) {
        ScheduleData scheduleData = fetchSchedule(scheduleId);

        if (!scheduleData.tripIds.contains(tripId.toString()))
            throw new NotFoundException("Schedule doesn't contain trip with id = " + tripId);

        boolean wasRemoved = tripRepository.remove(tripId);
        if (!wasRemoved)
            throw new NotFoundException("Failed to find trip to delete. Id = " + tripId);

        scheduleData.tripIds.remove(tripId.toString());
        scheduleRepository.update(scheduleMapper.fromDto(scheduleData));
    }
}
