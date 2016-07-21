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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.application.exception.BadRequestException;
import eu.socialedge.hermes.application.exception.NotFoundException;
import eu.socialedge.hermes.application.service.RouteService;
import eu.socialedge.hermes.application.service.ScheduleService;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private RouteService routeService;

    @Mock
    private StationService stationService;

    @Mock
    private ScheduleRepository scheduleRepository;

    private final int scheduleId1 = 1;
    private final Station station1 = new Station("station1", "stationName1", TransportType.BUS, new Position(1, 1));
    private final Route route1 = new Route("routeCode1", Collections.singleton(new Waypoint(station1, 1)));
    private final Route route2 = new Route("routeCode2", Collections.singleton(new Waypoint(station1, 1)));
    private final Departure departure1 = new Departure(station1, LocalTime.now());
    private final Departure departure2 = new Departure(station1, LocalTime.now());
    private final Schedule schedule1 = new Schedule("scheduleName1", route1, new ArrayList(Arrays.asList(departure1)));
    private final Schedule schedule2 = new Schedule("scheduleName2", route2, new ArrayList(Arrays.asList(departure2)));

    @Test
    public void testFetchScheduleSuccess() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        Schedule schedule = scheduleService.fetchSchedule(scheduleId1);

        assertEquals(schedule1, schedule);

        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchScheduleWrongId() {
        scheduleService.fetchSchedule(0);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchScheduleNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.empty());

        scheduleService.fetchSchedule(scheduleId1);


        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testFetchAllSchedulesSuccess() {
        List<Schedule> schedules = Arrays.asList(schedule1, schedule2);
        when(scheduleRepository.list()).thenReturn(schedules);

        assertEquals(schedules, scheduleService.fetchAllSchedules());

        verify(scheduleRepository).list();
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testFetchAllScheduleEmpty() {
        when(scheduleRepository.list()).thenReturn(Collections.emptyList());

        assertTrue(scheduleService.fetchAllSchedules().isEmpty());

        verify(scheduleRepository).list();
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testFetchAllSchedulesByRouteCodeSuccess() {
        List<Schedule> routes = Arrays.asList(schedule1, schedule2);
        when(scheduleRepository.findByRouteCodeId(route1.getCodeId())).thenReturn(routes);

        assertEquals(routes, scheduleService.fetchAllSchedulesByRouteCode(route1.getCodeId()));

        verify(scheduleRepository).findByRouteCodeId(route1.getCodeId());
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testFetchAllSchedulesByRouteCodeEmpty() {
        when(scheduleRepository.findByRouteCodeId(route1.getCodeId())).thenReturn(Collections.emptyList());

        assertTrue(scheduleService.fetchAllSchedulesByRouteCode(route1.getCodeId()).isEmpty());

        verify(scheduleRepository).findByRouteCodeId(route1.getCodeId());
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testFetchDeparturesSuccess() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        assertEquals(schedule1.getDepartures(), scheduleService.fetchDepartures(1));


        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testCreateScheduleSuccess() {
        schedule1.setExpirationDate(LocalDate.now());
        when(routeService.fetchRoute(route1.getCodeId())).thenReturn(route1);
        when(scheduleRepository.store(schedule1)).then(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(schedule1.getDepartures(), schedule.getDepartures());
            assertEquals(schedule1.getExpirationDate(), schedule.getExpirationDate());

            return schedule;
        });

        scheduleService.createSchedule(schedule1.getName(), route1.getCodeId(), schedule1.getDepartures(),
                schedule1.getExpirationDate());

        verify(routeService).fetchRoute(route1.getCodeId());
        verify(scheduleRepository).store(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateScheduleEmptyName() {
        when(routeService.fetchRoute(route1.getCodeId())).thenReturn(route1);

        scheduleService.createSchedule("", route1.getCodeId(), schedule1.getDepartures(), schedule1.getExpirationDate());


        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testCreateScheduleRouteNotFound() {
        schedule1.setExpirationDate(LocalDate.now());
        when(routeService.fetchRoute(route1.getCodeId())).thenThrow(new NotFoundException(""));

        scheduleService.createSchedule(schedule1.getName(), route1.getCodeId(), schedule1.getDepartures(),
                schedule1.getExpirationDate());


        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateScheduleWrongExpirationDate() {
        when(routeService.fetchRoute(route1.getCodeId())).thenReturn(route1);

        scheduleService.createSchedule(schedule1.getName(), route1.getCodeId(), schedule1.getDepartures(),
                LocalDate.now().minusYears(1));


        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testCreateScheduleNullDeparturesAndExpirationDate() {
        schedule1.setExpirationDate(LocalDate.now());
        when(routeService.fetchRoute(route1.getCodeId())).thenReturn(route1);
        when(scheduleRepository.store(schedule1)).then(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertTrue(schedule.getDepartures().isEmpty());
            assertEquals(LocalDate.MAX, schedule.getExpirationDate());

            return schedule;
        });

        scheduleService.createSchedule(schedule1.getName(), route1.getCodeId(), null, null);

        verify(routeService).fetchRoute(route1.getCodeId());
        verify(scheduleRepository).store(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testCreateDepartureSuccess() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));
        when(stationService.fetchStation(station1.getCodeId())).thenReturn(station1);
        when(scheduleRepository.store(schedule1)).then(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(schedule1, schedule);
            assertTrue(schedule.getDepartures().contains(new Departure(departure1.getStation(), departure2.getTime())));

            return schedule;
        });

        scheduleService.createDeparture(1, departure1.getStation().getCodeId(), departure2.getTime());


        verify(scheduleRepository).get(scheduleId1);
        verify(stationService).fetchStation(station1.getCodeId());
        verify(scheduleRepository).store(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testCreateDepartureScheduleNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.empty());

        scheduleService.createDeparture(scheduleId1, departure1.getStation().getCodeId(), departure2.getTime());


        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testCreateDepartureStationNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));
        when(stationService.fetchStation(station1.getCodeId())).thenThrow(new NotFoundException(""));

        scheduleService.createDeparture(scheduleId1, departure1.getStation().getCodeId(), departure2.getTime());


        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testUpdateScheduleSuccess() {
        schedule2.setExpirationDate(LocalDate.now().plusYears(1));
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));
        when(scheduleRepository.store(schedule2)).then(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(scheduleId1, schedule.getId());
            assertEquals(schedule2.getName(), schedule.getName());
            assertEquals(schedule2.getDepartures(), schedule.getDepartures());
            assertEquals(schedule2.getExpirationDate(), schedule.getExpirationDate());

            return schedule;
        });

        scheduleService.updateSchedule(1, schedule2.getName(), schedule2.getDepartures(), schedule2.getExpirationDate());


        verify(scheduleRepository).get(scheduleId1);
        verify(scheduleRepository).store(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateScheduleNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.empty());

        scheduleService.updateSchedule(scheduleId1, schedule1.getName(), schedule1.getDepartures(), LocalDate.now());

        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testUpdateScheduleNotUpdated() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        scheduleService.updateSchedule(scheduleId1, "", Collections.emptyList(), LocalDate.now());

        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testRemoveScheduleSuccess() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        scheduleService.removeSchedule(scheduleId1);

        verify(scheduleRepository).get(scheduleId1);
        verify(scheduleRepository).remove(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveScheduleNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.empty());

        scheduleService.removeSchedule(scheduleId1);

        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test
    public void testRemoveDepartureSuccess() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        scheduleService.removeDeparture(scheduleId1, station1.getCodeId());

        verify(scheduleRepository).get(scheduleId1);
        verify(scheduleRepository).store(schedule1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveDepartureNotFound() {
        when(scheduleRepository.get(scheduleId1)).thenReturn(Optional.of(schedule1));

        scheduleService.removeDeparture(scheduleId1, "wrong id");

        verify(scheduleRepository).get(scheduleId1);
        verifyNoMoreInteractions(scheduleRepository, stationService, routeService);
    }
}
