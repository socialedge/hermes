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
package eu.socialedge.hermes.domain.infrastructure;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouteTest {
    private Route route;

    @Before
    public void setUp() {
        String randomCode = String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
        route = new Route(randomCode);
    }


    @Test
    public void testAppendFirstWaypoint() {
        Station station = randStation();
        Waypoint appendedWaypoint = route.appendWaypoint(station);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, appendedWaypoint.getPosition());
        assertEquals(station, appendedWaypoint.getStation());
    }

    @Test
    public void testAppendWaypoint() {
        randStations(3).stream().forEach(route::appendWaypoint);

        Station station = randStation();
        Waypoint appendedWaypoint = route.appendWaypoint(station);

        assertEquals(4, route.getWaypoints().size());
        assertEquals(4, appendedWaypoint.getPosition());
        assertEquals(station, appendedWaypoint.getStation());
    }

    @Test
    public void testPrependFirstWaypoint() {
        Station station = randStation();
        Waypoint appendedWaypoint = route.prependWaypoint(station);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, appendedWaypoint.getPosition());
        assertEquals(station, appendedWaypoint.getStation());
    }

    @Test
    public void testPrependWaypoint() {
        randStations(3).stream().forEach(route::appendWaypoint);

        Station station = randStation();
        Waypoint appendedWaypoint = route.prependWaypoint(station);

        assertEquals(4, route.getWaypoints().size());
        assertEquals(1, appendedWaypoint.getPosition());
        assertEquals(station, appendedWaypoint.getStation());
    }

    @Test
    public void testGetWaypointSuccess() {
        Station station1 = randStation();
        Station station2 = randStation();
        route.appendWaypoint(station1);
        route.appendWaypoint(station2);

        Optional<Waypoint> waypoint1Opt = route.getWaypoint(1);

        assertTrue(waypoint1Opt.isPresent());
        assertEquals(station1, waypoint1Opt.get().getStation());

        Optional<Waypoint> waypoint2Opt = route.getWaypoint(2);

        assertTrue(waypoint2Opt.isPresent());
        assertEquals(station2, waypoint2Opt.get().getStation());
    }

    @Test
    public void testInsertWaypoint() {
        randStations(3).stream().forEach(route::appendWaypoint);
        
        Station station = randStation();
        Waypoint addedWaypoint = route.insertWaypoint(station, 2);

        assertEquals(4, route.getWaypoints().size());
        assertEquals(2, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testInsertFirstWaypoint() {
        Station station = randStation();
        Waypoint addedWaypoint = route.insertWaypoint(station, 3);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testRemoveFirstWaypoint() {
        Station station = randStation();
        route.appendWaypoint(station);
        route.removeWaypoint(Waypoint.of(station, 1));

        assertEquals(0, route.getWaypoints().size());
    }

    @Test
    public void testRemoveLastWaypoint() {
        Station stationFirst = randStation();
        Station stationSecond = randStation();
        
        route.appendWaypoint(stationFirst);
        route.appendWaypoint(stationSecond);

        route.removeWaypoint(Waypoint.of(stationFirst, 1)); // stationSecond.pos(2) -> 1

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(Waypoint.of(stationSecond, 1)));
    }

    @Test
    public void testRemoveWaypointByPredicate() {
        randStations(3).stream().forEach(route::appendWaypoint);

        route.removeWaypoint(waypoint -> waypoint.getPosition() == 2);

        assertEquals(2, route.getWaypoints().size());
        System.out.println(route.getWaypoints().stream().map(Waypoint::getPosition).reduce(Integer::sum).get());
        assertTrue(route.getWaypoints().stream().map(Waypoint::getPosition).reduce(Integer::sum).get() == 3);
    }

    @Test
    public void testRemoveAllWaypointsByPredicate() {
        randStations(2).stream().forEach(route::appendWaypoint);
        route.removeWaypoint(waypoint -> waypoint.getPosition() < 3);

        assertTrue(route.getWaypoints().isEmpty());
    }

    @Test
    public void testRemoveWaypointByStation() {
        Station station = randStation();
        route.appendWaypoint(station);

        route.removeWaypoint(station);

        assertTrue(route.getWaypoints().isEmpty());
    }

    @Test
    public void testRemoveWaypointByStationCode() {
        Station station = randStation();
        route.appendWaypoint(station);

        route.removeWaypoint(station.getCodeId());

        assertTrue(route.getWaypoints().isEmpty());
    }

    @Test
    public void testGetFirstWaypoint() {
        Station station = randStation();
        route.appendWaypoint(station);
        route.appendWaypoint(randStation());

        assertTrue(route.getFirstWaypoint().isPresent());
        assertEquals(station, route.getFirstWaypoint().get().getStation());
    }

    @Test
    public void testGetLastWaypoint() {
        route.appendWaypoint(randStation());

        Station station = randStation();
        route.appendWaypoint(station);

        assertTrue(route.getLastWaypoint().isPresent());
        assertEquals(station, route.getLastWaypoint().get().getStation());
    }

    private Station randStation() {
        String randomCode = String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
        String randomName = String.valueOf(ThreadLocalRandom.current().nextInt(100, 10000));
        float randomLatitude = ThreadLocalRandom.current().nextLong(-90, 90);
        float randomLongitude = ThreadLocalRandom.current().nextLong(-180, 180);

        return new Station(randomCode, randomName, TransportType.BUS, new Position(randomLatitude, randomLongitude));
    }

    private Collection<Station> randStations(int amount) {
        List<Station> stations = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) stations.add(randStation());

        return stations;
    }
}
