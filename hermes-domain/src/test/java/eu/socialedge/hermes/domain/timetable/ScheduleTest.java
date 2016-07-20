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

import eu.socialedge.hermes.domain.infrastructure.*;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScheduleTest {
    private final Station station1 = new Station("stationCode1", "name1", TransportType.BUS, new Position(1, 1));
    private final Station station2 = new Station("stationCode2", "name2", TransportType.BUS, new Position(2, 2));
    private final Departure departure1 = new Departure(station1, LocalTime.NOON);
    private final Departure departure2 = new Departure(station2, LocalTime.NOON);
    private final Route route = new Route("routeCode");
    private final Schedule schedule = new Schedule("Name", route);

    @Before
    public void setUp() {
        schedule.getDepartures().add(departure1);
        schedule.getDepartures().add(departure2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetExpirationDateBeforeCreationDate() {
        schedule.setExpirationDate(LocalDate.now().minusDays(1));
    }

    @Test
    public void testSetExpirationDateSuccess() {
        LocalDate expirationDate = LocalDate.now().plusDays(1);

        schedule.setExpirationDate(expirationDate);

        assertEquals(expirationDate, schedule.getExpirationDate());
    }

    @Test
    public void testAddDepartureSuccess() {
        schedule.getRoute().appendWaypoint(station1);

        assertTrue(schedule.getDepartures().contains(departure1));
    }

    @Test(expected = NullPointerException.class)
    public void testAddDepartureNullStation() {
        schedule.addDeparture(new Departure(null, LocalTime.NOON));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDepartureWithNotExistingStation() {
        schedule.getRoute().appendWaypoint(station1);

        schedule.addDeparture(new Departure(new Station("newStation", "newName", TransportType.TRAM, new Position(1, 2)),
                LocalTime.NOON));
    }

    @Test
    public void testRemoveDeparture() {
        schedule.removeDeparture(departure1);

        assertEquals(1, schedule.getDepartures().size());
        assertTrue(schedule.getDepartures().contains(departure2));
    }

    @Test
    public void testRemoveDepartureByCorrectPredicateOnStation() {
        schedule.removeDeparture(dep -> station1.equals(dep.getStation()));

        assertEquals(1, schedule.getDepartures().size());
        assertTrue(schedule.getDepartures().contains(departure2));
    }

    @Test
    public void testRemoveDepartureByCorrectPredicateOnTime() {
        int removed = schedule.removeDeparture(dep -> LocalTime.NOON.equals(dep.getTime()));

        assertEquals(removed, 2);
        assertTrue(schedule.getDepartures().isEmpty());
    }

    @Test
    public void testRemoveDepartureByStation() {
        schedule.removeDeparture(station1);

        assertEquals(1, schedule.getDepartures().size());
        assertTrue(schedule.getDepartures().contains(departure2));
    }

    @Test
    public void testRemoveDepartureByStationCode() {
        schedule.removeDeparture(station1.getCodeId());

        assertEquals(1, schedule.getDepartures().size());
        assertTrue(schedule.getDepartures().contains(departure2));
    }

    @Test
    public void testSetDeparturesSuccess() {
        schedule.getRoute().appendWaypoint(station1);
        schedule.getRoute().appendWaypoint(station2);

        schedule.setDepartures(Arrays.asList(departure1, departure2));

        assertEquals(2, schedule.getDepartures().size());
        assertTrue(schedule.getDepartures().contains(departure1));
        assertTrue(schedule.getDepartures().contains(departure2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDeparturesNotExistingStation() {
        schedule.getRoute().appendWaypoint(station1);

        schedule.setDepartures(Arrays.asList(departure1, departure2));
    }
}
