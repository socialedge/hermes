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

import eu.socialedge.hermes.domain.infrastructure.StationId;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TripTest {

    private Set<Stop> stops = new HashSet<>();
    private Trip trip;

    @Before
    public void setUp() {
        stops.add(randomStop());
        stops.add(randomStop());
        stops.add(randomStop());

        trip = new Trip(stops);
    }

    @Test
    public void testAddStopShouldContainAddedStop() {
        Stop stopToAdd = randomStop();

        trip.addStop(stopToAdd);

        assertTrue(stops.contains(stopToAdd));
    }

    @Test
    public void testRemoveStopShouldNotContainRemovedStop() {
        Stop stopToRemove = randomStop();
        stops.add(stopToRemove);

        trip.removeStop(stopToRemove);

        assertFalse(stops.contains(stopToRemove));
    }

    @Test
    public void testHasStopShouldReturnTrueIfContains() {
        Stop stop = randomStop();
        stops.add(stop);

        assertTrue(trip.hasStop(stop));
    }

    @Test
    public void testHasStopShouldReturnFalseIfNotContains() {
        assertFalse(trip.hasStop(randomStop()));
    }

    @Test
    public void testRemoveAllStopsShouldRemoveAllStops() {
        trip.removeAllStops();

        assertTrue(stops.isEmpty());
    }

    @Test
    public void testSizeShouldRemoveCountOfStops() {
        assertEquals(stops.size(), trip.size());
    }

    @Test
    public void testIsEmptyShouldReturnTrueIfEmpty() {
        stops.clear();

        assertTrue(trip.isEmpty());
    }

    @Test
    public void testIsEmptyShouldReturnFalseIfNotEmpty() {
        assertFalse(trip.isEmpty());
    }


    private Stop randomStop() {
        StationId stationId = StationId.of("stationId" + ThreadLocalRandom.current().nextInt());
        LocalTime arrival = LocalTime.now().minusMinutes(ThreadLocalRandom.current().nextInt());
        LocalTime departure = LocalTime.now().minusMinutes(ThreadLocalRandom.current().nextInt());
        return new Stop(stationId, arrival, departure);
    }
}
