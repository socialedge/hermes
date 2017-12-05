/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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
 *
 */
package eu.socialedge.hermes.backend.gen.serialization.velocity;

import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createLine;
import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createRoute;
import static eu.socialedge.hermes.backend.gen.DomainTestUtils.createSchedule;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.val;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Comparators;

import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;

public class StationScheduleTemplateTest {

    private Line line;
    private Station station;
    private List<Schedule> schedules = new ArrayList<>();

    @Before
    public void setUp() {
        line = createLine(9, 8);
        station = line.getInboundRoute().getStations().get(4);

        schedules.add(createSchedule(line, 10));
        schedules.add(createSchedule(line, 8));
    }

    @Test
    public void shouldSetLineNameFromLine() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        assertEquals(line.getName(), template.getLineName());
    }

    @Test
    public void shouldSetVehicleTypeFromLine() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        assertEquals(line.getVehicleType().toString(), template.getVehicleType());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfStationIsNotInTheLine() {
        val inboundStations = line.getInboundRoute().getStations();
        inboundStations.remove(station);
        line.setInboundRoute(createRoute(inboundStations));
        val outboundStations = line.getInboundRoute().getStations();
        outboundStations.remove(station);
        line.setInboundRoute(createRoute(outboundStations));

        StationScheduleTemplate.from(line, station, schedules);
    }

    @Test
    public void shouldChooseInboundRouteFromLineWhenSpecifiedStationIsThere() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        assertEquals(line.getInboundRoute().getHead().getName(), template.getFirstStation());
    }

    @Test
    public void shouldUseSpecifiedStationAsCurrent() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        assertEquals(station.getName(), template.getCurrentStation());
    }

    @Test
    public void shouldUseAllStationsButSpecifiedForFollowingStations() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        val expectedFollowingStations = line.getInboundRoute().getStations().stream()
            .skip(line.getInboundRoute().getStations().indexOf(station) + 1)
            .map(Station::getName)
            .collect(Collectors.toList());
        assertEquals(expectedFollowingStations, template.getFollowingStations());
    }

    @Test
    public void shouldCreateScheduleTemplateForEachSchedule() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        assertEquals(schedules.size(), template.getSchedules().size());
    }

    @Test
    public void shouldSortTimesAscending() {
        val template = StationScheduleTemplate.from(line, station, schedules);

        val scheduleData = template.getSchedules().get(0);
        assertTrue(Comparators.isInOrder(scheduleData.getTimes().keySet(), Integer::compareTo));
        for (List<Integer> minutes : scheduleData.getTimes().values()) {
            assertTrue(Comparators.isInOrder(minutes, Integer::compareTo));
        }
    }

}
