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

import eu.socialedge.hermes.application.exception.BadRequestException;
import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

@Component
@Transactional(readOnly = true)
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final RouteService routeService;
    private final StationService stationService;

    @Inject
    public ScheduleService(ScheduleRepository scheduleRepository, RouteService routeService, StationService stationService) {
        this.scheduleRepository = scheduleRepository;
        this.routeService = routeService;
        this.stationService = stationService;
    }

    @Transactional
    public Schedule createSchedule(String name, String routeCode, Collection<Departure> departures, LocalDate expDate) {
        Route route = routeService.fetchRoute(routeCode);
        Schedule schedule = new Schedule(name, route);

        if (departures != null && !departures.isEmpty())
            schedule.setDepartures(departures);
        if (expDate != null) {
            if (expDate.isBefore(LocalDate.now()))
                throw new BadRequestException("Expiration date cant be before today's date");
            schedule.setExpirationDate(expDate);
        }

        return scheduleRepository.store(schedule);
    }

    @Transactional
    public Departure createDeparture(int scheduleId, String routeCodeId, String stationCode, LocalTime departureTime) {
        Schedule schedule = fetchSchedule(scheduleId);
        Station station = stationService.fetchStation(stationCode);

        Departure departure = Departure.of(station, departureTime);
        schedule.addDeparture(departure);
        scheduleRepository.store(schedule);

        return departure;
    }

    public Schedule fetchSchedule(int scheduleId) {
        if (scheduleId <= 0)
            throw new IllegalArgumentException("Invalid schedule (not > 0)");

        return scheduleRepository.get(scheduleId).orElseThrow(()
                -> new NotFoundException("No schedule found with id = " + scheduleId));
    }

    public Collection<Schedule> fetchAllSchedules() {
        return scheduleRepository.list();
    }

    public Collection<Schedule> fetchAllSchedulesByRouteCode(String routeCode) {
        return scheduleRepository.findByRouteCodeId(routeCode);
    }

    public Collection<Departure> fetchDepartures(int scheduleId) {
        return fetchSchedule(scheduleId).getDepartures();
    }

    @Transactional
    public void updateSchedule(int scheduleId, String name, Collection<Departure> departures, LocalDate expDate) {
        Schedule scheduleToPatch = fetchSchedule(scheduleId);
        boolean wasUpdated = false;

        if (StringUtils.isNotBlank(name)) {
            scheduleToPatch.setName(name);
            wasUpdated = true;
        }

        if (expDate != null && expDate.isBefore(LocalDate.now())) {
            scheduleToPatch.setExpirationDate(expDate);
            wasUpdated = true;
        }

        if (departures != null && !departures.isEmpty()) {
            scheduleToPatch.setDepartures(departures);
            wasUpdated = true;
        }

        if (wasUpdated)
            scheduleRepository.store(scheduleToPatch);
    }

    @Transactional
    public void removeSchedule(int scheduleId) {
        scheduleRepository.remove(fetchSchedule(scheduleId));
    }

    @Transactional
    public void removeDeparture(int scheduleId, String stationCodeId) {
        Schedule schedule = fetchSchedule(scheduleId);

        if (!schedule.removeDeparture(stationCodeId))
            throw new NotFoundException("No station on the route found with code id = " + stationCodeId);

        scheduleRepository.store(schedule);
    }
}
