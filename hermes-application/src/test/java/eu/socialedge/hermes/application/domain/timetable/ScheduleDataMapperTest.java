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

import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleAvailability;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ScheduleDataMapperTest {

    @Test
    public void testToData() {
        Schedule schedule = new Schedule(ScheduleId.of("scheduleId"),
                RouteId.of("routeId"),
                "schedule",
                ScheduleAvailability.workingDays(LocalDate.now().minusDays(5), LocalDate.now()),
                new HashSet<TripId>() {{
                    add(TripId.of("trip1"));
                    add(TripId.of("trip2"));
                    add(TripId.of("trip3"));
                }});

        ScheduleData data = ScheduleDataMapper.toData(schedule);

        assertEquals(schedule.id().toString(), data.scheduleId);
        assertEquals(schedule.name(), data.description);
        assertEquals(schedule.routeId().toString(), data.routeId);
        assertEquals(schedule.scheduleAvailability(), data.scheduleAvailability);
        assertEquals(schedule.tripIds(), data.tripIds.stream().map(TripId::of).collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        ScheduleData data = new ScheduleData();
        data.scheduleId = "scheduleId";
        data.description = "schedule";;
        data.routeId = "routeId";
        data.scheduleAvailability = ScheduleAvailability.workingDays(LocalDate.now().minusDays(5), LocalDate.now());
        data.tripIds = new HashSet<String>() {{
            add("trip1");
            add("trip2");
            add("trip2");
        }};

        Schedule schedule = ScheduleDataMapper.fromData(data);


        assertEquals(data.scheduleId, schedule.id().toString());
        assertEquals(data.description, schedule.name());
        assertEquals(data.routeId, schedule.routeId().toString());
        assertEquals(data.scheduleAvailability, schedule.scheduleAvailability());
        assertEquals(data.tripIds.stream().map(TripId::of)
                .collect(Collectors.toSet()), schedule.tripIds());
    }
}
