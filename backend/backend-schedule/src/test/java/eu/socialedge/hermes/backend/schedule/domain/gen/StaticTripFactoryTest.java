/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain.gen;

import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.transit.domain.service.Segment;
import lombok.experimental.var;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StaticTripFactoryTest {

    @Mock
    private StopFactory stopFactory;
    private Quantity<Speed> speed = Quantities.getQuantity(100, Units.METRE_PER_SECOND);
    private Quantity<Length> distance = Quantities.getQuantity(1000, Units.METRE);
    private TripFactory tripFactory;

    private Route route;

    @Before
    public void setUp() {
        val station1 = generateStation(Duration.ofMinutes(5));
        val station2 = generateStation(Duration.ofMinutes(5));
        val station3 = generateStation(Duration.ofMinutes(5));
        val station4 = generateStation(Duration.ofMinutes(5));
        val segments = new ArrayList<Segment>();
        segments.add(createSegment(station1, station2));
        segments.add(createSegment(station2, station3));
        segments.add(createSegment(station3, station4));
        route = new Route(segments);

        when(stopFactory.create(any(LocalTime.class), any(Station.class))).then(invocation -> {
            val arrival = (LocalTime) invocation.getArguments()[0];
            val station = (Station) invocation.getArguments()[1];
            return new Stop(arrival, arrival.plus(station.getDwell()), station);
        });
        tripFactory = new StaticTripFactory(stopFactory, speed);
    }

    @Test
    public void shouldSetCorrectHeadsign() {
        val startTime = LocalTime.now();
        val headsign = "hello";

        val trip = tripFactory.create(startTime, headsign, route);

        assertEquals(headsign, trip.getHeadsign());
    }

    @Test
    public void shouldCreateStopsForAllStations() {
        val startTime = LocalTime.now();

        val trip = tripFactory.create(startTime, route);

        assertEquals(route.getStations().size(), trip.getStops().size());
        for (Stop stop : trip.getStops()) {
            assertTrue(route.getStations().contains(stop.getStation()));
        }
    }

    @Test
    public void shouldCalculateCorrectArrivalAndDepartureTimes() {
        val startTime = LocalTime.now();

        val trip = tripFactory.create(startTime, route);

        val stops = new ArrayList<Stop>(trip.getStops());
        stops.sort(Comparator.comparing(Stop::getArrival));
        var arrivalTime = startTime;
        for (Stop stop : stops) {
            assertEquals(arrivalTime, stop.getArrival());
            assertEquals(arrivalTime.plus(stop.getStation().getDwell()), stop.getDeparture());
            arrivalTime = stop.getDeparture().plusSeconds(distance.divide(speed).getValue().longValue());
        }
    }

    private Segment createSegment(Station start, Station end) {
        return new Segment(start, end, distance);
    }

    private Station generateStation(Duration duration) {
        return new Station(String.valueOf(System.currentTimeMillis()), new HashSet<VehicleType>(){{add(VehicleType.BUS);}},
            Location.of(1.1, 1.1), duration);
    }
}
