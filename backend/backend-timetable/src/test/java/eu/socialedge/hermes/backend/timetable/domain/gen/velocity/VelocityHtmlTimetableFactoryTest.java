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
package eu.socialedge.hermes.backend.timetable.domain.gen.velocity;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.timetable.domain.gen.TimetableFactory;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.socialedge.hermes.backend.timetable.DomainTestUtils.createLine;
import static eu.socialedge.hermes.backend.timetable.DomainTestUtils.createStation;
import static eu.socialedge.hermes.backend.timetable.DomainTestUtils.createTrips;
import static org.junit.Assert.assertEquals;

public class VelocityHtmlTimetableFactoryTest {
    private static final String TEMPLATE_NAME = "test_schedule_template.txt";

    private final TimetableFactory serializer = new VelocityHtmlTimetableFactory(TEMPLATE_NAME);

    @Test
    public void shouldSerializeCorrectly() throws Exception {
        Station firstStation = createStation();
        firstStation.setName("station0");
        Station currentStation = createStation();
        currentStation.setName("station1");
        Station followingStation0 = createStation();
        followingStation0.setName("station2");
        Station followingStation1 = createStation();
        followingStation1.setName("station3");
        Station followingStation2 = createStation();
        followingStation2.setName("station4");

        List<Station> inboundStations = Arrays.asList(firstStation, currentStation, followingStation0, followingStation1, followingStation2);
        Line line = createLine(inboundStations, Arrays.asList(createStation(), createStation()));

        line.setName("name");
        line.setVehicleType(VehicleType.BUS);

        List<LocalTime> arrivals = Arrays.asList(LocalTime.of(10, 10), LocalTime.of(10, 11),
            LocalTime.of(11, 10), LocalTime.of(12, 30));
        Schedule schedule1 = new Schedule.Builder()
            .line(line)
            .inboundTrips(getTripsWithSpecificStationArrivals(arrivals, inboundStations, currentStation))
            .outboundTrips(createTrips(line.getOutboundRoute().getStations(), 4))
            .availability(Availability.workingDays(LocalDate.MIN, LocalDate.MAX))
            .build();

        arrivals =  Arrays.asList(LocalTime.of(13, 10), LocalTime.of(13, 11),
            LocalTime.of(14, 10), LocalTime.of(15, 30));
        Schedule schedule2 = new Schedule.Builder()
            .line(line)
            .inboundTrips(getTripsWithSpecificStationArrivals(arrivals, inboundStations, currentStation))
            .outboundTrips(createTrips(line.getOutboundRoute().getStations(), 4))
            .availability(Availability.workingDays(LocalDate.MIN, LocalDate.MAX))
            .build();


        String result = serializer.create(line, currentStation, Arrays.asList(schedule1, schedule2)).contentAsString();
        String expectedResult = new String(Files.readAllBytes(new File(getClass().getResource("/expected_test_result.txt").getPath()).toPath()));

        assertEquals(expectedResult, result);
    }

    private List<Trip> getTripsWithSpecificStationArrivals(List<LocalTime> arrivals, List<Station> stations, Station station) {
        List<Trip> trips = new ArrayList<>(createTrips(stations, arrivals.size()));
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            List<Stop> stops = new ArrayList<>(trip.getStops());
            stops.set(stations.indexOf(station), new Stop(arrivals.get(i), arrivals.get(i).plus(station.getDwell()), station));
            trips.set(i, new Trip(trip.getVehicleId(), stops));
        }
        return trips;
    }
}
