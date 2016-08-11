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
import eu.socialedge.hermes.domain.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    public void testFetchAllSchedulesReturnCollection() throws Exception {
        List<Schedule> scheduleList = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule());
        when(scheduleRepository.list()).thenReturn(scheduleList);

        Collection<Schedule> fetchResult = scheduleService.fetchAllSchedules();

        assertEquals(scheduleList, fetchResult);
    }

    @Test
    public void testFetchAllSchedulesEmptyResult() throws Exception {
        when(scheduleRepository.list()).thenReturn(Collections.emptyList());

        Collection<Schedule> fetchResult = scheduleService.fetchAllSchedules();

        assertTrue(fetchResult.isEmpty());
        verify(scheduleRepository).list();
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchScheduleNotFound() throws Exception {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.get(scheduleId)).thenReturn(Optional.empty());

        scheduleService.fetchSchedule(scheduleId);
    }

    @Test
    public void testCreateScheduleWithAllFields() {
        ScheduleSpecification spec = scheduleSpecification();

        Mockito.doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertScheduleEqualsToSpec(spec, schedule);

            return null;
        }).when(scheduleRepository).add(any(Schedule.class));

        scheduleService.createSchedule(spec);

        verify(scheduleRepository).add(any(Schedule.class));
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testUpdateScheduleAllFields() throws Exception {
        Schedule scheduleToUpdate = randomSchedule();
        ScheduleSpecification spec = scheduleSpecification();
        spec.scheduleId = scheduleToUpdate.id().toString();
        when(scheduleRepository.get(scheduleToUpdate.id())).thenReturn(Optional.of(scheduleToUpdate));
        doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(spec.scheduleId, schedule.id().toString());
            assertEquals(spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet()), schedule.tripIds());

            return null;
        }).when(scheduleRepository).update(scheduleToUpdate);

        scheduleService.updateSchedule(scheduleToUpdate.id(), spec);

        verify(scheduleRepository).get(scheduleToUpdate.id());
        verify(scheduleRepository).update(scheduleToUpdate);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testUpdateScheduleAllFieldsBlankOrNull() throws Exception {
        Schedule scheduleToUpdate = randomSchedule();
        ScheduleSpecification spec = new ScheduleSpecification();
        spec.scheduleId = scheduleToUpdate.id().toString();
        spec.tripIds = Collections.emptySet();
        when(scheduleRepository.get(scheduleToUpdate.id())).thenReturn(Optional.of(scheduleToUpdate));
        doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(scheduleToUpdate.id(), schedule.id());
            assertEquals(scheduleToUpdate.tripIds(),
                        schedule.tripIds());

            return null;
        }).when(scheduleRepository).update(scheduleToUpdate);

        scheduleService.updateSchedule(scheduleToUpdate.id(), spec);

        verify(scheduleRepository).get(scheduleToUpdate.id());
        verify(scheduleRepository).update(scheduleToUpdate);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateScheduleNotFound() {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.get(scheduleId)).thenReturn(Optional.empty());

        scheduleService.updateSchedule(scheduleId, scheduleSpecification());

        verify(scheduleRepository).get(scheduleId);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testDeleteSchedule() {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.remove(scheduleId)).thenReturn(true);

        scheduleService.deleteSchedule(scheduleId);

        verify(scheduleRepository).remove(scheduleId);
        verifyNoMoreInteractions(scheduleRepository);
    }

    private void assertScheduleEqualsToSpec(ScheduleSpecification spec, Schedule schedule) {
        assertEquals(spec.scheduleId, schedule.id().toString());
        assertEquals(spec.routeId, schedule.routeId().toString());
        assertEquals(spec.scheduleAvailability, schedule.scheduleAvailability());
        assertEquals(spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet()), schedule.tripIds());
    }

    private Schedule randomSchedule() throws Exception {
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        ScheduleAvailability availability = ScheduleAvailability.weekendDays(LocalDate.now().minusDays(5), LocalDate.now());
        Set<TripId> tripIds = new HashSet<TripId>() {{
           add(TripId.of(String.valueOf(id + id)));
            add(TripId.of(String.valueOf(id + id)));
        }};
        return new Schedule(ScheduleId.of("schedule" + id), RouteId.of("route" + id), "123",
                availability, tripIds);
    }

    private ScheduleSpecification scheduleSpecification() {
        ScheduleSpecification spec = new ScheduleSpecification();
        spec.scheduleId = "scheduleId";
        spec.routeId = "routeId";
        spec.description = "descr";
        spec.scheduleAvailability = ScheduleAvailability.weekendDays(LocalDate.now().minusDays(3), LocalDate.now());
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        spec.tripIds = new HashSet<String>() {{
            add(String.valueOf(id + id));
            add(String.valueOf(id + id));
        }};

        return spec;
    }
}
