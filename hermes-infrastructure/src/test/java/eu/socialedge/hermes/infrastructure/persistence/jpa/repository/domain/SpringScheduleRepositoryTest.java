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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.transit.Route;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import eu.socialedge.hermes.domain.transport.VehicleType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain
        .RandomIdGenerator.randomRouteId;
import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain
        .RandomIdGenerator.randomScheduleId;
import static eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain
        .RandomIdGenerator.randomStationId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringV2TestConfig.class, loader = AnnotationConfigContextLoader.class)
@Transactional
public class SpringScheduleRepositoryTest {

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private StationRepository stationRepository;

    @Test @Rollback
    public void shouldCreateAndReturnValidSchedule() throws Exception {
        assertEquals(0, scheduleRepository.size());

        Schedule schedule = randomSchedule();

        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        Optional<Schedule> storedSchedule1520Opt = scheduleRepository.get(schedule.id());
        assertTrue(storedSchedule1520Opt.isPresent());

        assertEquals(schedule, storedSchedule1520Opt.get());
    }

    @Test @Rollback
    public void shouldContainCreatedSchedule() throws Exception {
        Schedule schedule = randomSchedule();

        scheduleRepository.save(schedule);
        assertTrue(scheduleRepository.contains(schedule.id()));
    }

    @Test @Rollback
    public void shouldClearRepository() throws Exception {
        assertEquals(0, scheduleRepository.size());

        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        scheduleRepository.clear();
        assertEquals(0, scheduleRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedSchedule() throws Exception {
        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        Schedule schedule2 = randomSchedule();
        scheduleRepository.save(schedule2);
        assertEquals(2, scheduleRepository.size());

        scheduleRepository.remove(schedule);
        assertEquals(1, scheduleRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedScheduleById() throws Exception {
        Schedule schedule = randomSchedule();
        scheduleRepository.save(schedule);
        assertEquals(1, scheduleRepository.size());

        scheduleRepository.remove(schedule.id());
        assertEquals(0, scheduleRepository.size());
    }

    @Test @Rollback
    public void shouldHaveProperSizeAfterDeletion() throws Exception {
        List<Schedule> schedules = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule(), randomSchedule());

        schedules.forEach(scheduleRepository::save);
        assertEquals(schedules.size(), scheduleRepository.size());

        scheduleRepository.remove(schedules.get(ThreadLocalRandom.current().nextInt(0, schedules.size() - 1)));
        assertEquals(schedules.size() - 1, scheduleRepository.size());
    }

    @Test @Rollback
    public void shouldRemoveCreatedScheduleByIds() throws Exception {
        List<Schedule> schedules = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule(), randomSchedule());

        schedules.forEach(scheduleRepository::save);
        assertEquals(schedules.size(), scheduleRepository.size());

        List<ScheduleId> scheduleIdsToRemove = schedules.stream()
                .skip(ThreadLocalRandom.current().nextInt(0, schedules.size() - 1))
                .map(Schedule::id)
                .collect(Collectors.toList());

        scheduleRepository.remove(scheduleIdsToRemove);
        assertEquals(scheduleIdsToRemove.size(), schedules.size() - scheduleRepository.size());
        assertTrue(scheduleIdsToRemove.stream().noneMatch(id -> scheduleRepository.contains(id)));
    }

    private Schedule randomSchedule() throws MalformedURLException {
        Station station = new Station(randomStationId(), "name1", new Location(11, 11), VehicleType.BUS);
        stationRepository.save(station);

        Route route = new Route(randomRouteId(), Collections.singletonList(station.id()));
        routeRepository.save(route);

        return new Schedule(randomScheduleId(), route.id(),
                ScheduleAvailability.weekendDays(LocalDate.now().minusDays(1), LocalDate.now()));
    }
}