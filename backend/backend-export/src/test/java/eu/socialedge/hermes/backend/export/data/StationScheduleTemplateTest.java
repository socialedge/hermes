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
package eu.socialedge.hermes.backend.export.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Comparators;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.provider.Agency;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;

public class StationScheduleTemplateTest {

    private Line line;
    private Station station;
    private List<Schedule> schedules = new ArrayList<>();

    @Before
    public void setUp() {
        station = createStation();
        line = createLine(Arrays.asList(createStation(), createStation(), createStation(), createStation(), station,
                createStation(), createStation(), createStation(), createStation()),
            Arrays.asList(createStation(), createStation(), createStation(), createStation(),
                createStation(), createStation(), createStation(), createStation()));

        Schedule schedule1 = new Schedule.Builder()
            .availability(Availability.workingDays(LocalDate.MIN, LocalDate.MAX))
            .line(line)
            .inboundTrips(createTrips(line.getInboundRoute().getStations(), 10))
            .outboundTrips(createTrips(line.getOutboundRoute().getStations(), 10))
            .build();

        Schedule schedule2 = new Schedule.Builder()
            .availability(Availability.weekendDays(LocalDate.MIN, LocalDate.MAX))
            .line(line)
            .inboundTrips(createTrips(line.getInboundRoute().getStations(), 8))
            .outboundTrips(createTrips(line.getOutboundRoute().getStations(), 8))
            .build();

        schedules.add(schedule1);
        schedules.add(schedule2);
    }

    @Test
    public void shouldSetLineNameFromLine() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        assertEquals(line.getName(), template.getLineName());
    }

    @Test
    public void shouldSetVehicleTypeFromLine() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        assertEquals(line.getVehicleType().toString(), template.getVehicleType());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfStationIsNotInTheLine() {
        List<Station> inboundStations = line.getInboundRoute().getStations();
        inboundStations.remove(station);
        line.setInboundRoute(createRoute(inboundStations));
        List<Station> outboundStations = line.getInboundRoute().getStations();
        outboundStations.remove(station);
        line.setInboundRoute(createRoute(outboundStations));

        StationScheduleTemplate.create(line, station, schedules);
    }

    @Test
    public void shouldChooseInboudRouteFromLineWhenSpecifiedStationIsThere() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        assertEquals(line.getInboundRoute().getHead().getName(), template.getFirstStation());
    }

    @Test
    public void shouldUseSpecifiedStationAsCurrent() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        assertEquals(station.getName(), template.getCurrentStation());
    }

    @Test
    public void shouldUseAllStationsButSpecifiedForFollowingStations() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        List<String> expectedFollowingStations = line.getInboundRoute().getStations().stream()
            .skip(line.getInboundRoute().getStations().indexOf(station) + 1)
            .map(Station::getName)
            .collect(Collectors.toList());
        assertEquals(expectedFollowingStations, template.getFollowingStations());
    }

    @Test
    public void shouldCreateScheduleTemplateForEachSchedule() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        assertEquals(schedules.size(), template.getSchedules().size());
    }

    @Test
    public void shouldFormatAvailabilityAsShortDaysSeparatedWithComma() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        ScheduleTemplateData scheduleTemplateDataWeekDays = template.getSchedules().get(0);
        assertEquals("ПН, ВТ, СР, ЧТ, ПТ", scheduleTemplateDataWeekDays.getAvailability());

        ScheduleTemplateData scheduleTemplateDataWeekend = template.getSchedules().get(1);
        assertEquals("СБ, НД", scheduleTemplateDataWeekend.getAvailability());
    }

    @Test
    public void shouldSortTimesAscending() {
        StationScheduleTemplate template = StationScheduleTemplate.create(line, station, schedules);

        ScheduleTemplateData scheduleData = template.getSchedules().get(0);
        assertTrue(Comparators.isInOrder(scheduleData.getTimes().keySet(), Integer::compareTo));
        for (List<Integer> minutes : scheduleData.getTimes().values()) {
            assertTrue(Comparators.isInOrder(minutes, Integer::compareTo));
        }
    }

    private Station createStation() {
        return new Station.Builder()
            .name(UUID.randomUUID().toString())
            .vehicleType(VehicleType.TROLLEYBUS)
            .location(Location.of(1.0, 1.0))
            .dwell(Duration.ofSeconds(10))
            .build();
    }

    private Line createLine(List<Station> inboundStations, List<Station> outboundStations) {
        return new Line.Builder()
            .name(UUID.randomUUID().toString())
            .vehicleType(VehicleType.TROLLEYBUS)
            .inboundRoute(createRoute(inboundStations))
            .outboundRoute(createRoute(outboundStations))
            .agency(new Agency("name"))
            .build();
    }

    private Route createRoute(List<Station> stations) {
        return Route.of(formSegments(stations));
    }

    private List<Segment> formSegments(List<Station> stations) {
        List<Segment> segments = new ArrayList<>();
        for (int i = 0; i < stations.size() - 1; i++) {
            segments.add(new Segment(stations.get(i), stations.get(i + 1)));
        }
        return segments;
    }

    private List<Trip> createTrips(List<Station> stations, int count) {
        List<Trip> trips = new ArrayList<>();
        LocalTime localTime = LocalTime.now();
        for (int i = 0; i < count; i++) {
            List<Stop> stops = new ArrayList<>();
            for (Station station : stations) {
                stops.add(new Stop(localTime.plusMinutes(i), localTime.plusMinutes(i).plusSeconds(10), station));
            }
            trips.add(new Trip(i, stops));
            localTime = localTime.plusMinutes(10);
        }
        return trips;
    }
}
