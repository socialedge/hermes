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
package eu.socialedge.hermes.infrastructure.persistence.jpa.mapping;

import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.Trip;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaRoute;
import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.JpaSchedule;
import eu.socialedge.hermes.infrastructure.persistence.jpa.repository.entity.SpringJpaRouteRepository;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;

@Component
public class ScheduleEntityMapper implements EntityMapper<Schedule, JpaSchedule> {

    private final SpringJpaRouteRepository jpaRouteRepository;
    private final TripEntityMapper tripEntityMapper;
    private final ScheduleAvailabilityEntityMapper availabilityEntityMapper;

    @Inject
    public ScheduleEntityMapper(SpringJpaRouteRepository jpaRouteRepository,
                                TripEntityMapper tripEntityMapper,
                                ScheduleAvailabilityEntityMapper availabilityEntityMapper) {
        this.jpaRouteRepository = jpaRouteRepository;
        this.tripEntityMapper = tripEntityMapper;
        this.availabilityEntityMapper = availabilityEntityMapper;
    }

    @Override
    public JpaSchedule mapToEntity(Schedule schedule) {
        JpaSchedule jpaSchedule = new JpaSchedule();

        jpaSchedule.scheduleId(schedule.toString());
        jpaSchedule.route(findRouteById(schedule.routeId()));
        jpaSchedule.scheduleAvailability(availabilityEntityMapper
                .mapToEntity(schedule.scheduleAvailability()));
        jpaSchedule.trips(schedule.stream()
                .map(tripEntityMapper::mapToEntity)
                .collect(Collectors.toList()));

        return jpaSchedule;
    }

    @Override
    public Schedule mapToDomain(JpaSchedule jpaSchedule) {
        ScheduleId scheduleId = ScheduleId.of(jpaSchedule.scheduleId());
        RouteId routeId = RouteId.of(jpaSchedule.route().routeId());
        ScheduleAvailability scheduleAvailability = availabilityEntityMapper
                .mapToDomain(jpaSchedule.scheduleAvailability());
        Collection<Trip> trips = jpaSchedule.trips().stream()
                .map(tripEntityMapper::mapToDomain)
                .collect(Collectors.toList());

        return new Schedule(scheduleId, routeId, scheduleAvailability, trips);
    }

    private JpaRoute findRouteById(RouteId routeId) {
        return jpaRouteRepository.findOne(routeId.toString());
    }
}
