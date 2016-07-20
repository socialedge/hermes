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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RouteTest {

    private final Station station1 = new Station("stationCode1", "name1", TransportType.BUS, new Position(1, 1));
    private final Station station2 = new Station("stationCode2", "name2", TransportType.BUS, new Position(2, 2));
    private final Waypoint waypoint1 = new Waypoint(station1, 1);
    private final Waypoint waypoint2 = new Waypoint(station2, 2);
    private final Route route = new Route("routeCode");

    @Before
    public void setUp() {
        route.getWaypoints().add(waypoint1);
        route.getWaypoints().add(waypoint2);
    }

    @Test
    public void testGetWaypointSuccess() {
        Optional<Waypoint> optional1 = route.getWaypoint(1);

        assertTrue(optional1.isPresent());
        assertEquals(waypoint1, optional1.get());

        Optional<Waypoint> optional2 = route.getWaypoint(2);

        assertTrue(optional2.isPresent());
        assertEquals(waypoint2, optional2.get());
    }

    @Test
    public void testAppendWaypoint() {
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.appendWaypoint(station);

        assertEquals(3, route.getWaypoints().size());
        assertEquals(3, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testAppendFirstWaypoint() {
        route.getWaypoints().removeAll(Arrays.asList(waypoint1, waypoint2));
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.appendWaypoint(station);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testPrependWaypoint() {
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.prependWaypoint(station);

        assertEquals(3, route.getWaypoints().size());
        assertEquals(1, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testPrependFirstWaypoint() {
        route.getWaypoints().removeAll(Arrays.asList(waypoint1, waypoint2));
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.appendWaypoint(station);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testInsertWaypoint() {
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.insertWaypoint(station, 2);

        assertEquals(3, route.getWaypoints().size());
        assertEquals(2, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testInsertFirstWaypoint() {
        route.getWaypoints().removeAll(Arrays.asList(waypoint1, waypoint2));
        Station station = new Station("stationCode3", "name3", TransportType.BUS, new Position(3, 3));

        Waypoint addedWaypoint = route.insertWaypoint(station, 3);

        assertEquals(1, route.getWaypoints().size());
        assertEquals(1, addedWaypoint.getPosition());
        assertEquals(station, addedWaypoint.getStation());
    }

    @Test
    public void testRemoveFirstWaypoint() {
        route.removeWaypoint(waypoint1);

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(new Waypoint(station2, 1)));
    }

    @Test
    public void testRemoveLastWaypoint() {
        route.removeWaypoint(waypoint2);

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(waypoint1));
        assertEquals(1, waypoint1.getPosition());
    }

    @Test
    public void testRemoveWaypointByPredicate() {
        route.removeWaypoint(waypoint -> waypoint.getPosition() == 2);

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(waypoint1));
        assertEquals(1, waypoint1.getPosition());
    }

    @Test
    public void testRemoveAllWaypointsByPredicate() {
        route.removeWaypoint(waypoint -> waypoint.getPosition() < 3);

        assertTrue(route.getWaypoints().isEmpty());
    }

    @Test
    public void testRemoveWaypointByStation() {
        route.removeWaypoint(waypoint2.getStation());

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(waypoint1));
        assertEquals(1, waypoint1.getPosition());
    }

    @Test
    public void testRemoveWaypointByStationCode() {
        route.removeWaypoint(waypoint1.getStation().getCodeId());

        assertEquals(1, route.getWaypoints().size());
        assertTrue(route.getWaypoints().contains(waypoint2));
    }
}
