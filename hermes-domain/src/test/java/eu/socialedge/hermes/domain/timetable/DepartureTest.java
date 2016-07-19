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

import eu.socialedge.hermes.domain.infrastructure.Position;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.TransportType;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class DepartureTest {

    private static final Station station = new Station("stationCode", "Station", TransportType.BUS, new Position(0, 0));

    @Test
    public void testConstructorNotNullParamsSuccess() {
        Departure departure = new Departure(station, LocalTime.NOON);
        assertEquals(station, departure.getStation());
        assertEquals(LocalTime.NOON, departure.getTime());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullStation() {
        new Departure(null, LocalTime.NOON);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullTime() {
        new Departure(station, null);
    }

    @Test
    public void testOfSuccess() {
        Departure departureConstructor = new Departure(station, LocalTime.NOON);
        Departure departureOf = Departure.of(station, LocalTime.NOON);
        assertEquals(departureConstructor, departureOf);
    }

}
