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

import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecification;
import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecificationMapper;
import eu.socialedge.hermes.domain.timetable.*;
import eu.socialedge.hermes.domain.transit.RouteId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    @InjectMocks
    private TimetableService timetableService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Spy
    private ScheduleSpecificationMapper scheduleDataMapper;

    @Test
    public void testFetchAllSchedulesReturnCollection() throws Exception {
        List<Schedule> scheduleList = Arrays.asList(randomSchedule(), randomSchedule(), randomSchedule());
        when(scheduleRepository.list()).thenReturn(scheduleList);

        Collection<ScheduleSpecification> fetchResult = timetableService.fetchAllSchedules();

        assertEquals(scheduleList, fetchResult.stream().map(scheduleDataMapper::fromDto).collect(Collectors.toList()));
    }

    @Test
    public void testFetchAllSchedulesEmptyResult() throws Exception {
        when(scheduleRepository.list()).thenReturn(Collections.emptyList());

        Collection<ScheduleSpecification> fetchResult = timetableService.fetchAllSchedules();

        assertTrue(fetchResult.isEmpty());
        verify(scheduleRepository).list();
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testFetchScheduleNotFound() throws Exception {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.get(scheduleId)).thenReturn(Optional.empty());

        timetableService.fetchSchedule(scheduleId);
    }

    @Test
    public void testFetchScheduleSuccess() throws Exception {
        Schedule schedule = randomSchedule();
        when(scheduleRepository.get(schedule.id())).thenReturn(Optional.of(schedule));

        ScheduleSpecification data = timetableService.fetchSchedule(schedule.id());

        assertEquals(schedule, scheduleDataMapper.fromDto(data));
        verify(scheduleRepository).get(schedule.id());
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testCreateScheduleWithAllFields() {
        ScheduleSpecification data = scheduleSpecification();

        Mockito.doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertScheduleEqualsToSpec(data, schedule);

            return null;
        }).when(scheduleRepository).add(any(Schedule.class));

        timetableService.createSchedule(data);

        verify(scheduleRepository).add(any(Schedule.class));
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testUpdateScheduleAllFields() throws Exception {
        Schedule scheduleToUpdate = randomSchedule();
        ScheduleSpecification data = scheduleSpecification();
        data.scheduleId = scheduleToUpdate.id().toString();
        when(scheduleRepository.get(scheduleToUpdate.id())).thenReturn(Optional.of(scheduleToUpdate));
        doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(data.scheduleId, schedule.id().toString());
            assertEquals(data.tripIds.stream().map(TripId::of).collect(Collectors.toSet()), schedule.tripIds());

            return null;
        }).when(scheduleRepository).update(scheduleToUpdate);

        timetableService.updateSchedule(scheduleToUpdate.id(), data);

        verify(scheduleRepository).get(scheduleToUpdate.id());
        verify(scheduleRepository).update(scheduleToUpdate);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testUpdateScheduleAllFieldsBlankOrNull() throws Exception {
        Schedule scheduleToUpdate = randomSchedule();
        ScheduleSpecification data = new ScheduleSpecification();
        data.scheduleId = scheduleToUpdate.id().toString();
        data.tripIds = Collections.emptySet();
        when(scheduleRepository.get(scheduleToUpdate.id())).thenReturn(Optional.of(scheduleToUpdate));
        doAnswer(invocation -> {
            Schedule schedule = (Schedule) invocation.getArguments()[0];

            assertEquals(scheduleToUpdate.id(), schedule.id());
            assertEquals(scheduleToUpdate.tripIds(),
                        schedule.tripIds());

            return null;
        }).when(scheduleRepository).update(scheduleToUpdate);

        timetableService.updateSchedule(scheduleToUpdate.id(), data);

        verify(scheduleRepository).get(scheduleToUpdate.id());
        verify(scheduleRepository).update(scheduleToUpdate);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateScheduleNotFound() {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.get(scheduleId)).thenReturn(Optional.empty());

        timetableService.updateSchedule(scheduleId, scheduleSpecification());

        verify(scheduleRepository).get(scheduleId);
        verifyNoMoreInteractions(scheduleRepository);
    }

    @Test
    public void testDeleteSchedule() {
        final ScheduleId scheduleId = ScheduleId.of("scheduleId");
        when(scheduleRepository.remove(scheduleId)).thenReturn(true);

        timetableService.deleteSchedule(scheduleId);

        verify(scheduleRepository).remove(scheduleId);
        verifyNoMoreInteractions(scheduleRepository);
    }

    private void assertScheduleEqualsToSpec(ScheduleSpecification data, Schedule schedule) {
        assertEquals(data.scheduleId, schedule.id().toString());
        assertEquals(data.routeId, schedule.routeId().toString());
        assertEquals(data.scheduleAvailability, schedule.scheduleAvailability());
        assertEquals(data.tripIds.stream().map(TripId::of).collect(Collectors.toSet()), schedule.tripIds());
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
        ScheduleSpecification data = new ScheduleSpecification();
        data.scheduleId = "scheduleId";
        data.routeId = "routeId";
        data.description = "descr";
        data.scheduleAvailability = ScheduleAvailability.weekendDays(LocalDate.now().minusDays(3), LocalDate.now());
        int id = ThreadLocalRandom.current().nextInt(100, 1000);
        data.tripIds = new HashSet<String>() {{
            add(String.valueOf(id + id));
            add(String.valueOf(id + id));
        }};

        return data;
    }
}
