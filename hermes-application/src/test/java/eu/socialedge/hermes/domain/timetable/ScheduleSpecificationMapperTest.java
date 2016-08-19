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

import eu.socialedge.hermes.domain.timetable.dto.ScheduleSpecification;
import eu.socialedge.hermes.domain.timetable.dto.ScheduleSpecificationMapper;
import eu.socialedge.hermes.domain.transit.RouteId;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ScheduleSpecificationMapperTest {

    private ScheduleSpecificationMapper scheduleDataMapper = new ScheduleSpecificationMapper();

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

        ScheduleSpecification spec = scheduleDataMapper.toDto(schedule);

        assertEquals(schedule.id().toString(), spec.id);
        assertEquals(schedule.name(), spec.name);
        assertEquals(schedule.routeId().toString(), spec.routeId);
        assertEquals(schedule.scheduleAvailability(), spec.scheduleAvailability);
        assertEquals(schedule.tripIds(), spec.tripIds.stream().map(TripId::of).collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        ScheduleSpecification spec = new ScheduleSpecification();
        spec.id = "scheduleId";
        spec.name = "schedule";;
        spec.routeId = "routeId";
        spec.scheduleAvailability = ScheduleAvailability.workingDays(LocalDate.now().minusDays(5), LocalDate.now());
        spec.tripIds = new HashSet<String>() {{
            add("trip1");
            add("trip2");
            add("trip2");
        }};

        Schedule schedule = scheduleDataMapper.fromDto(spec);


        assertEquals(spec.id, schedule.id().toString());
        assertEquals(spec.name, schedule.name());
        assertEquals(spec.routeId, schedule.routeId().toString());
        assertEquals(spec.scheduleAvailability, schedule.scheduleAvailability());
        assertEquals(spec.tripIds.stream().map(TripId::of)
                .collect(Collectors.toSet()), schedule.tripIds());
    }
}
